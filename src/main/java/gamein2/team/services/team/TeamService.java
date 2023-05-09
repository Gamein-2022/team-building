package gamein2.team.services.team;

import gamein2.team.kernel.dto.request.ProfileInfoRequestDTO;
import gamein2.team.kernel.dto.result.ProfileInfoDTO;
import gamein2.team.kernel.dto.result.TeamInfoResultDTO;
import gamein2.team.kernel.dto.result.TeamOfferDTO;
import gamein2.team.kernel.dto.result.UserDTO;
import gamein2.team.kernel.entity.Team;
import gamein2.team.kernel.entity.User;
import gamein2.team.kernel.exceptions.BadRequestException;
import gamein2.team.kernel.exceptions.UnauthorizedException;
import gamein2.team.kernel.exceptions.UserNotFoundException;


import java.util.List;


public interface TeamService {
    ProfileInfoDTO getProfile(User user);
    ProfileInfoDTO updateProfile(User user, ProfileInfoRequestDTO newProfile) throws BadRequestException;
    List<UserDTO> getUsers(User user) throws BadRequestException;
    TeamOfferDTO requestTeamJoin(Team team, User owner, Long userId) throws UnauthorizedException, BadRequestException, UserNotFoundException;
    List<TeamOfferDTO> getMyOffers(User user) throws BadRequestException;
    List<TeamOfferDTO> getTeamOffers(Team team, User user) throws BadRequestException, UnauthorizedException, UserNotFoundException;
    TeamInfoResultDTO acceptOffer(User user, Long offerId) throws BadRequestException;
    ProfileInfoDTO leaveTeam(User user) throws BadRequestException;
    TeamInfoResultDTO getTeamInfo(Team team, User user) throws BadRequestException;
    TeamInfoResultDTO createTeam(User user, String name) throws BadRequestException;
    List<TeamOfferDTO> cancelOffer(User user, Long offerId) throws BadRequestException, UnauthorizedException, UserNotFoundException;
    List<TeamOfferDTO> declineOffer(User user, Long offerId) throws BadRequestException;
}
