package dev.byowa.hdp.model;

import dev.byowa.hdp.model.clinical.Person;
import dev.byowa.hdp.model.clinical.VisitDetail;
import dev.byowa.hdp.model.clinical.VisitOccurrence;
import dev.byowa.hdp.model.healthsystem.Provider;
import dev.byowa.hdp.model.vocabulary.Concept;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "drug_exposure", schema = "omop_cdm")
public class DrugExposure {
    @Id
    @Column(name = "drug_exposure_id", nullable = false)
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
    @Column(name = "drug_exposure_start_date", nullable = false)
    private LocalDate drugExposureStartDate;

    @Column(name = "drug_exposure_start_datetime")
    private Instant drugExposureStartDatetime;

    @NotNull
    @Column(name = "drug_exposure_end_date", nullable = false)
    private LocalDate drugExposureEndDate;

    @Column(name = "drug_exposure_end_datetime")
    private Instant drugExposureEndDatetime;

    @Column(name = "verbatim_end_date")
    private LocalDate verbatimEndDate;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "drug_type_concept_id", nullable = false)
    private Concept drugTypeConcept;

    @Size(max = 20)
    @Column(name = "stop_reason", length = 20)
    private String stopReason;

    @Column(name = "refills")
    private Integer refills;

    @Column(name = "quantity")
    private BigDecimal quantity;

    @Column(name = "days_supply")
    private Integer daysSupply;

    @Column(name = "sig", length = Integer.MAX_VALUE)
    private String sig;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_concept_id")
    private Concept routeConcept;

    @Size(max = 50)
    @Column(name = "lot_number", length = 50)
    private String lotNumber;

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
    @Column(name = "drug_source_value", length = 50)
    private String drugSourceValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drug_source_concept_id")
    private Concept drugSourceConcept;

    @Size(max = 50)
    @Column(name = "route_source_value", length = 50)
    private String routeSourceValue;

    @Size(max = 50)
    @Column(name = "dose_unit_source_value", length = 50)
    private String doseUnitSourceValue;

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

    public LocalDate getDrugExposureStartDate() {
        return drugExposureStartDate;
    }

    public void setDrugExposureStartDate(LocalDate drugExposureStartDate) {
        this.drugExposureStartDate = drugExposureStartDate;
    }

    public Instant getDrugExposureStartDatetime() {
        return drugExposureStartDatetime;
    }

    public void setDrugExposureStartDatetime(Instant drugExposureStartDatetime) {
        this.drugExposureStartDatetime = drugExposureStartDatetime;
    }

    public LocalDate getDrugExposureEndDate() {
        return drugExposureEndDate;
    }

    public void setDrugExposureEndDate(LocalDate drugExposureEndDate) {
        this.drugExposureEndDate = drugExposureEndDate;
    }

    public Instant getDrugExposureEndDatetime() {
        return drugExposureEndDatetime;
    }

    public void setDrugExposureEndDatetime(Instant drugExposureEndDatetime) {
        this.drugExposureEndDatetime = drugExposureEndDatetime;
    }

    public LocalDate getVerbatimEndDate() {
        return verbatimEndDate;
    }

    public void setVerbatimEndDate(LocalDate verbatimEndDate) {
        this.verbatimEndDate = verbatimEndDate;
    }

    public Concept getDrugTypeConcept() {
        return drugTypeConcept;
    }

    public void setDrugTypeConcept(Concept drugTypeConcept) {
        this.drugTypeConcept = drugTypeConcept;
    }

    public String getStopReason() {
        return stopReason;
    }

    public void setStopReason(String stopReason) {
        this.stopReason = stopReason;
    }

    public Integer getRefills() {
        return refills;
    }

    public void setRefills(Integer refills) {
        this.refills = refills;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public Integer getDaysSupply() {
        return daysSupply;
    }

    public void setDaysSupply(Integer daysSupply) {
        this.daysSupply = daysSupply;
    }

    public String getSig() {
        return sig;
    }

    public void setSig(String sig) {
        this.sig = sig;
    }

    public Concept getRouteConcept() {
        return routeConcept;
    }

    public void setRouteConcept(Concept routeConcept) {
        this.routeConcept = routeConcept;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
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

    public String getDrugSourceValue() {
        return drugSourceValue;
    }

    public void setDrugSourceValue(String drugSourceValue) {
        this.drugSourceValue = drugSourceValue;
    }

    public Concept getDrugSourceConcept() {
        return drugSourceConcept;
    }

    public void setDrugSourceConcept(Concept drugSourceConcept) {
        this.drugSourceConcept = drugSourceConcept;
    }

    public String getRouteSourceValue() {
        return routeSourceValue;
    }

    public void setRouteSourceValue(String routeSourceValue) {
        this.routeSourceValue = routeSourceValue;
    }

    public String getDoseUnitSourceValue() {
        return doseUnitSourceValue;
    }

    public void setDoseUnitSourceValue(String doseUnitSourceValue) {
        this.doseUnitSourceValue = doseUnitSourceValue;
    }

}