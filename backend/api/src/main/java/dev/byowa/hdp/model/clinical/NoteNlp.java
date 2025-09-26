package dev.byowa.hdp.model.clinical;

import dev.byowa.hdp.model.vocabulary.Concept;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "note_nlp", schema = "omop_cdm")
public class NoteNlp {
    @Id
    @Column(name = "note_nlp_id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "note_id", nullable = false)
    private Integer noteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_concept_id")
    private Concept sectionConcept;

    @Size(max = 250)
    @Column(name = "snippet", length = 250)
    private String snippet;

    @Size(max = 50)
    @Column(name = "\"offset\"", length = 50)
    private String offset;

    @Size(max = 250)
    @NotNull
    @Column(name = "lexical_variant", nullable = false, length = 250)
    private String lexicalVariant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_nlp_concept_id")
    private Concept noteNlpConcept;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_nlp_source_concept_id")
    private Concept noteNlpSourceConcept;

    @Size(max = 250)
    @Column(name = "nlp_system", length = 250)
    private String nlpSystem;

    @NotNull
    @Column(name = "nlp_date", nullable = false)
    private LocalDate nlpDate;

    @Column(name = "nlp_datetime")
    private Instant nlpDatetime;

    @Size(max = 1)
    @Column(name = "term_exists", length = 1)
    private String termExists;

    @Size(max = 50)
    @Column(name = "term_temporal", length = 50)
    private String termTemporal;

    @Size(max = 2000)
    @Column(name = "term_modifiers", length = 2000)
    private String termModifiers;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNoteId() {
        return noteId;
    }

    public void setNoteId(Integer noteId) {
        this.noteId = noteId;
    }

    public Concept getSectionConcept() {
        return sectionConcept;
    }

    public void setSectionConcept(Concept sectionConcept) {
        this.sectionConcept = sectionConcept;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public String getLexicalVariant() {
        return lexicalVariant;
    }

    public void setLexicalVariant(String lexicalVariant) {
        this.lexicalVariant = lexicalVariant;
    }

    public Concept getNoteNlpConcept() {
        return noteNlpConcept;
    }

    public void setNoteNlpConcept(Concept noteNlpConcept) {
        this.noteNlpConcept = noteNlpConcept;
    }

    public Concept getNoteNlpSourceConcept() {
        return noteNlpSourceConcept;
    }

    public void setNoteNlpSourceConcept(Concept noteNlpSourceConcept) {
        this.noteNlpSourceConcept = noteNlpSourceConcept;
    }

    public String getNlpSystem() {
        return nlpSystem;
    }

    public void setNlpSystem(String nlpSystem) {
        this.nlpSystem = nlpSystem;
    }

    public LocalDate getNlpDate() {
        return nlpDate;
    }

    public void setNlpDate(LocalDate nlpDate) {
        this.nlpDate = nlpDate;
    }

    public Instant getNlpDatetime() {
        return nlpDatetime;
    }

    public void setNlpDatetime(Instant nlpDatetime) {
        this.nlpDatetime = nlpDatetime;
    }

    public String getTermExists() {
        return termExists;
    }

    public void setTermExists(String termExists) {
        this.termExists = termExists;
    }

    public String getTermTemporal() {
        return termTemporal;
    }

    public void setTermTemporal(String termTemporal) {
        this.termTemporal = termTemporal;
    }

    public String getTermModifiers() {
        return termModifiers;
    }

    public void setTermModifiers(String termModifiers) {
        this.termModifiers = termModifiers;
    }

}