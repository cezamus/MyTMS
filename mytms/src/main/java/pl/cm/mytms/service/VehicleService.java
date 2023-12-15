package pl.cm.mytms.service;

import pl.cm.mytms.dto.VehicleWithAssignmentsDTO;
import pl.cm.mytms.model.Vehicle;

import java.util.List;

public interface VehicleService extends RegistrableUnitService {

    List<Vehicle> getAll();

    List<VehicleWithAssignmentsDTO> getAllWithTrailers();
}
