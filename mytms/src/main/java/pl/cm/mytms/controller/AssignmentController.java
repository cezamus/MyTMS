package pl.cm.mytms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.cm.mytms.model.Assignment;
import pl.cm.mytms.service.AssignmentService;

import java.time.Instant;
import java.util.List;

@Tag(name = "Assignment Management")
@RestController
@RequestMapping(path = "api/assignment")
public class AssignmentController extends ExceptionHandlingBaseController {

    private final AssignmentService assignmentService;

    @Autowired
    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @Operation(summary = "Create an assigment using IDs of vehicle and trailer. " +
            "If vehicle or trailer is already assigned in the specified period, operation will not be performed.")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "{vehicle-id}/{trailer-id}")
    public void create(
            @PathVariable("vehicle-id") Long vehicleId,
            @PathVariable("trailer-id") Long trailerId,
            @RequestParam(value = "start-date", required = false) Instant startDate,
            @RequestParam(value = "end-date", required = false) Instant endDate
    ) {
        assignmentService.create(vehicleId, trailerId, startDate, endDate);
    }

    @Operation(summary = "Create an assigment using registration IDs of vehicle and trailer." +
            "If vehicle or trailer is already assigned in the specified period, operation will not be performed.")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void create(
            @RequestParam(value = "vehicle-registration-id") String vehicleRegistrationId,
            @RequestParam(value = "trailer-registration-id") String trailerRegistrationId,
            @RequestParam(value = "start-date", required = false) Instant startDate,
            @RequestParam(value = "end-date", required = false) Instant endDate
    ) {
        assignmentService.create(vehicleRegistrationId, trailerRegistrationId, startDate, endDate);
    }

    @Operation(summary = "Create an assigment using IDs of vehicle and trailer. " +
            "If vehicle or trailer is already assigned in the specified period, operation will not be performed.") // todo: check summary
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "force/{vehicle-id}/{trailer-id}")
    public void forceCreate(
            @PathVariable("vehicle-id") Long vehicleId,
            @PathVariable("trailer-id") Long trailerId,
            @RequestParam(value = "start-date", required = false) Instant startDate,
            @RequestParam(value = "end-date", required = false) Instant endDate
    ) {
        assignmentService.forceCreate(vehicleId, trailerId, startDate, endDate);
    }

    @Operation(summary = "Create an assigment using registration IDs of vehicle and trailer." +
            "If vehicle or trailer is already assigned in the specified period, operation will not be performed.") // todo: check summary
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "force")
    public void forceCreate(
            @RequestParam(value = "vehicle-registration-id") String vehicleRegistrationId,
            @RequestParam(value = "trailer-registration-id") String trailerRegistrationId,
            @RequestParam(value = "start-date", required = false) Instant startDate,
            @RequestParam(value = "end-date", required = false) Instant endDate
    ) {
        assignmentService.forceCreate(vehicleRegistrationId, trailerRegistrationId, startDate, endDate);
    }

    @Operation(summary = "Get all assignments.")
    @GetMapping
    public List<Assignment> getAll() {
        return assignmentService.getAll();
    }

    // TODO: add missing method and crossHitch
}
