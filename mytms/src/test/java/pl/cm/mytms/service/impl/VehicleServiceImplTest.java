package pl.cm.mytms.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.cm.mytms.model.Vehicle;
import pl.cm.mytms.repository.AssignmentRepository;
import pl.cm.mytms.repository.VehicleRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceImplTest {

    private static final String CORRECT_REGISTRATION_ID = "AB00000";
    private static final String NEW_REGISTRATION_ID = "AB00001";
    private static final String REGISTRATION_ID_OF_EXISTING_VEHICLE = "AB00002";
    private static final String NULL_REGISTRATION_ID = null;
    private static final String BLANK_REGISTRATION_ID = "";
    private static final Long ID_OF_EXISTING_VEHICLE = 1L;
    private static final Long NEW_ID = 2L;
    private static final Long NULL_ID = null;

    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private AssignmentRepository assignmentRepository;
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
    void shouldUpdateVehicleByIdIfDataIsCorrect() {
        // given
        when(vehicleRepository.existsById(ID_OF_EXISTING_VEHICLE)).thenReturn(true);
        when(vehicleRepository.findVehicleByRegistrationId(NEW_REGISTRATION_ID)).thenReturn(Optional.empty());
        when(vehicleRepository.getReferenceById(ID_OF_EXISTING_VEHICLE)).thenReturn(vehicle);
        // when
        vehicleService.update(ID_OF_EXISTING_VEHICLE, NEW_REGISTRATION_ID);
        // then
        verify(vehicle).setRegistrationId(NEW_REGISTRATION_ID);
    }

    @Test
    void shouldUpdateVehicleByRegistrationIdIfDataIsCorrect() {
        // given
        when(vehicleRepository.findVehicleByRegistrationId(NEW_REGISTRATION_ID))
                .thenReturn(Optional.empty());
        when(vehicleRepository.findVehicleByRegistrationId(REGISTRATION_ID_OF_EXISTING_VEHICLE))
                .thenReturn(Optional.of(vehicle));
        // when
        vehicleService.update(REGISTRATION_ID_OF_EXISTING_VEHICLE, NEW_REGISTRATION_ID);
        // then
        verify(vehicle).setRegistrationId(NEW_REGISTRATION_ID);
    }

    @Test
    void shouldThrowExceptionWhenNewRegistrationIdIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> vehicleService.create(NULL_REGISTRATION_ID));
        assertThrows(IllegalArgumentException.class,
                () -> vehicleService.update(ID_OF_EXISTING_VEHICLE, NULL_REGISTRATION_ID));
        assertThrows(IllegalArgumentException.class,
                () -> vehicleService.update(REGISTRATION_ID_OF_EXISTING_VEHICLE, NULL_REGISTRATION_ID));
    }

    @Test
    void shouldThrowExceptionWhenNewRegistrationIdIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> vehicleService.create(BLANK_REGISTRATION_ID));
        assertThrows(IllegalArgumentException.class,
                () -> vehicleService.update(ID_OF_EXISTING_VEHICLE, BLANK_REGISTRATION_ID));
        assertThrows(IllegalArgumentException.class,
                () -> vehicleService.update(REGISTRATION_ID_OF_EXISTING_VEHICLE, BLANK_REGISTRATION_ID));
    }

    @Test
    void shouldThrowExceptionWhenRegistrationIdIsDuplicated() {
        when(vehicleRepository.findVehicleByRegistrationId(REGISTRATION_ID_OF_EXISTING_VEHICLE))
                .thenReturn(Optional.of(new Vehicle()));
        when(vehicleRepository.existsById(ID_OF_EXISTING_VEHICLE))
                .thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> vehicleService.create(REGISTRATION_ID_OF_EXISTING_VEHICLE));
        assertThrows(IllegalStateException.class,
                () -> vehicleService.update(ID_OF_EXISTING_VEHICLE, REGISTRATION_ID_OF_EXISTING_VEHICLE));
        assertThrows(IllegalStateException.class,
                () -> vehicleService.update(REGISTRATION_ID_OF_EXISTING_VEHICLE, REGISTRATION_ID_OF_EXISTING_VEHICLE));
    }

    @Test
    void shouldDeleteVehicleByIdIfDataIsCorrect(){
        // given
        when(vehicleRepository.existsById(ID_OF_EXISTING_VEHICLE)).thenReturn(true);
        doNothing().when(assignmentRepository).deleteAllByVehicle(any());
        // when
        vehicleService.delete(ID_OF_EXISTING_VEHICLE);
        // then
        verify(vehicleRepository).deleteById(any());
    }

    @Test
    void shouldDeleteVehicleByRegistrationIdIfDataIsCorrect(){
        // given
        when(vehicleRepository.findVehicleByRegistrationId(REGISTRATION_ID_OF_EXISTING_VEHICLE))
                .thenReturn(Optional.of(vehicle));
        doNothing().when(assignmentRepository).deleteAllByVehicle(vehicle);
        // when
        vehicleService.delete(REGISTRATION_ID_OF_EXISTING_VEHICLE);
        // then
        verify(vehicleRepository).delete(vehicle);
    }

    @Test
    void shouldThrowExceptionWhenIdIsUnknown() {
        assertThrows(NoSuchElementException.class,
                () -> vehicleService.update(NULL_ID, CORRECT_REGISTRATION_ID));
        assertThrows(NoSuchElementException.class,
                () -> vehicleService.update(NEW_ID, CORRECT_REGISTRATION_ID));
        assertThrows(NoSuchElementException.class,
                () -> vehicleService.update(NULL_REGISTRATION_ID, CORRECT_REGISTRATION_ID));
        assertThrows(NoSuchElementException.class,
                () -> vehicleService.update(NEW_REGISTRATION_ID, CORRECT_REGISTRATION_ID));
        assertThrows(NoSuchElementException.class,
                () -> vehicleService.delete(NULL_ID));
        assertThrows(NoSuchElementException.class,
                () -> vehicleService.delete(NEW_ID));
        assertThrows(NoSuchElementException.class,
                () -> vehicleService.delete(NULL_REGISTRATION_ID));
        assertThrows(NoSuchElementException.class,
                () -> vehicleService.delete(NEW_REGISTRATION_ID));
    }
}