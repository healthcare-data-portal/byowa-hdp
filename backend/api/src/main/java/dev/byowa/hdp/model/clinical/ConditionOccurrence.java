package dev.byowa.hdp.model.clinical;

import dev.byowa.hdp.model.healthsystem.Provider;
import dev.byowa.hdp.model.vocabulary.Concept;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "condition_occurrence", schema = "omop_cdm")
public class ConditionOccurrence {
    @Id
    @Column(name = "condition_occurrence_id", nullable = false)
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
    @Column(name = "condition_start_date", nullable = false)
    private LocalDate conditionStartDate;

    @Column(name = "condition_start_datetime")
    private Instant conditionStartDatetime;

    @Column(name = "condition_end_date")
    private LocalDate conditionEndDate;

    @Column(name = "condition_end_datetime")
    private Instant conditionEndDatetime;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "condition_type_concept_id", nullable = false)
    private Concept conditionTypeConcept;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "condition_status_concept_id")
    private Concept conditionStatusConcept;

    @Size(max = 20)
    @Column(name = "stop_reason", length = 20)
    private String stopReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    private Provider provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_occurrence_id")
    private VisitOccurrence visitOccurrence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_detail_id")
    private VisitDetail visitDetail;

    @Size(max = 50)
    @Column(name = "condition_source_value", length = 50)
    private String conditionSourceValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "condition_source_concept_id")
    private Concept conditionSourceConcept;

    @Size(max = 50)
    @Column(name = "condition_status_source_value", length = 50)
    private String conditionStatusSourceValue;

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

    public LocalDate getConditionStartDate() {
        return conditionStartDate;
    }

    public void setConditionStartDate(LocalDate conditionStartDate) {
        this.conditionStartDate = conditionStartDate;
    }

    public Instant getConditionStartDatetime() {
        return conditionStartDatetime;
    }

    public void setConditionStartDatetime(Instant conditionStartDatetime) {
        this.conditionStartDatetime = conditionStartDatetime;
    }

    public LocalDate getConditionEndDate() {
        return conditionEndDate;
    }

    public void setConditionEndDate(LocalDate conditionEndDate) {
        this.conditionEndDate = conditionEndDate;
    }

    public Instant getConditionEndDatetime() {
        return conditionEndDatetime;
    }

    public void setConditionEndDatetime(Instant conditionEndDatetime) {
        this.conditionEndDatetime = conditionEndDatetime;
    }

    public Concept getConditionTypeConcept() {
        return conditionTypeConcept;
    }

    public void setConditionTypeConcept(Concept conditionTypeConcept) {
        this.conditionTypeConcept = conditionTypeConcept;
    }

    public Concept getConditionStatusConcept() {
        return conditionStatusConcept;
    }

    public void setConditionStatusConcept(Concept conditionStatusConcept) {
        this.conditionStatusConcept = conditionStatusConcept;
    }

    public String getStopReason() {
        return stopReason;
    }

    public void setStopReason(String stopReason) {
        this.stopReason = stopReason;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public VisitOccurrence getVisitOccurrence() {
        return visitOccurrence;
    }

    public void setVisitOccurrence(VisitOccurrence visitOccurrence) {
        this.visitOccurrence = visitOccurrence;
    }

    public VisitDetail getVisitDetail() {
        return visitDetail;
    }

    public void setVisitDetail(VisitDetail visitDetail) {
        this.visitDetail = visitDetail;
    }

    public String getConditionSourceValue() {
        return conditionSourceValue;
    }

    public void setConditionSourceValue(String conditionSourceValue) {
        this.conditionSourceValue = conditionSourceValue;
    }

    public Concept getConditionSourceConcept() {
        return conditionSourceConcept;
    }

    public void setConditionSourceConcept(Concept conditionSourceConcept) {
        this.conditionSourceConcept = conditionSourceConcept;
    }

    public String getConditionStatusSourceValue() {
        return conditionStatusSourceValue;
    }

    public void setConditionStatusSourceValue(String conditionStatusSourceValue) {
        this.conditionStatusSourceValue = conditionStatusSourceValue;
    }

}