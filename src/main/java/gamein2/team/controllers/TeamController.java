package gamein2.team.controllers;

import gamein2.team.kernel.dto.request.AcceptTeamOfferRequestDTO;
import gamein2.team.kernel.dto.request.CreateTeamOfferRequestDTO;
import gamein2.team.kernel.dto.request.ProfileInfoRequestDTO;
import gamein2.team.kernel.dto.request.TeamInfoRequestDTO;
import gamein2.team.kernel.dto.result.BaseResult;
import gamein2.team.kernel.dto.result.ErrorResultDTO;
import gamein2.team.kernel.dto.result.ServiceResult;
import gamein2.team.kernel.dto.result.TeamInfoResultDTO;
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


@RestController
@RequestMapping("team")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TeamController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final TeamService serviceHandler;

    public TeamController(TeamService serviceHandler) {
        this.serviceHandler = serviceHandler;
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResult> createTeam(@ModelAttribute("authInfo") AuthInfo authInfo,
                                                 @RequestBody TeamInfoRequestDTO request) {
        try {
            TeamInfoResultDTO result = serviceHandler.createTeam(authInfo.getUser(), request.getName());
            return new ResponseEntity<>(ServiceResult.createResult(result), HttpStatus.OK);
        } catch (BadRequestException e) {
            logger.error(e.toString());
            ErrorResultDTO error = new ErrorResultDTO(e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResult> getTeamInfo(@ModelAttribute("authInfo") AuthInfo authInfo) {
        try {
            TeamInfoResultDTO result = serviceHandler.getTeamInfo(authInfo.getTeam(), authInfo.getUser());
            return new ResponseEntity<>(ServiceResult.createResult(result),
                    HttpStatus.OK);
        } catch (BadRequestException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(new ErrorResultDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping(value = "profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResult> getProfile(@ModelAttribute AuthInfo authInfo) {
        return new ResponseEntity<>(ServiceResult.createResult(serviceHandler.getProfile(authInfo.getUser())),
                HttpStatus.OK);
    }

    @PutMapping(value = "profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResult> updateProfile(@ModelAttribute AuthInfo authInfo,
                                                    @RequestBody ProfileInfoRequestDTO profile) {
        return new ResponseEntity<>(ServiceResult.createResult(serviceHandler.updateProfile(authInfo.getUser(), profile)),
                HttpStatus.OK);
    }

    @GetMapping(value = "users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResult> getUsers(@ModelAttribute AuthInfo authInfo) {
        try {
            return new ResponseEntity<>(ServiceResult.createResult(serviceHandler.getUsers(authInfo.getUser())),
                    HttpStatus.OK);
        } catch (BadRequestException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(new ErrorResultDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
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
    public ResponseEntity<BaseResult> getMyOffers(@ModelAttribute AuthInfo authInfo) {
        try {
            return new ResponseEntity<>(ServiceResult.createResult(serviceHandler.getMyOffers(authInfo.getUser())),
                    HttpStatus.OK);
        } catch (BadRequestException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(new ErrorResultDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "sent-offers", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResult> getTeamOffers(@ModelAttribute AuthInfo authInfo) {
        try {
            return new ResponseEntity<>(ServiceResult.createResult(serviceHandler.getTeamOffers(authInfo.getTeam(),
                    authInfo.getUser())),
                    HttpStatus.OK);
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

    @DeleteMapping(value = "sent-offers/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResult> cancelOffer(@ModelAttribute AuthInfo authInfo, @PathVariable Long id) {
        try {
            return new ResponseEntity<>(ServiceResult.createResult(serviceHandler.cancelOffer(authInfo.getUser(), id)),
                    HttpStatus.OK);
        } catch (BadRequestException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(new ErrorResultDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (UnauthorizedException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(new ErrorResultDTO(e.getMessage()), HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping(value = "offers/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResult> declineOffer(@ModelAttribute AuthInfo authInfo, @PathVariable Long id) {
        try {
            return new ResponseEntity<>(ServiceResult.createResult(serviceHandler.declineOffer(authInfo.getUser(), id)),
                    HttpStatus.OK);
        } catch (BadRequestException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(new ErrorResultDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
