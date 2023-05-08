package gamein2.team.services.team;

import gamein2.team.kernel.dto.result.ProfileInfoDTO;
import gamein2.team.kernel.dto.result.TeamInfoResultDTO;
import gamein2.team.kernel.dto.result.TeamOfferDTO;
import gamein2.team.kernel.dto.result.UserDTO;
import gamein2.team.kernel.entity.Team;
import gamein2.team.kernel.entity.User;
import gamein2.team.kernel.exceptions.BadRequestException;
import gamein2.team.kernel.exceptions.UnauthorizedException;


import java.util.List;


public interface TeamService {
    ProfileInfoDTO getProfile(User user);
    ProfileInfoDTO updateProfile(User user, ProfileInfoDTO newProfile);
    List<UserDTO> getUsers(User user) throws BadRequestException;
    TeamOfferDTO requestTeamJoin(Team team, User owner, Long userId) throws UnauthorizedException, BadRequestException;
    List<TeamOfferDTO> getMyOffers(User user) throws BadRequestException;
    List<TeamOfferDTO> getTeamOffers(Team team, User user) throws BadRequestException, UnauthorizedException;
    TeamInfoResultDTO acceptOffer(User user, Long offerId) throws BadRequestException;
    ProfileInfoDTO leaveTeam(User user) throws BadRequestException;
}
