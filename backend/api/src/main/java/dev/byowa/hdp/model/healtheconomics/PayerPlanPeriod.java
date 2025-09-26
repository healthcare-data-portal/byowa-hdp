package dev.byowa.hdp.model.healtheconomics;

import dev.byowa.hdp.model.clinical.Person;
import dev.byowa.hdp.model.vocabulary.Concept;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Entity
@Table(name = "payer_plan_period", schema = "omop_cdm")
public class PayerPlanPeriod {
    @Id
    @Column(name = "payer_plan_period_id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @NotNull
    @Column(name = "payer_plan_period_start_date", nullable = false)
    private LocalDate payerPlanPeriodStartDate;

    @NotNull
    @Column(name = "payer_plan_period_end_date", nullable = false)
    private LocalDate payerPlanPeriodEndDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payer_concept_id")
    private Concept payerConcept;

    @Size(max = 50)
    @Column(name = "payer_source_value", length = 50)
    private String payerSourceValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payer_source_concept_id")
    private Concept payerSourceConcept;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_concept_id")
    private Concept planConcept;

    @Size(max = 50)
    @Column(name = "plan_source_value", length = 50)
    private String planSourceValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_source_concept_id")
    private Concept planSourceConcept;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sponsor_concept_id")
    private Concept sponsorConcept;

    @Size(max = 50)
    @Column(name = "sponsor_source_value", length = 50)
    private String sponsorSourceValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sponsor_source_concept_id")
    private Concept sponsorSourceConcept;

    @Size(max = 50)
    @Column(name = "family_source_value", length = 50)
    private String familySourceValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stop_reason_concept_id")
    private Concept stopReasonConcept;

    @Size(max = 50)
    @Column(name = "stop_reason_source_value", length = 50)
    private String stopReasonSourceValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stop_reason_source_concept_id")
    private Concept stopReasonSourceConcept;

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

    public LocalDate getPayerPlanPeriodStartDate() {
        return payerPlanPeriodStartDate;
    }

    public void setPayerPlanPeriodStartDate(LocalDate payerPlanPeriodStartDate) {
        this.payerPlanPeriodStartDate = payerPlanPeriodStartDate;
    }

    public LocalDate getPayerPlanPeriodEndDate() {
        return payerPlanPeriodEndDate;
    }

    public void setPayerPlanPeriodEndDate(LocalDate payerPlanPeriodEndDate) {
        this.payerPlanPeriodEndDate = payerPlanPeriodEndDate;
    }

    public Concept getPayerConcept() {
        return payerConcept;
    }

    public void setPayerConcept(Concept payerConcept) {
        this.payerConcept = payerConcept;
    }

    public String getPayerSourceValue() {
        return payerSourceValue;
    }

    public void setPayerSourceValue(String payerSourceValue) {
        this.payerSourceValue = payerSourceValue;
    }

    public Concept getPayerSourceConcept() {
        return payerSourceConcept;
    }

    public void setPayerSourceConcept(Concept payerSourceConcept) {
        this.payerSourceConcept = payerSourceConcept;
    }

    public Concept getPlanConcept() {
        return planConcept;
    }

    public void setPlanConcept(Concept planConcept) {
        this.planConcept = planConcept;
    }

    public String getPlanSourceValue() {
        return planSourceValue;
    }

    public void setPlanSourceValue(String planSourceValue) {
        this.planSourceValue = planSourceValue;
    }

    public Concept getPlanSourceConcept() {
        return planSourceConcept;
    }

    public void setPlanSourceConcept(Concept planSourceConcept) {
        this.planSourceConcept = planSourceConcept;
    }

    public Concept getSponsorConcept() {
        return sponsorConcept;
    }

    public void setSponsorConcept(Concept sponsorConcept) {
        this.sponsorConcept = sponsorConcept;
    }

    public String getSponsorSourceValue() {
        return sponsorSourceValue;
    }

    public void setSponsorSourceValue(String sponsorSourceValue) {
        this.sponsorSourceValue = sponsorSourceValue;
    }

    public Concept getSponsorSourceConcept() {
        return sponsorSourceConcept;
    }

    public void setSponsorSourceConcept(Concept sponsorSourceConcept) {
        this.sponsorSourceConcept = sponsorSourceConcept;
    }

    public String getFamilySourceValue() {
        return familySourceValue;
    }

    public void setFamilySourceValue(String familySourceValue) {
        this.familySourceValue = familySourceValue;
    }

    public Concept getStopReasonConcept() {
        return stopReasonConcept;
    }

    public void setStopReasonConcept(Concept stopReasonConcept) {
        this.stopReasonConcept = stopReasonConcept;
    }

    public String getStopReasonSourceValue() {
        return stopReasonSourceValue;
    }

    public void setStopReasonSourceValue(String stopReasonSourceValue) {
        this.stopReasonSourceValue = stopReasonSourceValue;
    }

    public Concept getStopReasonSourceConcept() {
        return stopReasonSourceConcept;
    }

    public void setStopReasonSourceConcept(Concept stopReasonSourceConcept) {
        this.stopReasonSourceConcept = stopReasonSourceConcept;
    }

}