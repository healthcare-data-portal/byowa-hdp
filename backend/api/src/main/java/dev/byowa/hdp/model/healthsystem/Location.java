package dev.byowa.hdp.model.healthsystem;

import dev.byowa.hdp.model.vocabulary.Concept;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Entity
@Table(name = "location", schema = "omop_cdm")
public class Location {
    @Id
    @Column(name = "location_id", nullable = false)
    private Integer id;

    @Size(max = 50)
    @Column(name = "address_1", length = 50)
    private String address1;

    @Size(max = 50)
    @Column(name = "address_2", length = 50)
    private String address2;

    @Size(max = 50)
    @Column(name = "city", length = 50)
    private String city;

    @Size(max = 2)
    @Column(name = "state", length = 2)
    private String state;

    @Size(max = 9)
    @Column(name = "zip", length = 9)
    private String zip;

    @Size(max = 20)
    @Column(name = "county", length = 20)
    private String county;

    @Size(max = 50)
    @Column(name = "location_source_value", length = 50)
    private String locationSourceValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_concept_id")
    private Concept countryConcept;

    @Size(max = 80)
    @Column(name = "country_source_value", length = 80)
    private String countrySourceValue;

    @Column(name = "latitude")
    private BigDecimal latitude;

    @Column(name = "longitude")
    private BigDecimal longitude;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getLocationSourceValue() {
        return locationSourceValue;
    }

    public void setLocationSourceValue(String locationSourceValue) {
        this.locationSourceValue = locationSourceValue;
    }

    public Concept getCountryConcept() {
        return countryConcept;
    }

    public void setCountryConcept(Concept countryConcept) {
        this.countryConcept = countryConcept;
    }

    public String getCountrySourceValue() {
        return countrySourceValue;
    }

    public void setCountrySourceValue(String countrySourceValue) {
        this.countrySourceValue = countrySourceValue;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

}