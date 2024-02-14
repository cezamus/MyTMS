package pl.cm.mytms.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.cm.mytms.model.Vehicle;
import pl.cm.mytms.repository.VehicleRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceImplTest {

    private static final String CORRECT_REGISTRATION_ID = "AB12345";
    private static final String REPLACEMENT_REGISTRATION_ID = "AB67890";
    private static final String NULL_REGISTRATION_ID = null;
    private static final String BLANK_REGISTRATION_ID = "";
    private static final Long ID = 1L;

    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private Vehicle vehicle;

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    @Test
    void shouldCreateVehicleIfDataIsCorrect() {
        // given
        when(vehicleRepository.findVehicleByRegistrationId(CORRECT_REGISTRATION_ID)).thenReturn(Optional.empty());
        // when
        vehicleService.create(CORRECT_REGISTRATION_ID);
        // then
        verify(vehicleRepository).save(any());
    }

    @Test
    void shouldThrowExceptionWhenRegistrationIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> vehicleService.create(NULL_REGISTRATION_ID));
    }

    @Test
    void shouldThrowExceptionWhenRegistrationIdIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> vehicleService.create(BLANK_REGISTRATION_ID));
    }

    @Test
    void shouldThrowExceptionWhenRegistrationIdIsDuplicated() {
        when(vehicleRepository.findVehicleByRegistrationId(CORRECT_REGISTRATION_ID))
                .thenReturn(Optional.of(new Vehicle()));
        assertThrows(IllegalStateException.class, () -> vehicleService.create(CORRECT_REGISTRATION_ID));
    }

    @Test
    void shouldUpdateVehicleByIdIfDataIsCorrect() {
        // given
        when(vehicleRepository.existsById(ID)).thenReturn(true);
        when(vehicleRepository.findVehicleByRegistrationId(REPLACEMENT_REGISTRATION_ID)).thenReturn(Optional.empty());
        when(vehicleRepository.getReferenceById(ID)).thenReturn(vehicle);
        // when
        vehicleService.update(ID, REPLACEMENT_REGISTRATION_ID);
        // then
        verify(vehicle).setRegistrationId(REPLACEMENT_REGISTRATION_ID);
    }
}