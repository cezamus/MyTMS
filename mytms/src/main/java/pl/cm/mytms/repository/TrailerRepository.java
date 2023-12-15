package pl.cm.mytms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.cm.mytms.model.Trailer;

import java.util.Optional;

public interface TrailerRepository extends JpaRepository<Trailer, Long> {

    Optional<Trailer> findTrailerByRegistrationId(String registrationId);
}
