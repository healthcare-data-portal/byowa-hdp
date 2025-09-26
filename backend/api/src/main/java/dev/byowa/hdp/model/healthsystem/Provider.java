package dev.byowa.hdp.model.healthsystem;

import dev.byowa.hdp.model.vocabulary.Concept;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "provider", schema = "omop_cdm")
public class Provider {
    @Id
    @Column(name = "provider_id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @Column(name = "provider_name")
    private String providerName;

    @Size(max = 20)
    @Column(name = "npi", length = 20)
    private String npi;

    @Size(max = 20)
    @Column(name = "dea", length = 20)
    private String dea;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_concept_id")
    private Concept specialtyConcept;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "care_site_id")
    private CareSite careSite;

    @Column(name = "year_of_birth")
    private Integer yearOfBirth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gender_concept_id")
    private Concept genderConcept;

    @Size(max = 50)
    @Column(name = "provider_source_value", length = 50)
    private String providerSourceValue;

    @Size(max = 50)
    @Column(name = "specialty_source_value", length = 50)
    private String specialtySourceValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_source_concept_id")
    private Concept specialtySourceConcept;

    @Size(max = 50)
    @Column(name = "gender_source_value", length = 50)
    private String genderSourceValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gender_source_concept_id")
    private Concept genderSourceConcept;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getNpi() {
        return npi;
    }

    public void setNpi(String npi) {
        this.npi = npi;
    }

    public String getDea() {
        return dea;
    }

    public void setDea(String dea) {
        this.dea = dea;
    }

    public Concept getSpecialtyConcept() {
        return specialtyConcept;
    }

    public void setSpecialtyConcept(Concept specialtyConcept) {
        this.specialtyConcept = specialtyConcept;
    }

    public CareSite getCareSite() {
        return careSite;
    }

    public void setCareSite(CareSite careSite) {
        this.careSite = careSite;
    }

    public Integer getYearOfBirth() {
        return yearOfBirth;
    }

    public void setYearOfBirth(Integer yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }

    public Concept getGenderConcept() {
        return genderConcept;
    }

    public void setGenderConcept(Concept genderConcept) {
        this.genderConcept = genderConcept;
    }

    public String getProviderSourceValue() {
        return providerSourceValue;
    }

    public void setProviderSourceValue(String providerSourceValue) {
        this.providerSourceValue = providerSourceValue;
    }

    public String getSpecialtySourceValue() {
        return specialtySourceValue;
    }

    public void setSpecialtySourceValue(String specialtySourceValue) {
        this.specialtySourceValue = specialtySourceValue;
    }

    public Concept getSpecialtySourceConcept() {
        return specialtySourceConcept;
    }

    public void setSpecialtySourceConcept(Concept specialtySourceConcept) {
        this.specialtySourceConcept = specialtySourceConcept;
    }

    public String getGenderSourceValue() {
        return genderSourceValue;
    }

    public void setGenderSourceValue(String genderSourceValue) {
        this.genderSourceValue = genderSourceValue;
    }

    public Concept getGenderSourceConcept() {
        return genderSourceConcept;
    }

    public void setGenderSourceConcept(Concept genderSourceConcept) {
        this.genderSourceConcept = genderSourceConcept;
    }

}