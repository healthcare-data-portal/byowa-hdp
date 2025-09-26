package dev.byowa.hdp.model.vocabulary;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "vocabulary", schema = "omop_cdm")
public class Vocabulary {
    @Id
    @Size(max = 20)
    @Column(name = "vocabulary_id", nullable = false, length = 20)
    private String vocabularyId;

    @Size(max = 255)
    @NotNull
    @Column(name = "vocabulary_name", nullable = false)
    private String vocabularyName;

    @Size(max = 255)
    @Column(name = "vocabulary_reference")
    private String vocabularyReference;

    @Size(max = 255)
    @Column(name = "vocabulary_version")
    private String vocabularyVersion;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vocabulary_concept_id", nullable = false)
    private Concept vocabularyConcept;

    public String getVocabularyId() {
        return vocabularyId;
    }

    public void setVocabularyId(String vocabularyId) {
        this.vocabularyId = vocabularyId;
    }

    public String getVocabularyName() {
        return vocabularyName;
    }

    public void setVocabularyName(String vocabularyName) {
        this.vocabularyName = vocabularyName;
    }

    public String getVocabularyReference() {
        return vocabularyReference;
    }

    public void setVocabularyReference(String vocabularyReference) {
        this.vocabularyReference = vocabularyReference;
    }

    public String getVocabularyVersion() {
        return vocabularyVersion;
    }

    public void setVocabularyVersion(String vocabularyVersion) {
        this.vocabularyVersion = vocabularyVersion;
    }

    public Concept getVocabularyConcept() {
        return vocabularyConcept;
    }

    public void setVocabularyConcept(Concept vocabularyConcept) {
        this.vocabularyConcept = vocabularyConcept;
    }

}