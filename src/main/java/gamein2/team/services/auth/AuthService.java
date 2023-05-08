package gamein2.team.services.auth;


import gamein2.team.kernel.dto.result.RegisterAndLoginResultDTO;
import gamein2.team.kernel.entity.User;
import gamein2.team.kernel.exceptions.BadRequestException;
import gamein2.team.kernel.exceptions.UserAlreadyExist;
import gamein2.team.kernel.exceptions.UserNotFoundException;

public interface AuthService {
    RegisterAndLoginResultDTO login(String email, String phone, String password) throws UserNotFoundException, BadRequestException;
    RegisterAndLoginResultDTO register(String phone, String email, String password) throws BadRequestException, UserAlreadyExist;
    User getUserById(Long userId) throws BadRequestException;
    void forgotPassword(String email) throws UserNotFoundException;
    void forgotPasswordSMS(String phone) throws UserNotFoundException;
    void resetPassword(String code, String password) throws BadRequestException;
}