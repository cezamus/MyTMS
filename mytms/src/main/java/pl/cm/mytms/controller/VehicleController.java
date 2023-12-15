package pl.cm.mytms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.cm.mytms.dto.VehicleWithAssignmentsDTO;
import pl.cm.mytms.model.Vehicle;
import pl.cm.mytms.service.VehicleService;

import java.util.List;

@Tag(name = "Vehicle Management")
@RestController
@RequestMapping(path = "api/vehicle")
public class VehicleController extends ExceptionHandlingBaseController {

    private final VehicleService vehicleService;

    @Autowired
    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @Operation(summary = "Create a vehicle.")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "{registration-id}")
    public void create(@PathVariable("registration-id") String registrationId) {
        vehicleService.create(registrationId);
    }

    @Operation(summary = "Get all vehicles.")
    @GetMapping
    public List<Vehicle> getAll() {
        return vehicleService.getAll();
    }

    @Operation(summary = "Get all vehicles with trailers assigned up to a month back and in the future.")
    @GetMapping(path = "with-trailers")
    // TODO: add sorting and filtering
    public List<VehicleWithAssignmentsDTO> getAllWithTrailers() {
        return vehicleService.getAllWithTrailers();
    }

    @Operation(summary = "Change registration ID of a vehicle by its ID.")
    @PutMapping(path = "{id}")
    public void update(
            @PathVariable Long id,
            @RequestParam(value = "registration") String registrationId) {
        vehicleService.update(id, registrationId);
    }

    @Operation(summary = "Change registration ID of a vehicle by its current registration ID.")
    @PutMapping()
    public void update(
            @RequestParam(value = "current-registration") String currentRegistrationId,
            @RequestParam(value = "new-registration") String newRegistrationId) {
        vehicleService.update(currentRegistrationId, newRegistrationId);
    }

    @Operation(summary = "Delete a vehicle with all its assignments by its ID.")
    @DeleteMapping(path = "{id}")
    public void delete(@PathVariable Long id) {
        vehicleService.delete(id);
    }

    @Operation(summary = "Delete a vehicle with all its assignments by its registration ID.")
    @DeleteMapping
    public void delete(@RequestParam(value = "registration-id") String registrationId) {
        vehicleService.delete(registrationId);
    }
}
