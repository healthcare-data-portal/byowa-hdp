package dev.byowa.hdp.model.healthsystem;

import dev.byowa.hdp.model.vocabulary.Concept;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "care_site", schema = "omop_cdm")
public class CareSite {
    @Id
    @Column(name = "care_site_id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @Column(name = "care_site_name")
    private String careSiteName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_of_service_concept_id")
    private Concept placeOfServiceConcept;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @Size(max = 50)
    @Column(name = "care_site_source_value", length = 50)
    private String careSiteSourceValue;

    @Size(max = 50)
    @Column(name = "place_of_service_source_value", length = 50)
    private String placeOfServiceSourceValue;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCareSiteName() {
        return careSiteName;
    }

    public void setCareSiteName(String careSiteName) {
        this.careSiteName = careSiteName;
    }

    public Concept getPlaceOfServiceConcept() {
        return placeOfServiceConcept;
    }

    public void setPlaceOfServiceConcept(Concept placeOfServiceConcept) {
        this.placeOfServiceConcept = placeOfServiceConcept;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getCareSiteSourceValue() {
        return careSiteSourceValue;
    }

    public void setCareSiteSourceValue(String careSiteSourceValue) {
        this.careSiteSourceValue = careSiteSourceValue;
    }

    public String getPlaceOfServiceSourceValue() {
        return placeOfServiceSourceValue;
    }

    public void setPlaceOfServiceSourceValue(String placeOfServiceSourceValue) {
        this.placeOfServiceSourceValue = placeOfServiceSourceValue;
    }

}