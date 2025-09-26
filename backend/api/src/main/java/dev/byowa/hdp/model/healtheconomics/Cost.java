package dev.byowa.hdp.model.healtheconomics;

import dev.byowa.hdp.model.vocabulary.Concept;
import dev.byowa.hdp.model.vocabulary.Domain;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Entity
@Table(name = "cost", schema = "omop_cdm")
public class Cost {
    @Id
    @Column(name = "cost_id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "cost_event_id", nullable = false)
    private Integer costEventId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cost_domain_id", nullable = false)
    private Domain costDomain;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cost_type_concept_id", nullable = false)
    private Concept costTypeConcept;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_concept_id")
    private Concept currencyConcept;

    @Column(name = "total_charge")
    private BigDecimal totalCharge;

    @Column(name = "total_cost")
    private BigDecimal totalCost;

    @Column(name = "total_paid")
    private BigDecimal totalPaid;

    @Column(name = "paid_by_payer")
    private BigDecimal paidByPayer;

    @Column(name = "paid_by_patient")
    private BigDecimal paidByPatient;

    @Column(name = "paid_patient_copay")
    private BigDecimal paidPatientCopay;

    @Column(name = "paid_patient_coinsurance")
    private BigDecimal paidPatientCoinsurance;

    @Column(name = "paid_patient_deductible")
    private BigDecimal paidPatientDeductible;

    @Column(name = "paid_by_primary")
    private BigDecimal paidByPrimary;

    @Column(name = "paid_ingredient_cost")
    private BigDecimal paidIngredientCost;

    @Column(name = "paid_dispensing_fee")
    private BigDecimal paidDispensingFee;

    @Column(name = "payer_plan_period_id")
    private Integer payerPlanPeriodId;

    @Column(name = "amount_allowed")
    private BigDecimal amountAllowed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "revenue_code_concept_id")
    private Concept revenueCodeConcept;

    @Size(max = 50)
    @Column(name = "revenue_code_source_value", length = 50)
    private String revenueCodeSourceValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drg_concept_id")
    private Concept drgConcept;

    @Size(max = 3)
    @Column(name = "drg_source_value", length = 3)
    private String drgSourceValue;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCostEventId() {
        return costEventId;
    }

    public void setCostEventId(Integer costEventId) {
        this.costEventId = costEventId;
    }

    public Domain getCostDomain() {
        return costDomain;
    }

    public void setCostDomain(Domain costDomain) {
        this.costDomain = costDomain;
    }

    public Concept getCostTypeConcept() {
        return costTypeConcept;
    }

    public void setCostTypeConcept(Concept costTypeConcept) {
        this.costTypeConcept = costTypeConcept;
    }

    public Concept getCurrencyConcept() {
        return currencyConcept;
    }

    public void setCurrencyConcept(Concept currencyConcept) {
        this.currencyConcept = currencyConcept;
    }

    public BigDecimal getTotalCharge() {
        return totalCharge;
    }

    public void setTotalCharge(BigDecimal totalCharge) {
        this.totalCharge = totalCharge;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public BigDecimal getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(BigDecimal totalPaid) {
        this.totalPaid = totalPaid;
    }

    public BigDecimal getPaidByPayer() {
        return paidByPayer;
    }

    public void setPaidByPayer(BigDecimal paidByPayer) {
        this.paidByPayer = paidByPayer;
    }

    public BigDecimal getPaidByPatient() {
        return paidByPatient;
    }

    public void setPaidByPatient(BigDecimal paidByPatient) {
        this.paidByPatient = paidByPatient;
    }

    public BigDecimal getPaidPatientCopay() {
        return paidPatientCopay;
    }

    public void setPaidPatientCopay(BigDecimal paidPatientCopay) {
        this.paidPatientCopay = paidPatientCopay;
    }

    public BigDecimal getPaidPatientCoinsurance() {
        return paidPatientCoinsurance;
    }

    public void setPaidPatientCoinsurance(BigDecimal paidPatientCoinsurance) {
        this.paidPatientCoinsurance = paidPatientCoinsurance;
    }

    public BigDecimal getPaidPatientDeductible() {
        return paidPatientDeductible;
    }

    public void setPaidPatientDeductible(BigDecimal paidPatientDeductible) {
        this.paidPatientDeductible = paidPatientDeductible;
    }

    public BigDecimal getPaidByPrimary() {
        return paidByPrimary;
    }

    public void setPaidByPrimary(BigDecimal paidByPrimary) {
        this.paidByPrimary = paidByPrimary;
    }

    public BigDecimal getPaidIngredientCost() {
        return paidIngredientCost;
    }

    public void setPaidIngredientCost(BigDecimal paidIngredientCost) {
        this.paidIngredientCost = paidIngredientCost;
    }

    public BigDecimal getPaidDispensingFee() {
        return paidDispensingFee;
    }

    public void setPaidDispensingFee(BigDecimal paidDispensingFee) {
        this.paidDispensingFee = paidDispensingFee;
    }

    public Integer getPayerPlanPeriodId() {
        return payerPlanPeriodId;
    }

    public void setPayerPlanPeriodId(Integer payerPlanPeriodId) {
        this.payerPlanPeriodId = payerPlanPeriodId;
    }

    public BigDecimal getAmountAllowed() {
        return amountAllowed;
    }

    public void setAmountAllowed(BigDecimal amountAllowed) {
        this.amountAllowed = amountAllowed;
    }

    public Concept getRevenueCodeConcept() {
        return revenueCodeConcept;
    }

    public void setRevenueCodeConcept(Concept revenueCodeConcept) {
        this.revenueCodeConcept = revenueCodeConcept;
    }

    public String getRevenueCodeSourceValue() {
        return revenueCodeSourceValue;
    }

    public void setRevenueCodeSourceValue(String revenueCodeSourceValue) {
        this.revenueCodeSourceValue = revenueCodeSourceValue;
    }

    public Concept getDrgConcept() {
        return drgConcept;
    }

    public void setDrgConcept(Concept drgConcept) {
        this.drgConcept = drgConcept;
    }

    public String getDrgSourceValue() {
        return drgSourceValue;
    }

    public void setDrgSourceValue(String drgSourceValue) {
        this.drgSourceValue = drgSourceValue;
    }

}