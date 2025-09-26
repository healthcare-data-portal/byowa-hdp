package dev.byowa.hdp.model.clinical;

import dev.byowa.hdp.model.vocabulary.Concept;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "specimen", schema = "omop_cdm")
public class Speciman {
    @Id
    @Column(name = "specimen_id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "specimen_concept_id", nullable = false)
    private Concept specimenConcept;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "specimen_type_concept_id", nullable = false)
    private Concept specimenTypeConcept;

    @NotNull
    @Column(name = "specimen_date", nullable = false)
    private LocalDate specimenDate;

    @Column(name = "specimen_datetime")
    private Instant specimenDatetime;

    @Column(name = "quantity")
    private BigDecimal quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_concept_id")
    private Concept unitConcept;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anatomic_site_concept_id")
    private Concept anatomicSiteConcept;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disease_status_concept_id")
    private Concept diseaseStatusConcept;

    @Size(max = 50)
    @Column(name = "specimen_source_id", length = 50)
    private String specimenSourceId;

    @Size(max = 50)
    @Column(name = "specimen_source_value", length = 50)
    private String specimenSourceValue;

    @Size(max = 50)
    @Column(name = "unit_source_value", length = 50)
    private String unitSourceValue;

    @Size(max = 50)
    @Column(name = "anatomic_site_source_value", length = 50)
    private String anatomicSiteSourceValue;

    @Size(max = 50)
    @Column(name = "disease_status_source_value", length = 50)
    private String diseaseStatusSourceValue;

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

    public Concept getSpecimenConcept() {
        return specimenConcept;
    }

    public void setSpecimenConcept(Concept specimenConcept) {
        this.specimenConcept = specimenConcept;
    }

    public Concept getSpecimenTypeConcept() {
        return specimenTypeConcept;
    }

    public void setSpecimenTypeConcept(Concept specimenTypeConcept) {
        this.specimenTypeConcept = specimenTypeConcept;
    }

    public LocalDate getSpecimenDate() {
        return specimenDate;
    }

    public void setSpecimenDate(LocalDate specimenDate) {
        this.specimenDate = specimenDate;
    }

    public Instant getSpecimenDatetime() {
        return specimenDatetime;
    }

    public void setSpecimenDatetime(Instant specimenDatetime) {
        this.specimenDatetime = specimenDatetime;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public Concept getUnitConcept() {
        return unitConcept;
    }

    public void setUnitConcept(Concept unitConcept) {
        this.unitConcept = unitConcept;
    }

    public Concept getAnatomicSiteConcept() {
        return anatomicSiteConcept;
    }

    public void setAnatomicSiteConcept(Concept anatomicSiteConcept) {
        this.anatomicSiteConcept = anatomicSiteConcept;
    }

    public Concept getDiseaseStatusConcept() {
        return diseaseStatusConcept;
    }

    public void setDiseaseStatusConcept(Concept diseaseStatusConcept) {
        this.diseaseStatusConcept = diseaseStatusConcept;
    }

    public String getSpecimenSourceId() {
        return specimenSourceId;
    }

    public void setSpecimenSourceId(String specimenSourceId) {
        this.specimenSourceId = specimenSourceId;
    }

    public String getSpecimenSourceValue() {
        return specimenSourceValue;
    }

    public void setSpecimenSourceValue(String specimenSourceValue) {
        this.specimenSourceValue = specimenSourceValue;
    }

    public String getUnitSourceValue() {
        return unitSourceValue;
    }

    public void setUnitSourceValue(String unitSourceValue) {
        this.unitSourceValue = unitSourceValue;
    }

    public String getAnatomicSiteSourceValue() {
        return anatomicSiteSourceValue;
    }

    public void setAnatomicSiteSourceValue(String anatomicSiteSourceValue) {
        this.anatomicSiteSourceValue = anatomicSiteSourceValue;
    }

    public String getDiseaseStatusSourceValue() {
        return diseaseStatusSourceValue;
    }

    public void setDiseaseStatusSourceValue(String diseaseStatusSourceValue) {
        this.diseaseStatusSourceValue = diseaseStatusSourceValue;
    }

}