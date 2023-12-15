package pl.cm.mytms.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.cm.mytms.model.Assignment;
import pl.cm.mytms.model.Trailer;
import pl.cm.mytms.model.Vehicle;
import pl.cm.mytms.repository.AssignmentRepository;
import pl.cm.mytms.repository.TrailerRepository;
import pl.cm.mytms.repository.VehicleRepository;
import pl.cm.mytms.service.AssignmentService;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class AssignmentServiceImpl implements AssignmentService {

    private static final Long MINIMAL_BRAKE = 1000L;
    private final AssignmentRepository assignmentRepository;
    private final VehicleRepository vehicleRepository;
    private final TrailerRepository trailerRepository;

    @Autowired
    public AssignmentServiceImpl(
            AssignmentRepository assignmentRepository,
            VehicleRepository vehicleRepository,
            TrailerRepository trailerRepository
    ) {
        this.assignmentRepository = assignmentRepository;
        this.vehicleRepository = vehicleRepository;
        this.trailerRepository = trailerRepository;
    }

    @Override
    public void create(Long vehicleId, Long trailerId, Instant startDate, Instant endDate) {
        validateDates(startDate, endDate);
        checkIfVehicleExists(vehicleId);
        checkIfTrailerExists(trailerId);
        Vehicle vehicle = vehicleRepository.getReferenceById(vehicleId);
        Trailer trailer = trailerRepository.getReferenceById(trailerId);
        validateTimeAvailability(vehicle, trailer, startDate, endDate);
        assignmentRepository.save(new Assignment(vehicle, trailer, startDate, endDate));
    }

    @Override
    public void create(String vehicleRegistrationId, String trailerRegistrationId, Instant startDate, Instant endDate) {
        validateDates(startDate, endDate);
        Vehicle vehicle = getVehicleIfPossible(vehicleRegistrationId);
        Trailer trailer = getTrailerIfPossible(trailerRegistrationId);
        validateTimeAvailability(vehicle, trailer, startDate, endDate);
        assignmentRepository.save(new Assignment(vehicle, trailer, startDate, endDate));
    }

    @Override
    @Transactional
    public void forceCreate(Long vehicleId, Long trailerId, Instant startDate, Instant endDate) {
        validateDates(startDate, endDate);
        checkIfVehicleExists(vehicleId);
        checkIfTrailerExists(trailerId);
        Vehicle vehicle = vehicleRepository.getReferenceById(vehicleId);
        Trailer trailer = trailerRepository.getReferenceById(trailerId);
        adjustTimeAvailability(assignmentRepository.findAllByVehicle(vehicle), startDate, endDate);
        adjustTimeAvailability(assignmentRepository.findAllByTrailer(trailer), startDate, endDate);
        assignmentRepository.save(new Assignment(vehicle, trailer, startDate, endDate));
    }

    @Override
    @Transactional
    public void forceCreate(
            String vehicleRegistrationId,
            String trailerRegistrationId,
            Instant startDate,
            Instant endDate
    ) {
        validateDates(startDate, endDate);
        Vehicle vehicle = getVehicleIfPossible(vehicleRegistrationId);
        Trailer trailer = getTrailerIfPossible(trailerRegistrationId);
        adjustTimeAvailability(assignmentRepository.findAllByVehicle(vehicle), startDate, endDate);
        adjustTimeAvailability(assignmentRepository.findAllByTrailer(trailer), startDate, endDate);
        assignmentRepository.save(new Assignment(vehicle, trailer, startDate, endDate));
    }

    @Override
    public List<Assignment> getAll() {
        return assignmentRepository.findAll();
    }

    private void validateTimeAvailability(Vehicle vehicle, Trailer trailer, Instant startDate, Instant endDate) {
        validateVehicleAvailability(vehicle, startDate, endDate);
        validateTrailerAvailability(trailer, startDate, endDate);
    }

    private void validateVehicleAvailability(Vehicle vehicle, Instant startDate, Instant endDate) {
        List<Assignment> existingAssignments = assignmentRepository.findAllByVehicle(vehicle);
        if (!existingAssignments.isEmpty()) {
            for (Assignment existingAssignment : existingAssignments) {
                if (isStartOfNewAssignmentAfterEndOfExistingAssignment(existingAssignment.getEndDate(), startDate) ||
                        isEndOfNewAssignmentBeforeStartOfExistingAssignment(existingAssignment.getStartDate(), endDate)
                ) {
                    continue;
                }
                throw new IllegalStateException(
                        "A trailer is already assigned to this vehicle in the specified period."
                );
            }
        }
    }

    private void validateTrailerAvailability(Trailer trailer, Instant startDate, Instant endDate) {
        List<Assignment> existingAssignments = assignmentRepository.findAllByTrailer(trailer);
        if (!existingAssignments.isEmpty()) {
            for (Assignment existingAssignment : existingAssignments) {
                if (isStartOfNewAssignmentAfterEndOfExistingAssignment(existingAssignment.getEndDate(), startDate) ||
                        isEndOfNewAssignmentBeforeStartOfExistingAssignment(existingAssignment.getStartDate(), endDate)
                ) {
                    continue;
                }
                throw new IllegalStateException(
                        "This trailer is already assigned to a vehicle in the specified period."
                );
            }
        }
    }

    private void adjustTimeAvailability(List<Assignment> existingAssignments, Instant startDate, Instant endDate) {
        if (!existingAssignments.isEmpty()) {
            for (Assignment existingAssignment : existingAssignments) {
                if (isStartOfNewAssignmentAfterEndOfExistingAssignment(existingAssignment.getEndDate(), startDate) ||
                        isEndOfNewAssignmentBeforeStartOfExistingAssignment(existingAssignment.getStartDate(), endDate)
                ) {
                    continue;
                }
                modifyConflictingAssignment(existingAssignment, startDate, endDate);
            }
        }
    }

    private void modifyConflictingAssignment(
            Assignment existingAssignment,
            Instant newAssignmentStart,
            Instant newAssignmentEnd) {
        Instant existingAssignmentStart = existingAssignment.getStartDate();
        Instant existingAssignmentEnd = existingAssignment.getEndDate();
        if (isNewAssignmentCoveringLeftSideOfExistingAssignment(existingAssignmentStart, newAssignmentStart) &&
                isNewAssignmentCoveringRightSideOfExistingAssignment(existingAssignmentEnd, newAssignmentEnd)
        ) {
            assignmentRepository.delete(existingAssignment);
        } else if (isNewAssignmentCoveringLeftSideOfExistingAssignment(existingAssignmentStart, newAssignmentStart)) {
            existingAssignment.setStartDate(newAssignmentEnd.plusMillis(MINIMAL_BRAKE));
        } else if (isNewAssignmentCoveringRightSideOfExistingAssignment(existingAssignmentEnd, newAssignmentEnd)) {
            existingAssignment.setEndDate(newAssignmentStart.minusMillis(MINIMAL_BRAKE));
        } else {
            split(existingAssignment, newAssignmentStart, newAssignmentEnd);
        }
    }

    private void split(Assignment originalAssignment, Instant brakeStart, Instant brakeEnd) {
        Instant originalEnd = originalAssignment.getEndDate();
        originalAssignment.setEndDate(brakeStart.minusMillis(MINIMAL_BRAKE));
        assignmentRepository.save(new Assignment(
                originalAssignment.getVehicle(),
                originalAssignment.getTrailer(),
                brakeEnd.plusMillis(MINIMAL_BRAKE),
                originalEnd
        ));
    }

    private boolean isNewAssignmentCoveringLeftSideOfExistingAssignment(
            Instant existingAssignmentStart,
            Instant newAssignmentStart
    ) {
        return newAssignmentStart == null ||
                (existingAssignmentStart != null && !newAssignmentStart.isAfter(existingAssignmentStart));
    }

    private boolean isNewAssignmentCoveringRightSideOfExistingAssignment(
            Instant existingAssignmentEnd,
            Instant newAssignmentEnd
    ) {
        return newAssignmentEnd == null ||
                (existingAssignmentEnd != null && !newAssignmentEnd.isBefore(existingAssignmentEnd));
    }

    private boolean isStartOfNewAssignmentAfterEndOfExistingAssignment(
            Instant existingAssignmentEnd,
            Instant newAssignmentStart
    ) {
        return newAssignmentStart != null &&
                existingAssignmentEnd != null &&
                newAssignmentStart.isAfter(existingAssignmentEnd);
    }

    private boolean isEndOfNewAssignmentBeforeStartOfExistingAssignment(
            Instant existingAssignmentStart,
            Instant newAssignmentEnd) {
        return newAssignmentEnd != null &&
                existingAssignmentStart != null &&
                newAssignmentEnd.isBefore(existingAssignmentStart);
    }

    private void checkIfVehicleExists(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new NoSuchElementException("Vehicle with ID " + id + " has not been found.");
        }
    }

    private void checkIfTrailerExists(Long id) {
        if (!trailerRepository.existsById(id)) {
            throw new NoSuchElementException("Trailer with ID " + id + " has not been found.");
        }
    }

    private Vehicle getVehicleIfPossible(String registrationId) {
        Optional<Vehicle> vehicle = vehicleRepository.findVehicleByRegistrationId(registrationId);
        if (vehicle.isPresent()) {
            return vehicle.get();
        } else {
            throw new NoSuchElementException("Vehicle with Registration ID " + registrationId + " has not been found.");
        }
    }

    private Trailer getTrailerIfPossible(String registrationId) {
        Optional<Trailer> trailer = trailerRepository.findTrailerByRegistrationId(registrationId);
        if (trailer.isPresent()) {
            return trailer.get();
        } else {
            throw new NoSuchElementException("Trailer with Registration ID " + registrationId + " has not been found.");
        }
    }

    private void validateDates(Instant startDate, Instant endDate) {
        if (startDate != null) {
            if (endDate != null) {
                if (startDate.isAfter(endDate)) {
                    throw new IllegalArgumentException("Start date cannot be after end date.");
                }
            }
        }
    }
}
