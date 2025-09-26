package dev.byowa.hdp.model.clinical;

import dev.byowa.hdp.model.healthsystem.CareSite;
import dev.byowa.hdp.model.healthsystem.Provider;
import dev.byowa.hdp.model.vocabulary.Concept;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "visit_detail", schema = "omop_cdm")
public class VisitDetail {
    @Id
    @Column(name = "visit_detail_id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "visit_detail_concept_id", nullable = false)
    private Concept visitDetailConcept;

    @NotNull
    @Column(name = "visit_detail_start_date", nullable = false)
    private LocalDate visitDetailStartDate;

    @Column(name = "visit_detail_start_datetime")
    private Instant visitDetailStartDatetime;

    @NotNull
    @Column(name = "visit_detail_end_date", nullable = false)
    private LocalDate visitDetailEndDate;

    @Column(name = "visit_detail_end_datetime")
    private Instant visitDetailEndDatetime;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "visit_detail_type_concept_id", nullable = false)
    private Concept visitDetailTypeConcept;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    private Provider provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "care_site_id")
    private CareSite careSite;

    @Size(max = 50)
    @Column(name = "visit_detail_source_value", length = 50)
    private String visitDetailSourceValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_detail_source_concept_id")
    private Concept visitDetailSourceConcept;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admitted_from_concept_id")
    private Concept admittedFromConcept;

    @Size(max = 50)
    @Column(name = "admitted_from_source_value", length = 50)
    private String admittedFromSourceValue;

    @Size(max = 50)
    @Column(name = "discharged_to_source_value", length = 50)
    private String dischargedToSourceValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discharged_to_concept_id")
    private Concept dischargedToConcept;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preceding_visit_detail_id")
    private VisitDetail precedingVisitDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_visit_detail_id")
    private VisitDetail parentVisitDetail;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "visit_occurrence_id", nullable = false)
    private VisitOccurrence visitOccurrence;

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

    public Concept getVisitDetailConcept() {
        return visitDetailConcept;
    }

    public void setVisitDetailConcept(Concept visitDetailConcept) {
        this.visitDetailConcept = visitDetailConcept;
    }

    public LocalDate getVisitDetailStartDate() {
        return visitDetailStartDate;
    }

    public void setVisitDetailStartDate(LocalDate visitDetailStartDate) {
        this.visitDetailStartDate = visitDetailStartDate;
    }

    public Instant getVisitDetailStartDatetime() {
        return visitDetailStartDatetime;
    }

    public void setVisitDetailStartDatetime(Instant visitDetailStartDatetime) {
        this.visitDetailStartDatetime = visitDetailStartDatetime;
    }

    public LocalDate getVisitDetailEndDate() {
        return visitDetailEndDate;
    }

    public void setVisitDetailEndDate(LocalDate visitDetailEndDate) {
        this.visitDetailEndDate = visitDetailEndDate;
    }

    public Instant getVisitDetailEndDatetime() {
        return visitDetailEndDatetime;
    }

    public void setVisitDetailEndDatetime(Instant visitDetailEndDatetime) {
        this.visitDetailEndDatetime = visitDetailEndDatetime;
    }

    public Concept getVisitDetailTypeConcept() {
        return visitDetailTypeConcept;
    }

    public void setVisitDetailTypeConcept(Concept visitDetailTypeConcept) {
        this.visitDetailTypeConcept = visitDetailTypeConcept;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public CareSite getCareSite() {
        return careSite;
    }

    public void setCareSite(CareSite careSite) {
        this.careSite = careSite;
    }

    public String getVisitDetailSourceValue() {
        return visitDetailSourceValue;
    }

    public void setVisitDetailSourceValue(String visitDetailSourceValue) {
        this.visitDetailSourceValue = visitDetailSourceValue;
    }

    public Concept getVisitDetailSourceConcept() {
        return visitDetailSourceConcept;
    }

    public void setVisitDetailSourceConcept(Concept visitDetailSourceConcept) {
        this.visitDetailSourceConcept = visitDetailSourceConcept;
    }

    public Concept getAdmittedFromConcept() {
        return admittedFromConcept;
    }

    public void setAdmittedFromConcept(Concept admittedFromConcept) {
        this.admittedFromConcept = admittedFromConcept;
    }

    public String getAdmittedFromSourceValue() {
        return admittedFromSourceValue;
    }

    public void setAdmittedFromSourceValue(String admittedFromSourceValue) {
        this.admittedFromSourceValue = admittedFromSourceValue;
    }

    public String getDischargedToSourceValue() {
        return dischargedToSourceValue;
    }

    public void setDischargedToSourceValue(String dischargedToSourceValue) {
        this.dischargedToSourceValue = dischargedToSourceValue;
    }

    public Concept getDischargedToConcept() {
        return dischargedToConcept;
    }

    public void setDischargedToConcept(Concept dischargedToConcept) {
        this.dischargedToConcept = dischargedToConcept;
    }

    public VisitDetail getPrecedingVisitDetail() {
        return precedingVisitDetail;
    }

    public void setPrecedingVisitDetail(VisitDetail precedingVisitDetail) {
        this.precedingVisitDetail = precedingVisitDetail;
    }

    public VisitDetail getParentVisitDetail() {
        return parentVisitDetail;
    }

    public void setParentVisitDetail(VisitDetail parentVisitDetail) {
        this.parentVisitDetail = parentVisitDetail;
    }

    public VisitOccurrence getVisitOccurrence() {
        return visitOccurrence;
    }

    public void setVisitOccurrence(VisitOccurrence visitOccurrence) {
        this.visitOccurrence = visitOccurrence;
    }

}