package gamein2.team.services.auth;


import gamein2.team.kernel.dto.result.RegisterAndLoginResultDTO;
import gamein2.team.kernel.entity.User;
import gamein2.team.kernel.exceptions.BadRequestException;
import gamein2.team.kernel.exceptions.InvalidTokenException;
import gamein2.team.kernel.exceptions.UserAlreadyExist;
import gamein2.team.kernel.exceptions.UserNotFoundException;
import gamein2.team.kernel.iao.AuthInfo;

public interface AuthService {
    RegisterAndLoginResultDTO login(String email, String phone, String password) throws UserNotFoundException, BadRequestException;
    RegisterAndLoginResultDTO register(String phone, String email, String password,String persianName,
                                       String persianLastName) throws BadRequestException, UserAlreadyExist;
    User getUserById(Long userId) throws BadRequestException;
    void forgotPassword(String email) throws UserNotFoundException;
    void forgotPasswordSMS(String phone) throws UserNotFoundException;
    void resetPassword(String code, String password) throws BadRequestException;
    AuthInfo extractAuthInfoFromToken(String token) throws InvalidTokenException;
}