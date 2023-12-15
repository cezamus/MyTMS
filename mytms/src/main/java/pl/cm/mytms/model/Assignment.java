package pl.cm.mytms.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table
public class Assignment {

    private static final String ASSIGNMENT_SEQUENCE = "assignment_sequence";

    @Id
    @SequenceGenerator(
            name = ASSIGNMENT_SEQUENCE,
            sequenceName = ASSIGNMENT_SEQUENCE,
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = ASSIGNMENT_SEQUENCE
    )
    private Long id;
    @ManyToOne
    private Vehicle vehicle;
    @ManyToOne
    private Trailer trailer;
    private Instant startDate;
    private Instant endDate;

    public Assignment(Long id, Vehicle vehicle, Trailer trailer, Instant startDate, Instant endDate) {
        this.id = id;
        this.vehicle = vehicle;
        this.trailer = trailer;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Assignment(Vehicle vehicle, Trailer trailer, Instant startDate, Instant endDate) {
        this.vehicle = vehicle;
        this.trailer = trailer;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Assignment(Vehicle vehicle, Trailer trailer) {
        this.vehicle = vehicle;
        this.trailer = trailer;
    }

    public Assignment() {
    }

    public Long getId() {
        return id;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Trailer getTrailer() {
        return trailer;
    }

    public void setTrailer(Trailer trailer) {
        this.trailer = trailer;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "Assignment{" +
                "id=" + id +
                ", vehicle=" + vehicle +
                ", trailer=" + trailer +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
