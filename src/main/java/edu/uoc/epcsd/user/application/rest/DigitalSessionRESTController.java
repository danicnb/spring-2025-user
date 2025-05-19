package edu.uoc.epcsd.user.application.rest;

import edu.uoc.epcsd.user.application.rest.request.CreateDigitalSessionRequest;
import edu.uoc.epcsd.user.domain.DigitalItem;
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

    /**
     * GET request to retrieve a digital session by its ID.
     *
     * @param digitalSessionId the ID of the digital session to retrieve
     * @return the digital session if found, wrapped in a ResponseEntity with status 200 OK;
     *         or status 404 Not Found if no session exists with the given ID
     */
    @GetMapping("/{digitalSessionId}")
    public ResponseEntity<DigitalSession> getDigitalSessionById(@PathVariable @NotNull Long digitalSessionId) {
        log.trace("getDigitalSessionById");

        return digitalSessionService.getDigitalSessionById(digitalSessionId).map( digitalItem -> ResponseEntity.ok().body(digitalItem))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET request to retrieve all digital sessions associated with a specific user ID.
     *
     * @param userId the ID of the user whose digital sessions are to be retrieved
     * @return a list of digital sessions linked to the specified user
     */
    @GetMapping("/digitalByUser")
    public List<DigitalSession> findDigitalSessionByUser(@RequestParam @NotNull Long userId) {
        log.trace("findDigitalSessionByUser");

        return digitalSessionService.findDigitalSessionByUser(userId);
    }

    /**
     * POST request to create a new digital session for a specific user.
     *
     * @param createDigitalSessionRequest the request body containing details of the digital session to be created
     * @return the ID of the newly created digital session wrapped in a ResponseEntity with status 201 Created;
     *         or status 400 Bad Request if the specified user does not exist
     */
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

    /**
     * PATCH request to update an existing digital session by its ID.
     *
     * @param digitalSessionId the ID of the digital session to be updated
     * @param updateDigitalSessionRequest the request body containing the updated digital session details
     * @return ResponseEntity with the ID of the updated digital session and status 201 Created;
     *         or status 400 Bad Request if the specified session does not exist
     */
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

    /**
     * DELETE request to remove a digital session by its ID.
     *
     * @param digitalSessionId the ID of the digital session to be removed
     * @return ResponseEntity with status 204 No Content if successfully removed;
     *         or status 400 Bad Request if the specified session does not exist
     */
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
