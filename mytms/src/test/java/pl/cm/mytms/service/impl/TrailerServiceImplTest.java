package pl.cm.mytms.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.cm.mytms.model.Trailer;
import pl.cm.mytms.repository.TrailerRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrailerServiceImplTest {

    private static final String CORRECT_REGISTRATION_ID = "AB12345";
    private static final String NULL_REGISTRATION_ID = null;
    private static final String BLANK_REGISTRATION_ID = "";

    @Mock
    TrailerRepository trailerRepository;

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
    void shouldThrowExceptionWhenRegistrationIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> trailerService.create(NULL_REGISTRATION_ID));
    }

    @Test
    void shouldThrowExceptionWhenRegistrationIdIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> trailerService.create(BLANK_REGISTRATION_ID));
    }

    @Test
    void shouldThrowExceptionWhenRegistrationIdIsDuplicated() {
        when(trailerRepository.findTrailerByRegistrationId(CORRECT_REGISTRATION_ID))
                .thenReturn(Optional.of(new Trailer()));
        assertThrows(IllegalStateException.class, () -> trailerService.create(CORRECT_REGISTRATION_ID));
    }
}