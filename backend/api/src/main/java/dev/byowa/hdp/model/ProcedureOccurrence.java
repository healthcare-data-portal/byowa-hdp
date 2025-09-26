package dev.byowa.hdp.model;

import dev.byowa.hdp.model.clinical.Person;
import dev.byowa.hdp.model.clinical.VisitDetail;
import dev.byowa.hdp.model.clinical.VisitOccurrence;
import dev.byowa.hdp.model.healthsystem.Provider;
import dev.byowa.hdp.model.vocabulary.Concept;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "procedure_occurrence", schema = "omop_cdm")
public class ProcedureOccurrence {
    @Id
    @Column(name = "procedure_occurrence_id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "procedure_concept_id", nullable = false)
    private Concept procedureConcept;

    @NotNull
    @Column(name = "procedure_date", nullable = false)
    private LocalDate procedureDate;

    @Column(name = "procedure_datetime")
    private Instant procedureDatetime;

    @Column(name = "procedure_end_date")
    private LocalDate procedureEndDate;

    @Column(name = "procedure_end_datetime")
    private Instant procedureEndDatetime;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "procedure_type_concept_id", nullable = false)
    private Concept procedureTypeConcept;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modifier_concept_id")
    private Concept modifierConcept;

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
    @Column(name = "procedure_source_value", length = 50)
    private String procedureSourceValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "procedure_source_concept_id")
    private Concept procedureSourceConcept;

    @Size(max = 50)
    @Column(name = "modifier_source_value", length = 50)
    private String modifierSourceValue;

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

    public Concept getProcedureConcept() {
        return procedureConcept;
    }

    public void setProcedureConcept(Concept procedureConcept) {
        this.procedureConcept = procedureConcept;
    }

    public LocalDate getProcedureDate() {
        return procedureDate;
    }

    public void setProcedureDate(LocalDate procedureDate) {
        this.procedureDate = procedureDate;
    }

    public Instant getProcedureDatetime() {
        return procedureDatetime;
    }

    public void setProcedureDatetime(Instant procedureDatetime) {
        this.procedureDatetime = procedureDatetime;
    }

    public LocalDate getProcedureEndDate() {
        return procedureEndDate;
    }

    public void setProcedureEndDate(LocalDate procedureEndDate) {
        this.procedureEndDate = procedureEndDate;
    }

    public Instant getProcedureEndDatetime() {
        return procedureEndDatetime;
    }

    public void setProcedureEndDatetime(Instant procedureEndDatetime) {
        this.procedureEndDatetime = procedureEndDatetime;
    }

    public Concept getProcedureTypeConcept() {
        return procedureTypeConcept;
    }

    public void setProcedureTypeConcept(Concept procedureTypeConcept) {
        this.procedureTypeConcept = procedureTypeConcept;
    }

    public Concept getModifierConcept() {
        return modifierConcept;
    }

    public void setModifierConcept(Concept modifierConcept) {
        this.modifierConcept = modifierConcept;
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

    public String getProcedureSourceValue() {
        return procedureSourceValue;
    }

    public void setProcedureSourceValue(String procedureSourceValue) {
        this.procedureSourceValue = procedureSourceValue;
    }

    public Concept getProcedureSourceConcept() {
        return procedureSourceConcept;
    }

    public void setProcedureSourceConcept(Concept procedureSourceConcept) {
        this.procedureSourceConcept = procedureSourceConcept;
    }

    public String getModifierSourceValue() {
        return modifierSourceValue;
    }

    public void setModifierSourceValue(String modifierSourceValue) {
        this.modifierSourceValue = modifierSourceValue;
    }

}