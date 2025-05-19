package edu.uoc.epcsd.user.application.rest;

import edu.uoc.epcsd.user.application.rest.request.CreateDigitalSessionRequest;
import edu.uoc.epcsd.user.domain.DigitalItem;
import edu.uoc.epcsd.user.domain.DigitalItemStatus;
import edu.uoc.epcsd.user.domain.DigitalSession;
import edu.uoc.epcsd.user.domain.service.DigitalSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;

@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@RequestMapping("/digital")
public class DigitalSessionRESTController {

    private final DigitalSessionService digitalSessionService;

    @GetMapping("/allDigital")
    @ResponseStatus(HttpStatus.OK)
    public List<DigitalSession> getAllDigitalSession() {
        log.trace("getAllDigitalSession");

        return digitalSessionService.findAllDigitalSession();
    }
    
    // TODO: add the code for the missing system operations here: 
    // use the corresponding mapping HTTP request annotation with the parameter: "/{digitalSessionId}"
    // and create the method getDigitalSessionById(@PathVariable @NotNull Long digitalSessionId)
    // which call the corresponding getDigitalSessionById method
    @GetMapping("/{digitalSessionId}")
    public ResponseEntity<DigitalSession> getDigitalSessionById(@PathVariable @NotNull Long digitalSessionId) {
        log.trace("getDigitalSessionById");

        return digitalSessionService.getDigitalSessionById(digitalSessionId).map( digitalItem -> ResponseEntity.ok().body(digitalItem))
                .orElse(ResponseEntity.notFound().build());
    }

    // TODO: add the code for the missing system operations here: 
    // use the corresponding mapping HTTP request annotation with the parameter: "/digitalByUser"
    // and create the method findDigitalSessionByUser(@RequestParam @NotNull Long userId)
    // which call the corresponding findDigitalSessionByUser method
    @GetMapping("/digitalByUser")
    public List<DigitalSession> findDigitalSessionByUser(@RequestParam @NotNull Long userId) {
        log.trace("findDigitalSessionByUser");

        return digitalSessionService.findDigitalSessionByUser(userId);
    }

    // TODO: add the code for the missing system operations here: 
    // use the corresponding mapping HTTP request annotation with the parameter: "/createDigital"
    // and create the method createDigitalSession(@RequestBody @Valid CreateDigitalSessionRequest createDigitalSessionRequest)
    // which call the corresponding createDigitalSession method
    @PostMapping("/createDigital")
    public ResponseEntity<Long> createDigitalSession(@RequestBody @Valid CreateDigitalSessionRequest createDigitalSessionRequest) {
        log.trace("createDigitalSession");

        log.trace("Creating digital session" + createDigitalSessionRequest);

        try {
            Long digitalSessionId = digitalSessionService.createDigitalSession(DigitalSession.builder()
                    .description(createDigitalSessionRequest.getDescription())
                    .location(createDigitalSessionRequest.getLocation())
                    .link(createDigitalSessionRequest.getLink())
                    .userId(createDigitalSessionRequest.getUserId())
                    .build());

            URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{createDigital}")
                    .buildAndExpand(digitalSessionId)
                    .toUri();

            return ResponseEntity.created(uri).body(digitalSessionId);

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The specified user " + createDigitalSessionRequest.getUserId() + " does not exist.", e);
        }
    }

    // TODO: add the code for the missing system operations here: 
    // use the corresponding mapping HTTP request annotation with the parameter: "/updateDigital/{digitalSessionId}"
    // and create the method updateDigitalSession(@PathVariable @NotNull Long digitalSessionId, @RequestBody @Valid CreateDigitalSessionRequest updateDigitalSessionRequest)
    // which call the corresponding updateDigitalSession method
    @PatchMapping("/updateDigital/{digitalSessionId}")
    public ResponseEntity<Long> updateDigitalSession(@PathVariable @NotNull Long digitalSessionId, @RequestBody @Valid CreateDigitalSessionRequest updateDigitalSessionRequest) {
        log.trace("updateDigitalSession");

        log.trace("Updating digital session" + updateDigitalSessionRequest);

        try {
            Long id = digitalSessionService.updateDigitalSession(digitalSessionId,
                    updateDigitalSessionRequest.getDescription(),
                    updateDigitalSessionRequest.getLink(),
                    updateDigitalSessionRequest.getLocation(),
                    updateDigitalSessionRequest.getUserId());

            URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(id)
                    .toUri();

            return ResponseEntity.created(uri).body(id);

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The specified digital session " + digitalSessionId + " does not exist.", e);
        }
    }

    // TODO: add the code for the missing system operations here: 
    // use the corresponding mapping HTTP request annotation with the parameter: "/removeDigital/{digitalSessionId}"
    // and create the method removeDigitalSession(@PathVariable @NotNull Long digitalSessionId
    // which call the corresponding removeDigitalSession method
    @DeleteMapping("/removeDigital/{digitalSessionId}")
    public ResponseEntity<Long> removeDigitalSession(@PathVariable @NotNull Long digitalSessionId) {
        log.trace("removeDigitalSession");

        log.trace("Removing digital session" + digitalSessionId);

        digitalSessionService.getDigitalSessionById(digitalSessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "The specified digital session " + digitalSessionId + " does not exist."));

        digitalSessionService.removeDigitalSession(digitalSessionId);

        return ResponseEntity.noContent().build();
    }
}
