package dev.byowa.hdp.model.clinical;

import dev.byowa.hdp.model.healthsystem.Provider;
import dev.byowa.hdp.model.vocabulary.Concept;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "note", schema = "omop_cdm")
public class Note {
    @Id
    @Column(name = "note_id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @NotNull
    @Column(name = "note_date", nullable = false)
    private LocalDate noteDate;

    @Column(name = "note_datetime")
    private Instant noteDatetime;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "note_type_concept_id", nullable = false)
    private Concept noteTypeConcept;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "note_class_concept_id", nullable = false)
    private Concept noteClassConcept;

    @Size(max = 250)
    @Column(name = "note_title", length = 250)
    private String noteTitle;

    @NotNull
    @Column(name = "note_text", nullable = false, length = Integer.MAX_VALUE)
    private String noteText;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "encoding_concept_id", nullable = false)
    private Concept encodingConcept;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "language_concept_id", nullable = false)
    private Concept languageConcept;

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
    @Column(name = "note_source_value", length = 50)
    private String noteSourceValue;

    @Column(name = "note_event_id")
    private Long noteEventId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_event_field_concept_id")
    private Concept noteEventFieldConcept;

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

    public LocalDate getNoteDate() {
        return noteDate;
    }

    public void setNoteDate(LocalDate noteDate) {
        this.noteDate = noteDate;
    }

    public Instant getNoteDatetime() {
        return noteDatetime;
    }

    public void setNoteDatetime(Instant noteDatetime) {
        this.noteDatetime = noteDatetime;
    }

    public Concept getNoteTypeConcept() {
        return noteTypeConcept;
    }

    public void setNoteTypeConcept(Concept noteTypeConcept) {
        this.noteTypeConcept = noteTypeConcept;
    }

    public Concept getNoteClassConcept() {
        return noteClassConcept;
    }

    public void setNoteClassConcept(Concept noteClassConcept) {
        this.noteClassConcept = noteClassConcept;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public Concept getEncodingConcept() {
        return encodingConcept;
    }

    public void setEncodingConcept(Concept encodingConcept) {
        this.encodingConcept = encodingConcept;
    }

    public Concept getLanguageConcept() {
        return languageConcept;
    }

    public void setLanguageConcept(Concept languageConcept) {
        this.languageConcept = languageConcept;
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

    public String getNoteSourceValue() {
        return noteSourceValue;
    }

    public void setNoteSourceValue(String noteSourceValue) {
        this.noteSourceValue = noteSourceValue;
    }

    public Long getNoteEventId() {
        return noteEventId;
    }

    public void setNoteEventId(Long noteEventId) {
        this.noteEventId = noteEventId;
    }

    public Concept getNoteEventFieldConcept() {
        return noteEventFieldConcept;
    }

    public void setNoteEventFieldConcept(Concept noteEventFieldConcept) {
        this.noteEventFieldConcept = noteEventFieldConcept;
    }

}