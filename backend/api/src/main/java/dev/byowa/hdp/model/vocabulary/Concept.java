package dev.byowa.hdp.model.vocabulary;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Entity
@Table(name = "concept", schema = "omop_cdm")
public class Concept {
    @Id
    @Column(name = "concept_id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "concept_name", nullable = false)
    private String conceptName;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "domain_id", nullable = false)
    private Domain domain;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vocabulary_id", nullable = false)
    private Vocabulary vocabulary;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "concept_class_id", nullable = false)
    private ConceptClass conceptClass;

    @Size(max = 1)
    @Column(name = "standard_concept", length = 1)
    private String standardConcept;

    @Size(max = 50)
    @NotNull
    @Column(name = "concept_code", nullable = false, length = 50)
    private String conceptCode;

    @NotNull
    @Column(name = "valid_start_date", nullable = false)
    private LocalDate validStartDate;

    @NotNull
    @Column(name = "valid_end_date", nullable = false)
    private LocalDate validEndDate;

    @Size(max = 1)
    @Column(name = "invalid_reason", length = 1)
    private String invalidReason;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getConceptName() {
        return conceptName;
    }

    public void setConceptName(String conceptName) {
        this.conceptName = conceptName;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public Vocabulary getVocabulary() {
        return vocabulary;
    }

    public void setVocabulary(Vocabulary vocabulary) {
        this.vocabulary = vocabulary;
    }

    public ConceptClass getConceptClass() {
        return conceptClass;
    }

    public void setConceptClass(ConceptClass conceptClass) {
        this.conceptClass = conceptClass;
    }

    public String getStandardConcept() {
        return standardConcept;
    }

    public void setStandardConcept(String standardConcept) {
        this.standardConcept = standardConcept;
    }

    public String getConceptCode() {
        return conceptCode;
    }

    public void setConceptCode(String conceptCode) {
        this.conceptCode = conceptCode;
    }

    public LocalDate getValidStartDate() {
        return validStartDate;
    }

    public void setValidStartDate(LocalDate validStartDate) {
        this.validStartDate = validStartDate;
    }

    public LocalDate getValidEndDate() {
        return validEndDate;
    }

    public void setValidEndDate(LocalDate validEndDate) {
        this.validEndDate = validEndDate;
    }

    public String getInvalidReason() {
        return invalidReason;
    }

    public void setInvalidReason(String invalidReason) {
        this.invalidReason = invalidReason;
    }

}