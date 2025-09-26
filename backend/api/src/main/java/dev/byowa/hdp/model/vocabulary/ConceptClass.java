package dev.byowa.hdp.model.vocabulary;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "concept_class", schema = "omop_cdm")
public class ConceptClass {
    @Id
    @Size(max = 20)
    @Column(name = "concept_class_id", nullable = false, length = 20)
    private String conceptClassId;

    @Size(max = 255)
    @NotNull
    @Column(name = "concept_class_name", nullable = false)
    private String conceptClassName;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "concept_class_concept_id", nullable = false)
    private Concept conceptClassConcept;

    public String getConceptClassId() {
        return conceptClassId;
    }

    public void setConceptClassId(String conceptClassId) {
        this.conceptClassId = conceptClassId;
    }

    public String getConceptClassName() {
        return conceptClassName;
    }

    public void setConceptClassName(String conceptClassName) {
        this.conceptClassName = conceptClassName;
    }

    public Concept getConceptClassConcept() {
        return conceptClassConcept;
    }

    public void setConceptClassConcept(Concept conceptClassConcept) {
        this.conceptClassConcept = conceptClassConcept;
    }

}