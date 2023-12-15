package pl.cm.mytms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.cm.mytms.model.Trailer;
import pl.cm.mytms.service.TrailerService;

import java.util.List;

@Tag(name = "Trailer Management")
@RestController
@RequestMapping(path = "api/trailer")
public class TrailerController extends ExceptionHandlingBaseController {

    private final TrailerService trailerService;

    @Autowired
    public TrailerController(TrailerService trailerService) {
        this.trailerService = trailerService;
    }

    @Operation(summary = "Create a trailer.")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "{registration-id}")
    public void create(@PathVariable("registration-id") String registrationId) {
        trailerService.create(registrationId);
    }

    @Operation(summary = "Get all trailers.")
    @GetMapping
    public List<Trailer> getAll() {
        return trailerService.getAll();
    }

    @Operation(summary = "Change registration ID of a trailer by its ID.")
    @PutMapping(path = "{id}")
    public void update(
            @PathVariable Long id,
            @RequestParam(value = "registration-id") String registrationId) {
        trailerService.update(id, registrationId);
    }

    @Operation(summary = "Change registration ID of a trailer by its current registration ID.")
    @PutMapping()
    public void update(
            @RequestParam(value = "current-registration") String currentRegistrationId,
            @RequestParam(value = "new-registration") String newRegistrationId) {
        trailerService.update(currentRegistrationId, newRegistrationId);
    }

    @Operation(summary = "Delete a trailer with all its assignments by its ID.")
    @DeleteMapping(path = "{id}")
    public void delete(@PathVariable Long id) {
        trailerService.delete(id);
    }

    @Operation(summary = "Delete a trailer with all its assignments by its registration ID.")
    @DeleteMapping
    public void delete(@RequestParam(value = "registration-id") String registrationId) {
        trailerService.delete(registrationId);
    }
}
