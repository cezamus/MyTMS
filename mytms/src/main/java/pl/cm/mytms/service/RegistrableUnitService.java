package pl.cm.mytms.service;

public interface RegistrableUnitService {

    void create(String registrationId);

    void update(Long vehicleId, String registrationId);

    void update(String currentRegistrationId, String newRegistrationId);

    void delete(Long vehicleId);

    void delete(String registrationId);
}
