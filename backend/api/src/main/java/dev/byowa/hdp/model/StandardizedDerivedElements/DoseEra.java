package dev.byowa.hdp.model.StandardizedDerivedElements;

import dev.byowa.hdp.model.clinical.Person;
import dev.byowa.hdp.model.vocabulary.Concept;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "dose_era", schema = "omop_cdm")
public class DoseEra {
    @Id
    @Column(name = "dose_era_id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "drug_concept_id", nullable = false)
    private Concept drugConcept;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unit_concept_id", nullable = false)
    private Concept unitConcept;

    @NotNull
    @Column(name = "dose_value", nullable = false)
    private BigDecimal doseValue;

    @NotNull
    @Column(name = "dose_era_start_date", nullable = false)
    private Instant doseEraStartDate;

    @NotNull
    @Column(name = "dose_era_end_date", nullable = false)
    private Instant doseEraEndDate;

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

    public Concept getDrugConcept() {
        return drugConcept;
    }

    public void setDrugConcept(Concept drugConcept) {
        this.drugConcept = drugConcept;
    }

    public Concept getUnitConcept() {
        return unitConcept;
    }

    public void setUnitConcept(Concept unitConcept) {
        this.unitConcept = unitConcept;
    }

    public BigDecimal getDoseValue() {
        return doseValue;
    }

    public void setDoseValue(BigDecimal doseValue) {
        this.doseValue = doseValue;
    }

    public Instant getDoseEraStartDate() {
        return doseEraStartDate;
    }

    public void setDoseEraStartDate(Instant doseEraStartDate) {
        this.doseEraStartDate = doseEraStartDate;
    }

    public Instant getDoseEraEndDate() {
        return doseEraEndDate;
    }

    public void setDoseEraEndDate(Instant doseEraEndDate) {
        this.doseEraEndDate = doseEraEndDate;
    }

}