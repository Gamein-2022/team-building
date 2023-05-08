package gamein2.team.controllers;

import gamein2.team.kernel.dto.request.ForgotPasswordRequestDTO;
import gamein2.team.kernel.dto.request.ForgotPasswordSMSRequestDTO;
import gamein2.team.kernel.dto.request.RegisterAndLoginRequestDTO;
import gamein2.team.kernel.dto.request.ResetPasswordRequestDTO;
import gamein2.team.kernel.dto.result.BaseResult;
import gamein2.team.kernel.dto.result.ErrorResultDTO;
import gamein2.team.kernel.dto.result.RegisterAndLoginResultDTO;
import gamein2.team.kernel.dto.result.ServiceResult;
import gamein2.team.kernel.exceptions.BadRequestException;
import gamein2.team.kernel.exceptions.UserAlreadyExist;
import gamein2.team.kernel.exceptions.UserNotFoundException;
import gamein2.team.services.auth.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResult> login(@RequestBody RegisterAndLoginRequestDTO request) {
        logger.info(request.getEmail(), " --- ", request.getPhone(), " --- ", request
                .getPassword());
        try {
            RegisterAndLoginResultDTO result = authService.login(request.getEmail(), request.getPhone(), request.getPassword());
            return new ResponseEntity<>(ServiceResult.createResult(result), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            logger.error(e.toString());
            ErrorResultDTO error = new ErrorResultDTO(e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        } catch (BadRequestException e) {
            logger.error(e.toString());
            ErrorResultDTO error = new ErrorResultDTO(e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResult> register(@RequestBody RegisterAndLoginRequestDTO request) {
        try {
            RegisterAndLoginResultDTO result = authService.register(request.getPhone(), request.getEmail(), request.getPassword());
            return new ResponseEntity<>(ServiceResult.createResult(result), HttpStatus.OK);
        } catch (BadRequestException | UserAlreadyExist e) {
            logger.error(e.toString());
            ErrorResultDTO error = new ErrorResultDTO(e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("forgot-password")
    public ResponseEntity<BaseResult> forgotPassword(@RequestBody ForgotPasswordRequestDTO request) {
        try {
            authService.forgotPassword(request.getEmail());
        } catch (UserNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("forgot-password/sms")
    public ResponseEntity<BaseResult> forgotPasswordSMS(@RequestBody ForgotPasswordSMSRequestDTO request) {
        try {
            authService.forgotPasswordSMS(request.getPhone());
        } catch (UserNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("reset-password")
    public ResponseEntity<BaseResult> resetPassword(@RequestBody ResetPasswordRequestDTO request) {
        try {
            authService.resetPassword(request.getCode(), request.getPassword());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (BadRequestException e) {
            logger.error(e.getMessage(), e);
            ErrorResultDTO error = new ErrorResultDTO(e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }
}