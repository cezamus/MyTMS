package pl.cm.mytms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.cm.mytms.model.Assignment;
import pl.cm.mytms.model.Trailer;
import pl.cm.mytms.model.Vehicle;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findAllByVehicle(Vehicle vehicle);

    List<Assignment> findAllByTrailer(Trailer trailer);

    void deleteAllByVehicle(Vehicle vehicle);

    void deleteAllByTrailer(Trailer trailer);
}
