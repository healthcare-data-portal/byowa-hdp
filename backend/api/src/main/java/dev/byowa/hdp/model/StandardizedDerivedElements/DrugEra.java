package dev.byowa.hdp.model.StandardizedDerivedElements;

import dev.byowa.hdp.model.clinical.Person;
import dev.byowa.hdp.model.vocabulary.Concept;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@Entity
@Table(name = "drug_era", schema = "omop_cdm")
public class DrugEra {
    @Id
    @Column(name = "drug_era_id", nullable = false)
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
    @Column(name = "drug_era_start_date", nullable = false)
    private Instant drugEraStartDate;

    @NotNull
    @Column(name = "drug_era_end_date", nullable = false)
    private Instant drugEraEndDate;

    @Column(name = "drug_exposure_count")
    private Integer drugExposureCount;

    @Column(name = "gap_days")
    private Integer gapDays;

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

    public Instant getDrugEraStartDate() {
        return drugEraStartDate;
    }

    public void setDrugEraStartDate(Instant drugEraStartDate) {
        this.drugEraStartDate = drugEraStartDate;
    }

    public Instant getDrugEraEndDate() {
        return drugEraEndDate;
    }

    public void setDrugEraEndDate(Instant drugEraEndDate) {
        this.drugEraEndDate = drugEraEndDate;
    }

    public Integer getDrugExposureCount() {
        return drugExposureCount;
    }

    public void setDrugExposureCount(Integer drugExposureCount) {
        this.drugExposureCount = drugExposureCount;
    }

    public Integer getGapDays() {
        return gapDays;
    }

    public void setGapDays(Integer gapDays) {
        this.gapDays = gapDays;
    }

}