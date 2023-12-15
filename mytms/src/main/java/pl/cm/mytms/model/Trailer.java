package pl.cm.mytms.model;

import jakarta.persistence.*;

@Entity
@Table
public class Trailer {

    private static final String TRAILER_SEQUENCE = "trailer_sequence";

    @Id
    @SequenceGenerator(
            name = TRAILER_SEQUENCE,
            sequenceName = TRAILER_SEQUENCE,
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = TRAILER_SEQUENCE
    )
    private Long id;
    @Column(nullable = false, unique = true)
    private String registrationId;

    public Trailer(Long id, String registrationId) {
        this.id = id;
        this.registrationId = registrationId;
    }

    public Trailer(String registrationId) {
        this.registrationId = registrationId;
    }

    public Trailer() {
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
        return "Trailer{" +
                "id=" + id +
                ", registrationId='" + registrationId + '\'' +
                '}';
    }
}

