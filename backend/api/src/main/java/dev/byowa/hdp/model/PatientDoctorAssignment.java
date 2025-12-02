package dev.byowa.hdp.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
        name = "patient_doctor_assignment",
        schema = "omop_cdm",
        uniqueConstraints = @UniqueConstraint(columnNames = {"patient_user_id", "doctor_user_id"})
)
public class PatientDoctorAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_user_id", nullable = false)
    private User patient;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_user_id", nullable = false)
    private User doctor;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public PatientDoctorAssignment() {
    }

    public PatientDoctorAssignment(User patient, User doctor) {
        this.patient = patient;
        this.doctor = doctor;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public User getPatient() {
        return patient;
    }

    public void setPatient(User patient) {
        this.patient = patient;
    }

    public User getDoctor() {
        return doctor;
    }

    public void setDoctor(User doctor) {
        this.doctor = doctor;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
