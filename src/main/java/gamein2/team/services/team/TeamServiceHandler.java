package gamein2.team.services.team;

import gamein2.team.kernel.dto.result.*;
import gamein2.team.kernel.entity.*;
import gamein2.team.kernel.exceptions.BadRequestException;
import gamein2.team.kernel.exceptions.UnauthorizedException;
import gamein2.team.kernel.repos.TeamOfferRepository;
import gamein2.team.kernel.repos.TeamRepository;
import gamein2.team.kernel.repos.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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
    public List<UserDTO> getUsers(User user) {
        return userRepository.findAllByIdNot(user.getId()).stream().map(User::toDTO).collect(Collectors.toList());
    }

    @Override
    public TeamOfferDTO requestTeamJoin(Team team, User user, Long userId) throws UnauthorizedException,
            BadRequestException {
        validateTeamAccess(team, user);
        if (userId.equals(user.getId())) {
            throw new BadRequestException("شما نمی‌توانید به خود درخواست بدهید!");
        }

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new BadRequestException("کاربر درخواست شده وجود ندارد!");
        }
        Optional<TeamOffer> offerOptional = teamOfferRepository.findByTeam_IdAndUser_Id(team.getId(),
                userOptional.get().getId());
        if (offerOptional.isPresent()) {
            throw new BadRequestException("شما قبلا به این کاربر درخواست داده‌اید!");
        }

        TeamOffer offer = new TeamOffer();
        offer.setTeam(team);
        offer.setUser(userOptional.get());
        return teamOfferRepository.save(offer).toDTO();
    }

    @Override
    public List<TeamOfferDTO> getMyOffers(User user) {
        return teamOfferRepository.findAllByUser_Id(user.getId()).stream().map(TeamOffer::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<TeamOfferDTO> getTeamOffers(Team team, User user) throws BadRequestException,
            UnauthorizedException {
        validateTeamAccess(team, user);
        return teamOfferRepository.findAllByTeam_Id(team.getId()).stream().map(TeamOffer::toDTO).collect(Collectors.toList());
    }

    @Override
    public TeamInfoResultDTO acceptOffer(User user, Long offerId) throws BadRequestException {
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
        user.setTeam(team);
        teamRepository.save(team);
        userRepository.save(user);
        return new TeamInfoResultDTO(team.getName(),
                team.getUsers().stream().map(User::toDTO).collect(Collectors.toList()),
                false);
    }

    @Override
    public ProfileInfoResultDTO leaveTeam(User user) throws BadRequestException {
        Team team = user.getTeam();
        if (team == null) {
            throw new BadRequestException("شما تیم ندارید!");
        }
        if (user.getId().equals(team.getOwner().getId())) {
            team.getUsers().forEach(u -> {
                u.setTeam(null);
            });
            userRepository.saveAll(team.getUsers());
        } else {
            team.setUsers(team.getUsers().stream().filter(u -> u.getId().equals(user.getId())).toList());
            user.setTeam(null);
            teamRepository.save(team);
            userRepository.save(user);
        }
        return new ProfileInfoResultDTO(user.getEnglishName(), user.getPersianName());
    }

    private void validateTeamAccess(Team team, User user) throws BadRequestException, UnauthorizedException {
        if (team == null) {
            throw new BadRequestException("شما تیمی ندارید!");
        }
        if (!team.getOwner().getId().equals(user.getId())) {
            throw new UnauthorizedException("شما اجازه‌ی این کار را ندارید!");
        }
    }
}
