package dev.byowa.hdp.model.clinical;

import dev.byowa.hdp.model.healthsystem.CareSite;
import dev.byowa.hdp.model.healthsystem.Location;
import dev.byowa.hdp.model.healthsystem.Provider;
import dev.byowa.hdp.model.vocabulary.Concept;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "person", schema = "omop_cdm")
public class Person {
    @Id
    @Column(name = "person_id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "gender_concept_id", nullable = false)
    private Concept genderConcept;

    @NotNull
    @Column(name = "year_of_birth", nullable = false)
    private Integer yearOfBirth;

    @Column(name = "month_of_birth")
    private Integer monthOfBirth;

    @Column(name = "day_of_birth")
    private Integer dayOfBirth;

    @Column(name = "birth_datetime")
    private Instant birthDatetime;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "race_concept_id", nullable = false)
    private Concept raceConcept;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ethnicity_concept_id", nullable = false)
    private Concept ethnicityConcept;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    private Provider provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "care_site_id")
    private CareSite careSite;

    @Size(max = 50)
    @Column(name = "person_source_value", length = 50)
    private String personSourceValue;

    @Size(max = 50)
    @Column(name = "gender_source_value", length = 50)
    private String genderSourceValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gender_source_concept_id")
    private Concept genderSourceConcept;

    @Size(max = 50)
    @Column(name = "race_source_value", length = 50)
    private String raceSourceValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "race_source_concept_id")
    private Concept raceSourceConcept;

    @Size(max = 50)
    @Column(name = "ethnicity_source_value", length = 50)
    private String ethnicitySourceValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ethnicity_source_concept_id")
    private Concept ethnicitySourceConcept;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PersonName> personNames = new ArrayList<>();

    public List<PersonName> getPersonNames() {
        return personNames;
    }

    public void setPersonNames(List<PersonName> personNames) {
        this.personNames = personNames;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Concept getGenderConcept() {
        return genderConcept;
    }

    public void setGenderConcept(Concept genderConcept) {
        this.genderConcept = genderConcept;
    }

    public Integer getYearOfBirth() {
        return yearOfBirth;
    }

    public void setYearOfBirth(Integer yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }

    public Integer getMonthOfBirth() {
        return monthOfBirth;
    }

    public void setMonthOfBirth(Integer monthOfBirth) {
        this.monthOfBirth = monthOfBirth;
    }

    public Integer getDayOfBirth() {
        return dayOfBirth;
    }

    public void setDayOfBirth(Integer dayOfBirth) {
        this.dayOfBirth = dayOfBirth;
    }

    public Instant getBirthDatetime() {
        return birthDatetime;
    }

    public void setBirthDatetime(Instant birthDatetime) {
        this.birthDatetime = birthDatetime;
    }

    public Concept getRaceConcept() {
        return raceConcept;
    }

    public void setRaceConcept(Concept raceConcept) {
        this.raceConcept = raceConcept;
    }

    public Concept getEthnicityConcept() {
        return ethnicityConcept;
    }

    public void setEthnicityConcept(Concept ethnicityConcept) {
        this.ethnicityConcept = ethnicityConcept;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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

    public String getPersonSourceValue() {
        return personSourceValue;
    }

    public void setPersonSourceValue(String personSourceValue) {
        this.personSourceValue = personSourceValue;
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

    public String getRaceSourceValue() {
        return raceSourceValue;
    }

    public void setRaceSourceValue(String raceSourceValue) {
        this.raceSourceValue = raceSourceValue;
    }

    public Concept getRaceSourceConcept() {
        return raceSourceConcept;
    }

    public void setRaceSourceConcept(Concept raceSourceConcept) {
        this.raceSourceConcept = raceSourceConcept;
    }

    public String getEthnicitySourceValue() {
        return ethnicitySourceValue;
    }

    public void setEthnicitySourceValue(String ethnicitySourceValue) {
        this.ethnicitySourceValue = ethnicitySourceValue;
    }

    public Concept getEthnicitySourceConcept() {
        return ethnicitySourceConcept;
    }

    public void setEthnicitySourceConcept(Concept ethnicitySourceConcept) {
        this.ethnicitySourceConcept = ethnicitySourceConcept;
    }

}