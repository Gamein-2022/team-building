package gamein2.team.controllers;

import gamein2.team.kernel.dto.request.AcceptTeamOfferRequestDTO;
import gamein2.team.kernel.dto.request.CreateTeamOfferRequestDTO;
import gamein2.team.kernel.dto.result.*;
import gamein2.team.kernel.exceptions.BadRequestException;
import gamein2.team.kernel.exceptions.UnauthorizedException;
import gamein2.team.kernel.iao.AuthInfo;
import gamein2.team.services.team.TeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("team")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TeamController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final TeamService serviceHandler;

    public TeamController(TeamService serviceHandler) {
        this.serviceHandler = serviceHandler;
    }

    @GetMapping(value = "users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserDTO>> getUsers(@ModelAttribute AuthInfo authInfo) {
        return new ResponseEntity<>(serviceHandler.getUsers(authInfo.getUser()), HttpStatus.OK);
    }

    @PostMapping(value = "team-offer", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResult> createTeamOffer(@ModelAttribute AuthInfo authInfo,
                                                      @RequestBody CreateTeamOfferRequestDTO request) {
        try {
            return new ResponseEntity<>(ServiceResult.createResult(serviceHandler.requestTeamJoin(authInfo.getTeam(),
                    authInfo.getUser(),
                    request.getUserId())), HttpStatus.OK);
        } catch (UnauthorizedException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(new ErrorResultDTO(e.getMessage()), HttpStatus.UNAUTHORIZED);
        } catch (BadRequestException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(new ErrorResultDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "offers", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TeamOfferDTO>> getMyOffers(@ModelAttribute AuthInfo authInfo) {
        return new ResponseEntity<>(serviceHandler.getMyOffers(authInfo.getUser()), HttpStatus.OK);
    }

    @GetMapping(value = "sent-offers", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getTeamOffers(@ModelAttribute AuthInfo authInfo) {
        try {
            return new ResponseEntity<>(serviceHandler.getTeamOffers(authInfo.getTeam(), authInfo.getUser()), HttpStatus.OK);
        } catch (BadRequestException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(new ErrorResultDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (UnauthorizedException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(new ErrorResultDTO(e.getMessage()), HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping(value = "accept-offer", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResult> acceptTeamJoin(@ModelAttribute AuthInfo authInfo,
                                                            @RequestBody AcceptTeamOfferRequestDTO request) {
        try {
            return new ResponseEntity<>(ServiceResult.createResult(serviceHandler.acceptOffer(authInfo.getUser(),
                    request.getOfferId())),
                    HttpStatus.OK);
        } catch (BadRequestException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(new ErrorResultDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResult> leaveTeam(@ModelAttribute AuthInfo authInfo) {
        try {
            return new ResponseEntity<>(ServiceResult.createResult(serviceHandler.leaveTeam(authInfo.getUser())),
                    HttpStatus.OK);
        } catch (BadRequestException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(new ErrorResultDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
