package pl.cm.mytms.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.cm.mytms.dto.TrailerAssignmentDTO;
import pl.cm.mytms.dto.VehicleWithAssignmentsDTO;
import pl.cm.mytms.model.Vehicle;
import pl.cm.mytms.repository.AssignmentRepository;
import pl.cm.mytms.repository.VehicleRepository;
import pl.cm.mytms.service.VehicleService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final AssignmentRepository assignmentRepository;

    @Autowired
    public VehicleServiceImpl(VehicleRepository vehicleRepository, AssignmentRepository assignmentRepository) {
        this.vehicleRepository = vehicleRepository;
        this.assignmentRepository = assignmentRepository;
    }

    @Override
    public void create(String registrationId) {
        checkIfRegistrationIdIsValid(registrationId);
        checkIfRegistrationIdIsAlreadyInUse(registrationId);
        vehicleRepository.save(new Vehicle(registrationId));
    }

    @Override
    public List<Vehicle> getAll() {
        return vehicleRepository.findAll();
    }

    @Override
    public List<VehicleWithAssignmentsDTO> getAllWithTrailers() {
        return vehicleRepository.findAll().stream()
                .map(vehicle -> new VehicleWithAssignmentsDTO(
                        vehicle.getId(), vehicle.getRegistrationId(),
                        getAssignments(vehicle)
                ))
                .toList();
    }

    private List<TrailerAssignmentDTO> getAssignments(Vehicle vehicle) {
        return assignmentRepository.findAllByVehicle(vehicle).stream()
                .map(assignment -> new TrailerAssignmentDTO(
                        assignment.getId(),
                        assignment.getTrailer(),
                        assignment.getStartDate(),
                        assignment.getEndDate()
                ))
                .toList();
    }

    @Override
    @Transactional
    public void update(Long id, String registrationId) {
        checkIfRegistrationIdIsValid(registrationId);
        checkIfVehicleExists(id);
        checkIfRegistrationIdIsAlreadyInUse(registrationId);
        Vehicle vehicle = vehicleRepository.getReferenceById(id);
        vehicle.setRegistrationId(registrationId);
    }

    @Override
    @Transactional
    public void update(String currentRegistrationId, String newRegistrationId) {
        checkIfRegistrationIdIsValid(newRegistrationId);
        checkIfRegistrationIdIsAlreadyInUse(newRegistrationId);
        Vehicle vehicle = getIfPossible(currentRegistrationId);
        vehicle.setRegistrationId(newRegistrationId);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        checkIfVehicleExists(id);
        assignmentRepository.deleteAllByVehicle(vehicleRepository.getReferenceById(id));
        vehicleRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void delete(String registrationId) {
        Vehicle vehicle = getIfPossible(registrationId);
        assignmentRepository.deleteAllByVehicle(vehicle);
        vehicleRepository.delete(vehicle);
    }

    // TODO: Replace with custom exceptions.
    private void checkIfRegistrationIdIsValid(String registrationId) {
        if (registrationId == null || registrationId.isBlank()) {
            throw new IllegalArgumentException("Registration ID cannot be null or blank.");
        }
    }

    private void checkIfRegistrationIdIsAlreadyInUse(String registrationId) {
        if (vehicleRepository.findVehicleByRegistrationId(registrationId).isPresent()) {
            throw new IllegalStateException("Registration ID " + registrationId + " is already in use.");
        }
    }

    private void checkIfVehicleExists(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new NoSuchElementException("Vehicle with ID " + id + " has not been found.");
        }
    }

    private Vehicle getIfPossible(String registrationId) {
        Optional<Vehicle> vehicle = vehicleRepository.findVehicleByRegistrationId(registrationId);
        if (vehicle.isPresent()) {
            return vehicle.get();
        } else {
            throw new NoSuchElementException("Vehicle with Registration ID " + registrationId + " has not been found.");
        }
    }
}
