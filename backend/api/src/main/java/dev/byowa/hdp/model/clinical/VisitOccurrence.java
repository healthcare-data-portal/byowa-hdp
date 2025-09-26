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
@Table(name = "visit_occurrence", schema = "omop_cdm")
public class VisitOccurrence {
    @Id
    @Column(name = "visit_occurrence_id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "visit_concept_id", nullable = false)
    private Concept visitConcept;

    @NotNull
    @Column(name = "visit_start_date", nullable = false)
    private LocalDate visitStartDate;

    @Column(name = "visit_start_datetime")
    private Instant visitStartDatetime;

    @NotNull
    @Column(name = "visit_end_date", nullable = false)
    private LocalDate visitEndDate;

    @Column(name = "visit_end_datetime")
    private Instant visitEndDatetime;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "visit_type_concept_id", nullable = false)
    private Concept visitTypeConcept;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    private Provider provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "care_site_id")
    private CareSite careSite;

    @Size(max = 50)
    @Column(name = "visit_source_value", length = 50)
    private String visitSourceValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_source_concept_id")
    private Concept visitSourceConcept;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admitted_from_concept_id")
    private Concept admittedFromConcept;

    @Size(max = 50)
    @Column(name = "admitted_from_source_value", length = 50)
    private String admittedFromSourceValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discharged_to_concept_id")
    private Concept dischargedToConcept;

    @Size(max = 50)
    @Column(name = "discharged_to_source_value", length = 50)
    private String dischargedToSourceValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preceding_visit_occurrence_id")
    private VisitOccurrence precedingVisitOccurrence;

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

    public Concept getVisitConcept() {
        return visitConcept;
    }

    public void setVisitConcept(Concept visitConcept) {
        this.visitConcept = visitConcept;
    }

    public LocalDate getVisitStartDate() {
        return visitStartDate;
    }

    public void setVisitStartDate(LocalDate visitStartDate) {
        this.visitStartDate = visitStartDate;
    }

    public Instant getVisitStartDatetime() {
        return visitStartDatetime;
    }

    public void setVisitStartDatetime(Instant visitStartDatetime) {
        this.visitStartDatetime = visitStartDatetime;
    }

    public LocalDate getVisitEndDate() {
        return visitEndDate;
    }

    public void setVisitEndDate(LocalDate visitEndDate) {
        this.visitEndDate = visitEndDate;
    }

    public Instant getVisitEndDatetime() {
        return visitEndDatetime;
    }

    public void setVisitEndDatetime(Instant visitEndDatetime) {
        this.visitEndDatetime = visitEndDatetime;
    }

    public Concept getVisitTypeConcept() {
        return visitTypeConcept;
    }

    public void setVisitTypeConcept(Concept visitTypeConcept) {
        this.visitTypeConcept = visitTypeConcept;
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

    public String getVisitSourceValue() {
        return visitSourceValue;
    }

    public void setVisitSourceValue(String visitSourceValue) {
        this.visitSourceValue = visitSourceValue;
    }

    public Concept getVisitSourceConcept() {
        return visitSourceConcept;
    }

    public void setVisitSourceConcept(Concept visitSourceConcept) {
        this.visitSourceConcept = visitSourceConcept;
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

    public Concept getDischargedToConcept() {
        return dischargedToConcept;
    }

    public void setDischargedToConcept(Concept dischargedToConcept) {
        this.dischargedToConcept = dischargedToConcept;
    }

    public String getDischargedToSourceValue() {
        return dischargedToSourceValue;
    }

    public void setDischargedToSourceValue(String dischargedToSourceValue) {
        this.dischargedToSourceValue = dischargedToSourceValue;
    }

    public VisitOccurrence getPrecedingVisitOccurrence() {
        return precedingVisitOccurrence;
    }

    public void setPrecedingVisitOccurrence(VisitOccurrence precedingVisitOccurrence) {
        this.precedingVisitOccurrence = precedingVisitOccurrence;
    }

}