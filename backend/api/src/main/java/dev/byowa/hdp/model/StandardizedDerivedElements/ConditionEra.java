package dev.byowa.hdp.model.StandardizedDerivedElements;

import dev.byowa.hdp.model.clinical.Person;
import dev.byowa.hdp.model.vocabulary.Concept;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@Entity
@Table(name = "condition_era", schema = "omop_cdm")
public class ConditionEra {
    @Id
    @Column(name = "condition_era_id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "condition_concept_id", nullable = false)
    private Concept conditionConcept;

    @NotNull
    @Column(name = "condition_era_start_date", nullable = false)
    private Instant conditionEraStartDate;

    @NotNull
    @Column(name = "condition_era_end_date", nullable = false)
    private Instant conditionEraEndDate;

    @Column(name = "condition_occurrence_count")
    private Integer conditionOccurrenceCount;

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

    public Concept getConditionConcept() {
        return conditionConcept;
    }

    public void setConditionConcept(Concept conditionConcept) {
        this.conditionConcept = conditionConcept;
    }

    public Instant getConditionEraStartDate() {
        return conditionEraStartDate;
    }

    public void setConditionEraStartDate(Instant conditionEraStartDate) {
        this.conditionEraStartDate = conditionEraStartDate;
    }

    public Instant getConditionEraEndDate() {
        return conditionEraEndDate;
    }

    public void setConditionEraEndDate(Instant conditionEraEndDate) {
        this.conditionEraEndDate = conditionEraEndDate;
    }

    public Integer getConditionOccurrenceCount() {
        return conditionOccurrenceCount;
    }

    public void setConditionOccurrenceCount(Integer conditionOccurrenceCount) {
        this.conditionOccurrenceCount = conditionOccurrenceCount;
    }

}