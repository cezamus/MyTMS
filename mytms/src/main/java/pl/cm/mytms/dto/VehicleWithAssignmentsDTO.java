package pl.cm.mytms.dto;

import java.util.List;

public class VehicleWithAssignmentsDTO {

    private final Long id;
    private final String registrationId;
    private final List<TrailerAssignmentDTO> trailerAssignments;

    public VehicleWithAssignmentsDTO(Long id, String registrationId, List<TrailerAssignmentDTO> assignedTrailers) {
        this.id = id;
        this.registrationId = registrationId;
        this.trailerAssignments = assignedTrailers;
    }

    public Long getId() {
        return id;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public List<TrailerAssignmentDTO> getTrailerAssignments() {
        return trailerAssignments;
    }
}
