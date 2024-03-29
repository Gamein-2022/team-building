package gamein2.team.services.team;

import gamein2.team.kernel.dto.request.ProfileInfoRequestDTO;
import gamein2.team.kernel.dto.result.*;
import gamein2.team.kernel.entity.*;
import gamein2.team.kernel.exceptions.BadRequestException;
import gamein2.team.kernel.exceptions.UnauthorizedException;
import gamein2.team.kernel.exceptions.UserNotFoundException;
import gamein2.team.kernel.repos.TeamOfferRepository;
import gamein2.team.kernel.repos.TeamRepository;
import gamein2.team.kernel.repos.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service
public class TeamServiceHandler implements TeamService {
    private final UserRepository userRepository;
    private final TeamOfferRepository teamOfferRepository;
    private final TeamRepository teamRepository;

    public TeamServiceHandler(UserRepository userRepository, TeamOfferRepository teamOfferRepository, TeamRepository teamRepository) {
        this.userRepository = userRepository;
        this.teamOfferRepository = teamOfferRepository;
        this.teamRepository = teamRepository;
    }


    @Override
    public ProfileInfoDTO getProfile(User user) {
        return user.toProfileDTO();
    }

    @Override
    public ProfileInfoDTO updateProfile(User user, ProfileInfoRequestDTO newProfile) throws BadRequestException {
        user.setPersianName(newProfile.getPersianName());
        user.setPersianSurname(newProfile.getPersianSurname());
        user.setEnglishName(newProfile.getEnglishName());
        user.setEnglishSurname(newProfile.getEnglishSurname());
        user.setGender(newProfile.getGender());
        user.setDob(newProfile.getDob());
        user.setEducation(newProfile.getEducation());
        user.setSchool(newProfile.getSchool());
        user.setMajor(newProfile.getMajor());
        user.setYearOfEntrance(newProfile.getYearOfEntrance());
        user.setProvince(newProfile.getProvince());
        user.setCity(newProfile.getCity());
        user.setIntroductionMethod(newProfile.getIntroductionMethod());
        user.setName(newProfile.getUsername());

        try {
            return userRepository.save(user).toProfileDTO();
        } catch (Exception e) {

            throw new BadRequestException("این نام کاربری قبلا استفاده شده است .");

        }

    }

    @Override
    public List<UserDTO> getUsers(User user) throws BadRequestException {
        checkProfileCompletion(user);
        return userRepository.findAllByIdNotAndTeamIsNull(user.getId()).stream().filter(User::isComplete)
                .map(User::toDTO).collect(Collectors.toList());
    }

    @Override
    public TeamOfferDTO requestTeamJoin(Team team, User user, Long userId) throws UnauthorizedException,
            BadRequestException, UserNotFoundException {
        checkProfileCompletion(user);
        validateTeamAccess(team, user);
        if (userId.equals(user.getId())) {
            throw new BadRequestException("شما نمی‌توانید به خود درخواست بدهید!");
        }

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new BadRequestException("کاربر درخواست شده وجود ندارد!");
        }
        if (userOptional.get().getTeam() != null)
            throw new BadRequestException("شما نمی‌توانید به کابری که تیم دارد درخواست بدهید");
        List<TeamOffer> offers = teamOfferRepository.findAllByTeamIdAndUserIdAndDeclinedIsFalse(team.getId(), userOptional.get().getId());
        if (offers.size() > 0) {
            throw new BadRequestException("شما قبلا به این کاربر درخواست داده‌اید!");
        }


        TeamOffer offer = new TeamOffer();
        offer.setTeam(team);
        offer.setUser(userOptional.get());
        offer.setDeclined(false);
        return teamOfferRepository.save(offer).toDTO();
    }

    @Override
    public List<TeamOfferDTO> getMyOffers(User user) throws BadRequestException {
        checkProfileCompletion(user);
        if (user.getTeam() == null) {

            return teamOfferRepository.findAllByUser_IdAndDeclinedIsFalse(user.getId()).stream().map(TeamOffer::toDTO).collect(Collectors.toList());
        } else
            return new ArrayList<>();
    }

    @Override
    public List<TeamOfferDTO> getTeamOffers(Team team, User user) throws BadRequestException,
            UnauthorizedException, UserNotFoundException {
        checkProfileCompletion(user);
        validateTeamAccess(team, user);
        return teamOfferRepository.findAllByTeamIdAndDeclinedIsFalse(team.getId()).stream().map(TeamOffer::toDTO).collect(Collectors.toList());
    }

    @Override
    public TeamInfoResultDTO acceptOffer(User user, Long offerId) throws BadRequestException {
        checkProfileCompletion(user);
        Optional<TeamOffer> teamOfferOptional = teamOfferRepository.findById(offerId);
        if (teamOfferOptional.isEmpty()) {
            throw new BadRequestException("درخواست اضافه شدن به تیم یافت نشد!");
        }
        TeamOffer offer = teamOfferOptional.get();
        if (!user.getId().equals(offer.getUser().getId())) {
            throw new BadRequestException("درخواست اضافه شدن به تیم یافت نشد!");
        }
        if (user.getTeam() != null) {
            throw new BadRequestException("شما تیم دارید!");
        }
        Team team = offer.getTeam();
        if (team.getUsers().size() >= 3) {
            throw new BadRequestException("ظرفیت این تیم تکمیل است!");
        }
        team.getUsers().add(user);
        offer.setDeclined(true);
        user.setTeam(team);

        teamRepository.save(team);
        userRepository.save(user);
        return new TeamInfoResultDTO(team.getName(),
                team.getUsers().stream().map(User::userDTO).collect(Collectors.toList()),
                false);
    }

    @Override
    @Transactional
    public ProfileInfoDTO leaveTeam(User user) throws BadRequestException {
        checkProfileCompletion(user);
        Team team = user.getTeam();
        if (team == null) {
            throw new BadRequestException("شما تیم ندارید!");
        }
        if (user.getId().equals(team.getOwner().getId())) {
            team.getUsers().forEach(u -> {
                u.setTeam(null);
            });
            teamOfferRepository.deleteAllByTeam_Id(team.getId());
            teamRepository.delete(team);
            userRepository.saveAll(team.getUsers());
        } else {
            team.getUsers().remove(user);
            teamRepository.save(team);
            user.setTeam(null);
            userRepository.save(user);
        }
        return user.toProfileDTO();
    }

    @Override
    public TeamInfoResultDTO getTeamInfo(Team team, User user) throws BadRequestException {
        //checkProfileCompletion(user);
        return new TeamInfoResultDTO(
                team == null ? null : team.getName(),
                team == null ? null : team.getUsers().stream().map(User::userDTO).collect(Collectors.toList()),
                team == null ? null : user.getId().equals(team.getOwner().getId()));
    }

    @Override
    public TeamInfoResultDTO createTeam(User user, String teamName) throws BadRequestException {
        checkProfileCompletion(user);
        if (teamName == null || teamName.isEmpty()) {
            throw new BadRequestException("نام تیم نباید خالی باشد!");
        }

        Team team = new Team();
        team.setName(teamName);
        team.setUsers(new ArrayList<>());
        team.getUsers().add(user);
        team.setOwner(user);

        try {
            teamRepository.save(team);
        } catch (Exception e) {
            throw new BadRequestException("این نام برای تیم دیگری است.");
        }

        user.setTeam(team);

        userRepository.save(user);

        return new TeamInfoResultDTO(team.getName(),
                team.getUsers().stream().map(User::userDTO).collect(Collectors.toList()),
                true);
    }

    @Override
    public List<TeamOfferDTO> cancelOffer(User user, Long offerId) throws BadRequestException, UnauthorizedException, UserNotFoundException {
        Optional<TeamOffer> offerOptional = teamOfferRepository.findById(offerId);
        if (offerOptional.isEmpty()) {
            throw new BadRequestException("درخواست اضافه شدن به تیم یافت نشد!");
        }
        TeamOffer teamOffer = offerOptional.get();
        if (!user.getId().equals(teamOffer.getTeam().getOwner().getId())) {
            throw new BadRequestException("درخواست اضافه شدن به تیم یافت نشد!");
        }
        teamOfferRepository.delete(teamOffer);
        return getTeamOffers(teamOffer.getTeam(), user);
    }

    @Override
    public List<TeamOfferDTO> declineOffer(User user, Long offerId) throws BadRequestException {
        Optional<TeamOffer> offerOptional = teamOfferRepository.findById(offerId);
        if (offerOptional.isEmpty()) {
            throw new BadRequestException("درخواست اضافه شدن به تیم یافت نشد!");
        }
        TeamOffer teamOffer = offerOptional.get();
        if (!user.getId().equals(teamOffer.getUser().getId())) {
            throw new BadRequestException("درخواست اضافه شدن به تیم یافت نشد!");
        }
        teamOffer.setDeclined(true);
        teamOfferRepository.save(teamOffer);
        return getMyOffers(user);
    }

    private void validateTeamAccess(Team team, User user) throws BadRequestException, UnauthorizedException, UserNotFoundException {
        checkProfileCompletion(user);
        if (team == null) {
            throw new UserNotFoundException("شما تیمی ندارید!");
        }
        if (!team.getOwner().getId().equals(user.getId())) {
            throw new UnauthorizedException("شما اجازه‌ی این کار را ندارید!");
        }
    }

    private void checkProfileCompletion(User user) throws BadRequestException {
        if (user.getCity() == null || user.getDob() == null || user.getEducation() == null || user.getGender() == null
                || user.getEnglishName() == null || user.getEnglishSurname() == null || user.getPersianName() == null
                || user.getPersianSurname() == null || user.getMajor() == null || user.getName() == null
                || user.getIntroductionMethod() == null || user.getProvince() == null || user.getSchool() == null
                || user.getYearOfEntrance() == null) {
            throw new BadRequestException("پروفایل شما کامل نیست!");
        }
    }


/*    @Scheduled(fixedDelay = 10, timeUnit = TimeUnit.DAYS)
    @Transactional
    void a() {
        List<Team> teams = teamRepository.getALoneTeams();
        List<User> users = userRepository.getALoneUsers();
        for (int i = 0; i < teams.size(); ++i) {
            teams.get(i).getUsers().add(users.get(i));
            users.get(i).setTeam(teams.get(i));
        }
        int b = users.size() - teams.size();
        int c = teams.size();
        for (int i = 0; i * 2 < b; ++i) {
            Team team = new Team();
            team.setName("Gamein " + i);
            List<User> list = new ArrayList<>();
            team.setUsers(list);
            team.getUsers().add(users.get(c + 2 * i));
            team.getUsers().add(users.get(c + 2 * i + 1));
            team.setOwner(users.get(c + 2 * i));
            users.get(c + 2 * i).setTeam(team);
            users.get(c + 2 * i + 1).setTeam(team);
            teams.add(team);
        }
        teamRepository.saveAll(teams);
        userRepository.saveAll(users);
        List<User> users = userRepository.getBad();
        List<Team> teams = new ArrayList<>();
        int a = 100000;

        Team team = new Team();
        team.setUsers(new ArrayList<>());
        team.setId((long) a);
        team.getUsers().add(users.get(0));
        team.getUsers().add(users.get(1));
        team.setOwner(users.get(0));
        team.setName("Gamein " + a);
        users.get(0).setTeam(team);
        users.get(1).setTeam(team);

        teams.add(team);

        a += 1;


        for (int i = 2; i < users.size(); i = i + 3){
            Team newTeam = new Team();
            newTeam.setId((long) a);
            User user1 = users.get(i);
            User user2 = users.get(i + 1);
            User user3 = users.get(i + 2);
            newTeam.setOwner(user1);
            newTeam.setUsers(new ArrayList<>());
            newTeam.getUsers().add(user1);
            newTeam.getUsers().add(user2);
            newTeam.getUsers().add(user3);
            newTeam.setName("Gamein " + a);
            a += 1;
            user1.setTeam(newTeam);
            user2.setTeam(newTeam);
            user3.setTeam(newTeam);
            teams.add(newTeam);
        }

        teamRepository.saveAll(teams);
        userRepository.saveAll(users);


        System.out.println("Done");
    }*/


}
