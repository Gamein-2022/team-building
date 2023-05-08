package gamein2.team.services.auth;

import com.google.common.collect.Lists;
import gamein2.team.kernel.dto.result.RegisterAndLoginResultDTO;
import gamein2.team.kernel.entity.User;
import gamein2.team.kernel.exceptions.BadRequestException;
import gamein2.team.kernel.exceptions.InvalidTokenException;
import gamein2.team.kernel.exceptions.UserAlreadyExist;
import gamein2.team.kernel.exceptions.UserNotFoundException;
import gamein2.team.kernel.repos.UserRepository;
import gamein2.team.kernel.util.JwtUtils;
import it.ozimov.springboot.mail.model.Email;
import it.ozimov.springboot.mail.model.defaultimpl.DefaultEmail;
import it.ozimov.springboot.mail.service.EmailService;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Optional;


@Service
public class AuthServiceHandler implements AuthService {

    private final UserRepository userRepository;
    @Autowired
    public EmailService emailService;
    @Value("${spring.mail.username}")
    private String sender;


    public AuthServiceHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public RegisterAndLoginResultDTO login(String email, String phone, String password)
            throws UserNotFoundException, BadRequestException {
        if (
                (email == null && phone == null)
                        || ((email != null && email.isEmpty()) && (phone != null && phone.isEmpty()))
        ) {
            throw new BadRequestException("اطلاعات وارد شده صحیح نمی باشد");
        }

        if (password == null || password.length() < 8) {
            throw new BadRequestException("اطلاعات وارد شده صحیح نمی باشد");
        }

        Optional<User> userOptional = userRepository.findByEmailOrPhone(email, phone);

        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("اطلاعات وارد شده صحیح نمی باشد");
        }

        User user = userOptional.get();

        if ((new BCryptPasswordEncoder()).matches(password, user.getPassword())) {
            return new RegisterAndLoginResultDTO(JwtUtils.generateToken(user.getId()));
        }
        throw new UserNotFoundException();
    }


    @Override
    public RegisterAndLoginResultDTO register(String phone, String email, String password)
            throws BadRequestException, UserAlreadyExist {

        EmailValidator emailValidator = EmailValidator.getInstance(false);
        if (!emailValidator.isValid(email)) {
            throw new BadRequestException("اطلاعات وارد شده صحیح نمی باشد");
        }

        if (phone == null || phone.length() != 11) {
            throw new BadRequestException("اطلاعات وارد شده صحیح نمی باشد");
        }

        if (password == null || password.length() < 8) {
            throw new BadRequestException("اطلاعات وارد شده صحیح نمی باشد");
        }

        Optional<User> userOptional = userRepository.findByEmailOrPhone(email, phone);

        if (userOptional.isPresent()) {
            throw new UserAlreadyExist("کاربری با این شماره و ایمیل وجود دارد");
        }

        User user = new User();
        user.setPhone(phone);
        user.setEmail(email);
        user.setPassword((new BCryptPasswordEncoder()).encode(password));

        userRepository.save(user);

        return new RegisterAndLoginResultDTO(JwtUtils.generateToken(user.getId()));
    }

    @Override
    public void forgotPassword(String email) throws UserNotFoundException {
        String code = generateAndSaveCode(email);

        try {
            Email mail = DefaultEmail.builder()
                    .from(new InternetAddress(sender))
                    .to(Lists.newArrayList(new InternetAddress(email)))
                    .subject("Gamein Password Reset")
                    .body("کد فراموشی شما: " + code)
                    .encoding("UTF-8").build();
            emailService.send(mail);
        } catch (AddressException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void forgotPasswordSMS(String phone) throws UserNotFoundException {
        String code = generateAndSaveCode(phone);

        // TODO send sms
    }

    @Override
    public void resetPassword(String code, String password) throws BadRequestException {
        Optional<User> userOptional = userRepository.findByPasswordCode(code);
        if (userOptional.isEmpty()) {
            throw new BadRequestException("کد نامعتبر!");
        }
        if (password == null || password.length() < 8) {
            throw new BadRequestException("طول رمز عبور ۸ حرف یا بیشتر باشد!");
        }

        User user = userOptional.get();
        user.setPassword((new BCryptPasswordEncoder()).encode(password));
        user.setPasswordCode(null);
        userRepository.save(user);
    }

    @Override
    public User getUserById(Long userId) throws BadRequestException {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new BadRequestException("User does not exist!");
        }
        return userOptional.get();
    }

    private String generateAndSaveCode(String contact) throws UserNotFoundException {
        Optional<User> userOptional = userRepository.findByEmailOrPhone(contact, contact);
        if (userOptional.isEmpty()) throw new UserNotFoundException();
        User user = userOptional.get();
        String code = randomCodeGenerator();
        user.setPasswordCode(code);
        return userRepository.save(user).getPasswordCode();
    }

    private String randomCodeGenerator() {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(6);

        for (int i = 0; i < 6; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }
        return sb.toString();
    }
}
