package pl.cm.mytms.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.cm.mytms.model.Trailer;
import pl.cm.mytms.repository.AssignmentRepository;
import pl.cm.mytms.repository.TrailerRepository;
import pl.cm.mytms.service.TrailerService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class TrailerServiceImpl implements TrailerService {

    private final TrailerRepository trailerRepository;
    private final AssignmentRepository assignmentRepository;

    @Autowired
    public TrailerServiceImpl(TrailerRepository trailerRepository, AssignmentRepository assignmentRepository) {
        this.trailerRepository = trailerRepository;
        this.assignmentRepository = assignmentRepository;
    }

    @Override
    public void create(String registrationId) {
        checkIfRegistrationIdIsValid(registrationId);
        checkIfRegistrationIdIsAlreadyInUse(registrationId);
        trailerRepository.save(new Trailer(registrationId));
    }

    @Override
    public List<Trailer> getAll() {
        return trailerRepository.findAll();
    }

    @Override
    @Transactional
    public void update(Long id, String registrationId) {
        checkIfRegistrationIdIsValid(registrationId);
        checkIfTrailerExists(id);
        checkIfRegistrationIdIsAlreadyInUse(registrationId);
        Trailer trailer = trailerRepository.getReferenceById(id);
        trailer.setRegistrationId(registrationId);
    }

    @Override
    @Transactional
    public void update(String currentRegistrationId, String newRegistrationId) {
        checkIfRegistrationIdIsValid(newRegistrationId);
        checkIfRegistrationIdIsAlreadyInUse(newRegistrationId);
        Trailer trailer = getIfPossible(currentRegistrationId);
        trailer.setRegistrationId(newRegistrationId);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        checkIfTrailerExists(id);
        assignmentRepository.deleteAllByTrailer(trailerRepository.getReferenceById(id));
        trailerRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void delete(String registrationId) {
        Trailer trailer = getIfPossible(registrationId);
        assignmentRepository.deleteAllByTrailer(trailer);
        trailerRepository.delete(trailer);
    }

    // TODO: Replace with custom exceptions.
    private void checkIfRegistrationIdIsValid(String registrationId) {
        if (registrationId == null || registrationId.isBlank()) {
            throw new IllegalArgumentException("Registration ID cannot be null or blank.");
        }
    }

    private void checkIfRegistrationIdIsAlreadyInUse(String registrationId) {
        if (trailerRepository.findTrailerByRegistrationId(registrationId).isPresent()) {
            throw new IllegalStateException("Registration ID " + registrationId + " is already in use.");
        }
    }

    private void checkIfTrailerExists(Long id) {
        if (!trailerRepository.existsById(id)) {
            throw new NoSuchElementException("Trailer with ID " + id + " has not been found.");
        }
    }

    private Trailer getIfPossible(String registrationId) {
        Optional<Trailer> trailer = trailerRepository.findTrailerByRegistrationId(registrationId);
        if (trailer.isPresent()) {
            return trailer.get();
        } else {
            throw new NoSuchElementException("Trailer with Registration ID " + registrationId + " has not been found.");
        }
    }
}
