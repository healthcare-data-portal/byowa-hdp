package dev.byowa.hdp.model.StandardizedDerivedElements;

import dev.byowa.hdp.model.clinical.Person;
import dev.byowa.hdp.model.vocabulary.Concept;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "episode", schema = "omop_cdm")
public class Episode {
    @Id
    @Column(name = "episode_id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "episode_concept_id", nullable = false)
    private Concept episodeConcept;

    @NotNull
    @Column(name = "episode_start_date", nullable = false)
    private LocalDate episodeStartDate;

    @Column(name = "episode_start_datetime")
    private Instant episodeStartDatetime;

    @Column(name = "episode_end_date")
    private LocalDate episodeEndDate;

    @Column(name = "episode_end_datetime")
    private Instant episodeEndDatetime;

    @Column(name = "episode_parent_id")
    private Long episodeParentId;

    @Column(name = "episode_number")
    private Integer episodeNumber;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "episode_object_concept_id", nullable = false)
    private Concept episodeObjectConcept;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "episode_type_concept_id", nullable = false)
    private Concept episodeTypeConcept;

    @Size(max = 50)
    @Column(name = "episode_source_value", length = 50)
    private String episodeSourceValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_source_concept_id")
    private Concept episodeSourceConcept;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Concept getEpisodeConcept() {
        return episodeConcept;
    }

    public void setEpisodeConcept(Concept episodeConcept) {
        this.episodeConcept = episodeConcept;
    }

    public LocalDate getEpisodeStartDate() {
        return episodeStartDate;
    }

    public void setEpisodeStartDate(LocalDate episodeStartDate) {
        this.episodeStartDate = episodeStartDate;
    }

    public Instant getEpisodeStartDatetime() {
        return episodeStartDatetime;
    }

    public void setEpisodeStartDatetime(Instant episodeStartDatetime) {
        this.episodeStartDatetime = episodeStartDatetime;
    }

    public LocalDate getEpisodeEndDate() {
        return episodeEndDate;
    }

    public void setEpisodeEndDate(LocalDate episodeEndDate) {
        this.episodeEndDate = episodeEndDate;
    }

    public Instant getEpisodeEndDatetime() {
        return episodeEndDatetime;
    }

    public void setEpisodeEndDatetime(Instant episodeEndDatetime) {
        this.episodeEndDatetime = episodeEndDatetime;
    }

    public Long getEpisodeParentId() {
        return episodeParentId;
    }

    public void setEpisodeParentId(Long episodeParentId) {
        this.episodeParentId = episodeParentId;
    }

    public Integer getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(Integer episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    public Concept getEpisodeObjectConcept() {
        return episodeObjectConcept;
    }

    public void setEpisodeObjectConcept(Concept episodeObjectConcept) {
        this.episodeObjectConcept = episodeObjectConcept;
    }

    public Concept getEpisodeTypeConcept() {
        return episodeTypeConcept;
    }

    public void setEpisodeTypeConcept(Concept episodeTypeConcept) {
        this.episodeTypeConcept = episodeTypeConcept;
    }

    public String getEpisodeSourceValue() {
        return episodeSourceValue;
    }

    public void setEpisodeSourceValue(String episodeSourceValue) {
        this.episodeSourceValue = episodeSourceValue;
    }

    public Concept getEpisodeSourceConcept() {
        return episodeSourceConcept;
    }

    public void setEpisodeSourceConcept(Concept episodeSourceConcept) {
        this.episodeSourceConcept = episodeSourceConcept;
    }

}