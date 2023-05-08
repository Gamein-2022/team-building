package gamein2.team.controllers;

import gamein2.team.kernel.dto.result.ErrorResultDTO;
import gamein2.team.kernel.exceptions.BadRequestException;
import gamein2.team.kernel.exceptions.InvalidTokenException;
import gamein2.team.kernel.exceptions.UnauthorizedException;
import gamein2.team.kernel.iao.AuthInfo;
import gamein2.team.services.auth.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;


@ControllerAdvice(assignableTypes = {TeamController.class})
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class InitController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final AuthService authService;


    public InitController(AuthService authService) {
        this.authService = authService;
    }

    @ModelAttribute(name = "authInfo")
    public AuthInfo getLoginInformation(HttpServletRequest request) throws UnauthorizedException, BadRequestException {
        String token = request.getHeader("Authorization");
        if (token == null || token.length() < 8) {
            throw new UnauthorizedException("Invalid token!");
        }

        try {
            return authService.extractAuthInfoFromToken(token.substring(7));
        } catch (InvalidTokenException e) {
            logger.error(e.toString());
            throw new UnauthorizedException("Invalid token!");
        }
    }

    @ExceptionHandler(value = UnauthorizedException.class)
    public ResponseEntity<ErrorResultDTO> exception(UnauthorizedException e) {
        logger.error(e.getMessage());
        ErrorResultDTO result = new ErrorResultDTO(e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<ErrorResultDTO> exception(BadRequestException e) {
        logger.error(e.getMessage());
        ErrorResultDTO result = new ErrorResultDTO(e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }
}