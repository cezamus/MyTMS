package pl.cm.mytms.dto;

import pl.cm.mytms.model.Trailer;

import java.time.Instant;

public record TrailerAssignmentDTO(Long id, Trailer trailer, Instant startDate, Instant endDate) {
}
