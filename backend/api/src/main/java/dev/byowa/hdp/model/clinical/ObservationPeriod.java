package dev.byowa.hdp.model.clinical;

import dev.byowa.hdp.model.vocabulary.Concept;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Table(name = "observation_period", schema = "omop_cdm")
public class ObservationPeriod {
    @Id
    @Column(name = "observation_period_id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @NotNull
    @Column(name = "observation_period_start_date", nullable = false)
    private LocalDate observationPeriodStartDate;

    @NotNull
    @Column(name = "observation_period_end_date", nullable = false)
    private LocalDate observationPeriodEndDate;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "period_type_concept_id", nullable = false)
    private Concept periodTypeConcept;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public LocalDate getObservationPeriodStartDate() {
        return observationPeriodStartDate;
    }

    public void setObservationPeriodStartDate(LocalDate observationPeriodStartDate) {
        this.observationPeriodStartDate = observationPeriodStartDate;
    }

    public LocalDate getObservationPeriodEndDate() {
        return observationPeriodEndDate;
    }

    public void setObservationPeriodEndDate(LocalDate observationPeriodEndDate) {
        this.observationPeriodEndDate = observationPeriodEndDate;
    }

    public Concept getPeriodTypeConcept() {
        return periodTypeConcept;
    }

    public void setPeriodTypeConcept(Concept periodTypeConcept) {
        this.periodTypeConcept = periodTypeConcept;
    }

}