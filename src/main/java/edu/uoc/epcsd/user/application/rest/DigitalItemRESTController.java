package edu.uoc.epcsd.user.application.rest;

import edu.uoc.epcsd.user.application.rest.request.CreateDigitalItemRequest;
import edu.uoc.epcsd.user.domain.Alert;
import edu.uoc.epcsd.user.domain.DigitalItem;
import edu.uoc.epcsd.user.domain.DigitalItemStatus;
import edu.uoc.epcsd.user.domain.service.DigitalItemService;
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
@RequestMapping("/digitalItem")
public class DigitalItemRESTController {

    private final DigitalItemService digitalItemService;

    @GetMapping("/allItems")
    @ResponseStatus(HttpStatus.OK)
    public List<DigitalItem> getAllDigitalItem() {
        log.trace("getAllDigitalItem");

        return digitalItemService.findAllDigitalItem();
    }

    /**
     * GET request to retrieve a digital item by its ID.
     *
     * @param digitalItemId the ID of the digital item to retrieve
     * @return the digital item if found, wrapped in a ResponseEntity with status 200 OK;
     *         or status 404 Not Found if no item exists with the given ID
     */
    @GetMapping("/{digitalItemId}")
    public ResponseEntity<DigitalItem> getDigitalItemById(@PathVariable @NotNull Long digitalItemId) {
        log.trace("getDigitalItemById");

        return digitalItemService.getDigitalItemById(digitalItemId).map( digitalItem -> ResponseEntity.ok().body(digitalItem))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET request to retrieve all digital items associated with a specific digital session.
     *
     * @param digitalSessionId the ID of the digital session
     * @return a list of digital items linked to the specified session
     */
    @GetMapping("/digitalItemBySession")
    @ResponseStatus(HttpStatus.OK)
    public List<DigitalItem> findDigitalItemBySession(@RequestParam @NotNull Long digitalSessionId) {
        log.trace("findDigitalItemBySession");

        return digitalItemService.findDigitalItemBySession(digitalSessionId);
    }

    /**
     * POST request to add a new digital item to an existing digital session.
     *
     * @param createDigitalItemRequest the request body containing the digital item's details
     * @return ResponseEntity with the ID of the newly created digital item and status 201 Created;
     *         or status 400 Bad Request if the specified digital session does not exist
     */
    @PostMapping("/addItem")
    public ResponseEntity<Long> addDigitalItem(@RequestBody @Valid CreateDigitalItemRequest createDigitalItemRequest) {
        log.trace("addDigitalItem");

        log.trace("createDigitalItemRequest: " + createDigitalItemRequest);

        try {
            Long digitalItemId = digitalItemService.addDigitalItem(DigitalItem.builder()
                    .digitalSessionId(createDigitalItemRequest.getDigitalSessionId())
                    .description(createDigitalItemRequest.getDescription())
                    .lat(createDigitalItemRequest.getLat())
                    .lon(createDigitalItemRequest.getLon())
                    .link(createDigitalItemRequest.getLink())
                    .status(DigitalItemStatus.AVAILABLE)
                    .build());

            URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{digitalItemId}")
                    .buildAndExpand(digitalItemId)
                    .toUri();

            return ResponseEntity.created(uri).body(digitalItemId);

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The specified digital session " + createDigitalItemRequest.getDigitalSessionId() + " does not exist.", e);
        }
    }

    /**
     * PATCH request to update an existing digital item by its ID.
     *
     * @param digitalItemId the ID of the digital item to be updated
     * @param updateDigitalItemRequest the request body containing the updated digital item details
     * @return ResponseEntity with the ID of the updated digital item and status 201 Created;
     *         or status 400 Bad Request if the specified item does not exist
     */
    @PatchMapping("/updateItem/{digitalItemId}")
    public ResponseEntity<Long> updateDigitalItem(@PathVariable @NotNull Long digitalItemId, @RequestBody @Valid CreateDigitalItemRequest updateDigitalItemRequest) {
        log.trace("updateDigitalItem");

        log.trace("Updating digital item " + digitalItemId);

        try {
            Long id = digitalItemService.updateDigitalItem(digitalItemId,
                    updateDigitalItemRequest.getDescription(),
                    updateDigitalItemRequest.getLink(),
                    updateDigitalItemRequest.getLat(),
                    updateDigitalItemRequest.getLon());

            URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(id)
                    .toUri();

            return ResponseEntity.created(uri).body(id);

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The specified digital item " + digitalItemId + " does not exist.", e);
        }
    }

    /**
     * PATCH request to mark a digital item as pending review by its ID.
     *
     * @param digitalItemId the ID of the digital item to be marked for review
     * @throws ResponseStatusException with status 400 Bad Request if the specified item does not exist
     */
    @PatchMapping("/reviewDigitalItem/{digitalItemId}")
    @ResponseStatus(HttpStatus.OK)
    public void setDigitalItemForReview(@PathVariable @NotNull Long digitalItemId) {
        log.trace("setDigitalItemForReview");

        log.trace("Reviewing digital item " + digitalItemId);

        try {
            digitalItemService.setDigitalItemForReview(digitalItemId);

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The specified digital item " + digitalItemId + " does not exist.", e);
        }
    }

    /**
     * PATCH request to approve a pending digital item by its ID.
     *
     * @param digitalItemId the ID of the digital item to be approved
     * @throws IllegalArgumentException if the specified digital item does not exist
     */
    @PatchMapping("/approveDigitalItem/{digitalItemId}")
    @ResponseStatus(HttpStatus.OK)
    public void approvePendingDigitalItem(@PathVariable @NotNull Long digitalItemId) {
        log.trace("approvePendingDigitalItem");

        log.trace("Approving digital item " + digitalItemId);

        try{
            digitalItemService.approvePendingDigitalItem(digitalItemId);

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * PATCH request to reject a pending digital item by its ID.
     *
     * @param digitalItemId the ID of the digital item to be rejected
     * @throws IllegalArgumentException if the specified digital item does not exist
     */
    @PatchMapping("/rejectDigitalItem/{digitalItemId}")
    @ResponseStatus(HttpStatus.OK)
    public void rejectPendingDigitalItem(@PathVariable @NotNull Long digitalItemId) {
        log.trace("rejectPendingDigitalItem");

        log.trace("Rejecting digital item " + digitalItemId);

        try{
            digitalItemService.rejectPendingDigitalItem(digitalItemId);

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * DELETE request to remove a digital item by its ID.
     *
     * @param digitalItemId the ID of the digital item to be deleted
     * @return ResponseEntity with status 204 No Content if successfully deleted;
     *         or status 400 Bad Request if the specified item does not exist
     */
    @DeleteMapping("/dropItem/{digitalItemId}")
    public ResponseEntity<Void> dropDigitalItem(@PathVariable @NotNull Long digitalItemId) {
        log.trace("dropDigitalItem");
        log.trace("Deleting digital item " + digitalItemId);

        digitalItemService.getDigitalItemById(digitalItemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "The specified digital item " + digitalItemId + " does not exist."));

        digitalItemService.dropDigitalItem(digitalItemId);

        return ResponseEntity.noContent().build();
    }
}