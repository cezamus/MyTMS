package pl.cm.mytms.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.cm.mytms.model.Trailer;
import pl.cm.mytms.repository.AssignmentRepository;
import pl.cm.mytms.repository.TrailerRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class TrailerServiceImplTest {

    private static final String CORRECT_REGISTRATION_ID = "AB00000";
    private static final String NEW_REGISTRATION_ID = "AB00001";
    private static final String REGISTRATION_ID_OF_EXISTING_TRAILER = "AB00002";
    private static final String NULL_REGISTRATION_ID = null;
    private static final String BLANK_REGISTRATION_ID = "";
    private static final Long ID_OF_EXISTING_TRAILER = 1L;
    private static final Long NEW_ID = 2L;
    private static final Long NULL_ID = null;

    @Mock
    TrailerRepository trailerRepository;
    @Mock
    private AssignmentRepository assignmentRepository;
    @Mock
    Trailer trailer;

    @InjectMocks
    TrailerServiceImpl trailerService;

    @Test
    void shouldCreateTrailerIfDataIsCorrect() {
        // given
        when(trailerRepository.findTrailerByRegistrationId(CORRECT_REGISTRATION_ID)).thenReturn(Optional.empty());
        // when
        trailerService.create(CORRECT_REGISTRATION_ID);
        // then
        verify(trailerRepository).save(any());
    }

    @Test
    void shouldUpdateTrailerByIdIfDataIsCorrect() {
        // given
        when(trailerRepository.existsById(ID_OF_EXISTING_TRAILER)).thenReturn(true);
        when(trailerRepository.findTrailerByRegistrationId(NEW_REGISTRATION_ID)).thenReturn(Optional.empty());
        when(trailerRepository.getReferenceById(ID_OF_EXISTING_TRAILER)).thenReturn(trailer);
        // when
        trailerService.update(ID_OF_EXISTING_TRAILER, NEW_REGISTRATION_ID);
        // then
        verify(trailer).setRegistrationId(NEW_REGISTRATION_ID);
    }

    @Test
    void shouldUpdateVehicleByRegistrationIdIfDataIsCorrect() {
        // given
        when(trailerRepository.findTrailerByRegistrationId(NEW_REGISTRATION_ID))
                .thenReturn(Optional.empty());
        when(trailerRepository.findTrailerByRegistrationId(REGISTRATION_ID_OF_EXISTING_TRAILER))
                .thenReturn(Optional.of(trailer));
        // when
        trailerService.update(REGISTRATION_ID_OF_EXISTING_TRAILER, NEW_REGISTRATION_ID);
        // then
        verify(trailer).setRegistrationId(NEW_REGISTRATION_ID);
    }

    @Test
    void shouldThrowExceptionWhenRegistrationIdIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> trailerService.create(NULL_REGISTRATION_ID));
        assertThrows(IllegalArgumentException.class,
                () -> trailerService.update(ID_OF_EXISTING_TRAILER, NULL_REGISTRATION_ID));
        assertThrows(IllegalArgumentException.class,
                () -> trailerService.update(REGISTRATION_ID_OF_EXISTING_TRAILER, NULL_REGISTRATION_ID));
    }

    @Test
    void shouldThrowExceptionWhenRegistrationIdIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> trailerService.create(BLANK_REGISTRATION_ID));
        assertThrows(IllegalArgumentException.class,
                () -> trailerService.update(ID_OF_EXISTING_TRAILER, BLANK_REGISTRATION_ID));
        assertThrows(IllegalArgumentException.class,
                () -> trailerService.update(REGISTRATION_ID_OF_EXISTING_TRAILER, BLANK_REGISTRATION_ID));    }

    @Test
    void shouldThrowExceptionWhenRegistrationIdIsDuplicated() {
        when(trailerRepository.findTrailerByRegistrationId(REGISTRATION_ID_OF_EXISTING_TRAILER))
                .thenReturn(Optional.of(new Trailer()));
        when(trailerRepository.existsById(ID_OF_EXISTING_TRAILER))
                .thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> trailerService.create(REGISTRATION_ID_OF_EXISTING_TRAILER));
        assertThrows(IllegalStateException.class,
                () -> trailerService.update(ID_OF_EXISTING_TRAILER, REGISTRATION_ID_OF_EXISTING_TRAILER));
        assertThrows(IllegalStateException.class,
                () -> trailerService.update(REGISTRATION_ID_OF_EXISTING_TRAILER, REGISTRATION_ID_OF_EXISTING_TRAILER));
    }

    @Test
    void shouldDeleteVehicleByIdIfDataIsCorrect(){
        // given
        when(trailerRepository.existsById(ID_OF_EXISTING_TRAILER)).thenReturn(true);
        doNothing().when(assignmentRepository).deleteAllByTrailer(any());
        // when
        trailerService.delete(ID_OF_EXISTING_TRAILER);
        // then
        verify(trailerRepository).deleteById(any());
    }

    @Test
    void shouldDeleteVehicleByRegistrationIdIfDataIsCorrect(){
        // given
        when(trailerRepository.findTrailerByRegistrationId(REGISTRATION_ID_OF_EXISTING_TRAILER))
                .thenReturn(Optional.of(trailer));
        doNothing().when(assignmentRepository).deleteAllByTrailer(trailer);
        // when
        trailerService.delete(REGISTRATION_ID_OF_EXISTING_TRAILER);
        // then
        verify(trailerRepository).delete(trailer);
    }

    @Test
    void shouldThrowExceptionWhenIdIsUnknown() {
        assertThrows(NoSuchElementException.class,
                () -> trailerService.update(NULL_ID, CORRECT_REGISTRATION_ID));
        assertThrows(NoSuchElementException.class,
                () -> trailerService.update(NEW_ID, CORRECT_REGISTRATION_ID));
        assertThrows(NoSuchElementException.class,
                () -> trailerService.update(NULL_REGISTRATION_ID, CORRECT_REGISTRATION_ID));
        assertThrows(NoSuchElementException.class,
                () -> trailerService.update(NEW_REGISTRATION_ID, CORRECT_REGISTRATION_ID));
        assertThrows(NoSuchElementException.class,
                () -> trailerService.delete(NULL_ID));
        assertThrows(NoSuchElementException.class,
                () -> trailerService.delete(NEW_ID));
        assertThrows(NoSuchElementException.class,
                () -> trailerService.delete(NULL_REGISTRATION_ID));
        assertThrows(NoSuchElementException.class,
                () -> trailerService.delete(NEW_REGISTRATION_ID));
    }
}