package pl.cm.mytms.model;

import jakarta.persistence.*;

@Entity
@Table
public class Vehicle {

    private static final String VEHICLE_SEQUENCE = "vehicle_sequence";

    @Id
    @SequenceGenerator(
            name = VEHICLE_SEQUENCE,
            sequenceName = VEHICLE_SEQUENCE,
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = VEHICLE_SEQUENCE
    )
    private Long id;
    @Column(nullable = false, unique = true)
    private String registrationId;

    public Vehicle(Long id, String registrationId) {
        this.id = id;
        this.registrationId = registrationId;
    }

    public Vehicle(String registrationId) {
        this.registrationId = registrationId;
    }

    public Vehicle() {
    }

    public Long getId() {
        return id;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", RegistrationId='" + registrationId + '\'' +
                '}';
    }
}
