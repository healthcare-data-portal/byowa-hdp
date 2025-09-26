package dev.byowa.hdp.model.clinical;

import dev.byowa.hdp.model.healthsystem.Provider;
import dev.byowa.hdp.model.vocabulary.Concept;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "device_exposure", schema = "omop_cdm")
public class DeviceExposure {
    @Id
    @Column(name = "device_exposure_id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "device_concept_id", nullable = false)
    private Concept deviceConcept;

    @NotNull
    @Column(name = "device_exposure_start_date", nullable = false)
    private LocalDate deviceExposureStartDate;

    @Column(name = "device_exposure_start_datetime")
    private Instant deviceExposureStartDatetime;

    @Column(name = "device_exposure_end_date")
    private LocalDate deviceExposureEndDate;

    @Column(name = "device_exposure_end_datetime")
    private Instant deviceExposureEndDatetime;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "device_type_concept_id", nullable = false)
    private Concept deviceTypeConcept;

    @Size(max = 255)
    @Column(name = "unique_device_id")
    private String uniqueDeviceId;

    @Size(max = 255)
    @Column(name = "production_id")
    private String productionId;

    @Column(name = "quantity")
    private Integer quantity;

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
    @Column(name = "device_source_value", length = 50)
    private String deviceSourceValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_source_concept_id")
    private Concept deviceSourceConcept;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_concept_id")
    private Concept unitConcept;

    @Size(max = 50)
    @Column(name = "unit_source_value", length = 50)
    private String unitSourceValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_source_concept_id")
    private Concept unitSourceConcept;

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

    public Concept getDeviceConcept() {
        return deviceConcept;
    }

    public void setDeviceConcept(Concept deviceConcept) {
        this.deviceConcept = deviceConcept;
    }

    public LocalDate getDeviceExposureStartDate() {
        return deviceExposureStartDate;
    }

    public void setDeviceExposureStartDate(LocalDate deviceExposureStartDate) {
        this.deviceExposureStartDate = deviceExposureStartDate;
    }

    public Instant getDeviceExposureStartDatetime() {
        return deviceExposureStartDatetime;
    }

    public void setDeviceExposureStartDatetime(Instant deviceExposureStartDatetime) {
        this.deviceExposureStartDatetime = deviceExposureStartDatetime;
    }

    public LocalDate getDeviceExposureEndDate() {
        return deviceExposureEndDate;
    }

    public void setDeviceExposureEndDate(LocalDate deviceExposureEndDate) {
        this.deviceExposureEndDate = deviceExposureEndDate;
    }

    public Instant getDeviceExposureEndDatetime() {
        return deviceExposureEndDatetime;
    }

    public void setDeviceExposureEndDatetime(Instant deviceExposureEndDatetime) {
        this.deviceExposureEndDatetime = deviceExposureEndDatetime;
    }

    public Concept getDeviceTypeConcept() {
        return deviceTypeConcept;
    }

    public void setDeviceTypeConcept(Concept deviceTypeConcept) {
        this.deviceTypeConcept = deviceTypeConcept;
    }

    public String getUniqueDeviceId() {
        return uniqueDeviceId;
    }

    public void setUniqueDeviceId(String uniqueDeviceId) {
        this.uniqueDeviceId = uniqueDeviceId;
    }

    public String getProductionId() {
        return productionId;
    }

    public void setProductionId(String productionId) {
        this.productionId = productionId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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

    public String getDeviceSourceValue() {
        return deviceSourceValue;
    }

    public void setDeviceSourceValue(String deviceSourceValue) {
        this.deviceSourceValue = deviceSourceValue;
    }

    public Concept getDeviceSourceConcept() {
        return deviceSourceConcept;
    }

    public void setDeviceSourceConcept(Concept deviceSourceConcept) {
        this.deviceSourceConcept = deviceSourceConcept;
    }

    public Concept getUnitConcept() {
        return unitConcept;
    }

    public void setUnitConcept(Concept unitConcept) {
        this.unitConcept = unitConcept;
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

}