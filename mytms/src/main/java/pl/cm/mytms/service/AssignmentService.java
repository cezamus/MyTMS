package pl.cm.mytms.service;

import pl.cm.mytms.model.Assignment;

import java.time.Instant;
import java.util.List;

public interface AssignmentService {

    void create(Long vehicleId, Long trailerId, Instant startDate, Instant endDate);

    void create(String vehicleRegistrationId, String trailerRegistrationId, Instant startDate, Instant endDate);

    List<Assignment> getAll();

    void forceCreate(Long vehicleId, Long trailerId, Instant startDate, Instant endDate);

    void forceCreate(String vehicleRegistrationId, String trailerRegistrationId, Instant startDate, Instant endDate);
}
