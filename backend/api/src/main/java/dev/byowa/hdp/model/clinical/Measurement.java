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
@Table(name = "measurement", schema = "omop_cdm")
public class Measurement {
    @Id
    @Column(name = "measurement_id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "measurement_concept_id", nullable = false)
    private Concept measurementConcept;

    @NotNull
    @Column(name = "measurement_date", nullable = false)
    private LocalDate measurementDate;

    @Column(name = "measurement_datetime")
    private Instant measurementDatetime;

    @Size(max = 10)
    @Column(name = "measurement_time", length = 10)
    private String measurementTime;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "measurement_type_concept_id", nullable = false)
    private Concept measurementTypeConcept;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_concept_id")
    private Concept operatorConcept;

    @Column(name = "value_as_number")
    private BigDecimal valueAsNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "value_as_concept_id")
    private Concept valueAsConcept;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_concept_id")
    private Concept unitConcept;

    @Column(name = "range_low")
    private BigDecimal rangeLow;

    @Column(name = "range_high")
    private BigDecimal rangeHigh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    private Provider provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_occurrence_id")
    private VisitOccurrence visitOccurrence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_detail_id")
    private VisitDetail visitDetail;

    @Size(max = 100)
    @Column(name = "measurement_source_value", length = 50)
    private String measurementSourceValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "measurement_source_concept_id")
    private Concept measurementSourceConcept;

    @Size(max = 50)
    @Column(name = "unit_source_value", length = 50)
    private String unitSourceValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_source_concept_id")
    private Concept unitSourceConcept;

    @Size(max = 50)
    @Column(name = "value_source_value", length = 50)
    private String valueSourceValue;

    @Column(name = "measurement_event_id")
    private Long measurementEventId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meas_event_field_concept_id")
    private Concept measEventFieldConcept;

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

    public Concept getMeasurementConcept() {
        return measurementConcept;
    }

    public void setMeasurementConcept(Concept measurementConcept) {
        this.measurementConcept = measurementConcept;
    }

    public LocalDate getMeasurementDate() {
        return measurementDate;
    }

    public void setMeasurementDate(LocalDate measurementDate) {
        this.measurementDate = measurementDate;
    }

    public Instant getMeasurementDatetime() {
        return measurementDatetime;
    }

    public void setMeasurementDatetime(Instant measurementDatetime) {
        this.measurementDatetime = measurementDatetime;
    }

    public String getMeasurementTime() {
        return measurementTime;
    }

    public void setMeasurementTime(String measurementTime) {
        this.measurementTime = measurementTime;
    }

    public Concept getMeasurementTypeConcept() {
        return measurementTypeConcept;
    }

    public void setMeasurementTypeConcept(Concept measurementTypeConcept) {
        this.measurementTypeConcept = measurementTypeConcept;
    }

    public Concept getOperatorConcept() {
        return operatorConcept;
    }

    public void setOperatorConcept(Concept operatorConcept) {
        this.operatorConcept = operatorConcept;
    }

    public BigDecimal getValueAsNumber() {
        return valueAsNumber;
    }

    public void setValueAsNumber(BigDecimal valueAsNumber) {
        this.valueAsNumber = valueAsNumber;
    }

    public Concept getValueAsConcept() {
        return valueAsConcept;
    }

    public void setValueAsConcept(Concept valueAsConcept) {
        this.valueAsConcept = valueAsConcept;
    }

    public Concept getUnitConcept() {
        return unitConcept;
    }

    public void setUnitConcept(Concept unitConcept) {
        this.unitConcept = unitConcept;
    }

    public BigDecimal getRangeLow() {
        return rangeLow;
    }

    public void setRangeLow(BigDecimal rangeLow) {
        this.rangeLow = rangeLow;
    }

    public BigDecimal getRangeHigh() {
        return rangeHigh;
    }

    public void setRangeHigh(BigDecimal rangeHigh) {
        this.rangeHigh = rangeHigh;
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

    public String getMeasurementSourceValue() {
        return measurementSourceValue;
    }

    public void setMeasurementSourceValue(String measurementSourceValue) {
        this.measurementSourceValue = measurementSourceValue;
    }

    public Concept getMeasurementSourceConcept() {
        return measurementSourceConcept;
    }

    public void setMeasurementSourceConcept(Concept measurementSourceConcept) {
        this.measurementSourceConcept = measurementSourceConcept;
    }

    public String getUnitSourceValue() {
        return unitSourceValue;
    }

    public void setUnitSourceValue(String unitSourceValue) {
        this.unitSourceValue = unitSourceValue;
    }

    public Concept getUnitSourceConcept() {
        return unitSourceConcept;
    }

    public void setUnitSourceConcept(Concept unitSourceConcept) {
        this.unitSourceConcept = unitSourceConcept;
    }

    public String getValueSourceValue() {
        return valueSourceValue;
    }

    public void setValueSourceValue(String valueSourceValue) {
        this.valueSourceValue = valueSourceValue;
    }

    public Long getMeasurementEventId() {
        return measurementEventId;
    }

    public void setMeasurementEventId(Long measurementEventId) {
        this.measurementEventId = measurementEventId;
    }

    public Concept getMeasEventFieldConcept() {
        return measEventFieldConcept;
    }

    public void setMeasEventFieldConcept(Concept measEventFieldConcept) {
        this.measEventFieldConcept = measEventFieldConcept;
    }

}