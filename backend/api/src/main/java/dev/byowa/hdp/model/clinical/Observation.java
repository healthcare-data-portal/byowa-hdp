package dev.byowa.hdp.model.clinical;

import dev.byowa.hdp.model.healthsystem.Provider;
import dev.byowa.hdp.model.vocabulary.Concept;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "observation", schema = "omop_cdm")
public class Observation {
    @Id
    @Column(name = "observation_id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "observation_concept_id", nullable = false)
    private Concept observationConcept;

    @NotNull
    @Column(name = "observation_date", nullable = false)
    private LocalDate observationDate;

    @Column(name = "observation_datetime")
    private Instant observationDatetime;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "observation_type_concept_id", nullable = false)
    private Concept observationTypeConcept;

    @Column(name = "value_as_number")
    private BigDecimal valueAsNumber;

    @Size(max = 60)
    @Column(name = "value_as_string", length = 60)
    private String valueAsString;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "value_as_concept_id")
    private Concept valueAsConcept;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qualifier_concept_id")
    private Concept qualifierConcept;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_concept_id")
    private Concept unitConcept;

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
    @Column(name = "observation_source_value", length = 50)
    private String observationSourceValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "observation_source_concept_id")
    private Concept observationSourceConcept;

    @Size(max = 50)
    @Column(name = "unit_source_value", length = 50)
    private String unitSourceValue;

    @Size(max = 50)
    @Column(name = "qualifier_source_value", length = 50)
    private String qualifierSourceValue;

    @Size(max = 50)
    @Column(name = "value_source_value", length = 50)
    private String valueSourceValue;

    @Column(name = "observation_event_id")
    private Long observationEventId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "obs_event_field_concept_id")
    private Concept obsEventFieldConcept;

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

    public Concept getObservationConcept() {
        return observationConcept;
    }

    public void setObservationConcept(Concept observationConcept) {
        this.observationConcept = observationConcept;
    }

    public LocalDate getObservationDate() {
        return observationDate;
    }

    public void setObservationDate(LocalDate observationDate) {
        this.observationDate = observationDate;
    }

    public Instant getObservationDatetime() {
        return observationDatetime;
    }

    public void setObservationDatetime(Instant observationDatetime) {
        this.observationDatetime = observationDatetime;
    }

    public Concept getObservationTypeConcept() {
        return observationTypeConcept;
    }

    public void setObservationTypeConcept(Concept observationTypeConcept) {
        this.observationTypeConcept = observationTypeConcept;
    }

    public BigDecimal getValueAsNumber() {
        return valueAsNumber;
    }

    public void setValueAsNumber(BigDecimal valueAsNumber) {
        this.valueAsNumber = valueAsNumber;
    }

    public String getValueAsString() {
        return valueAsString;
    }

    public void setValueAsString(String valueAsString) {
        this.valueAsString = valueAsString;
    }

    public Concept getValueAsConcept() {
        return valueAsConcept;
    }

    public void setValueAsConcept(Concept valueAsConcept) {
        this.valueAsConcept = valueAsConcept;
    }

    public Concept getQualifierConcept() {
        return qualifierConcept;
    }

    public void setQualifierConcept(Concept qualifierConcept) {
        this.qualifierConcept = qualifierConcept;
    }

    public Concept getUnitConcept() {
        return unitConcept;
    }

    public void setUnitConcept(Concept unitConcept) {
        this.unitConcept = unitConcept;
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

    public String getObservationSourceValue() {
        return observationSourceValue;
    }

    public void setObservationSourceValue(String observationSourceValue) {
        this.observationSourceValue = observationSourceValue;
    }

    public Concept getObservationSourceConcept() {
        return observationSourceConcept;
    }

    public void setObservationSourceConcept(Concept observationSourceConcept) {
        this.observationSourceConcept = observationSourceConcept;
    }

    public String getUnitSourceValue() {
        return unitSourceValue;
    }

    public void setUnitSourceValue(String unitSourceValue) {
        this.unitSourceValue = unitSourceValue;
    }

    public String getQualifierSourceValue() {
        return qualifierSourceValue;
    }

    public void setQualifierSourceValue(String qualifierSourceValue) {
        this.qualifierSourceValue = qualifierSourceValue;
    }

    public String getValueSourceValue() {
        return valueSourceValue;
    }

    public void setValueSourceValue(String valueSourceValue) {
        this.valueSourceValue = valueSourceValue;
    }

    public Long getObservationEventId() {
        return observationEventId;
    }

    public void setObservationEventId(Long observationEventId) {
        this.observationEventId = observationEventId;
    }

    public Concept getObsEventFieldConcept() {
        return obsEventFieldConcept;
    }

    public void setObsEventFieldConcept(Concept obsEventFieldConcept) {
        this.obsEventFieldConcept = obsEventFieldConcept;
    }

}