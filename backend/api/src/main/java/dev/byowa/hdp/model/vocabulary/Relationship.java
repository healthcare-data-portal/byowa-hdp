package dev.byowa.hdp.model.vocabulary;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "relationship", schema = "omop_cdm")
public class Relationship {
    @Id
    @Size(max = 20)
    @Column(name = "relationship_id", nullable = false, length = 20)
    private String relationshipId;

    @Size(max = 255)
    @NotNull
    @Column(name = "relationship_name", nullable = false)
    private String relationshipName;

    @Size(max = 1)
    @NotNull
    @Column(name = "is_hierarchical", nullable = false, length = 1)
    private String isHierarchical;

    @Size(max = 1)
    @NotNull
    @Column(name = "defines_ancestry", nullable = false, length = 1)
    private String definesAncestry;

    @Size(max = 20)
    @NotNull
    @Column(name = "reverse_relationship_id", nullable = false, length = 20)
    private String reverseRelationshipId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "relationship_concept_id", nullable = false)
    private Concept relationshipConcept;

    public String getRelationshipId() {
        return relationshipId;
    }

    public void setRelationshipId(String relationshipId) {
        this.relationshipId = relationshipId;
    }

    public String getRelationshipName() {
        return relationshipName;
    }

    public void setRelationshipName(String relationshipName) {
        this.relationshipName = relationshipName;
    }

    public String getIsHierarchical() {
        return isHierarchical;
    }

    public void setIsHierarchical(String isHierarchical) {
        this.isHierarchical = isHierarchical;
    }

    public String getDefinesAncestry() {
        return definesAncestry;
    }

    public void setDefinesAncestry(String definesAncestry) {
        this.definesAncestry = definesAncestry;
    }

    public String getReverseRelationshipId() {
        return reverseRelationshipId;
    }

    public void setReverseRelationshipId(String reverseRelationshipId) {
        this.reverseRelationshipId = reverseRelationshipId;
    }

    public Concept getRelationshipConcept() {
        return relationshipConcept;
    }

    public void setRelationshipConcept(Concept relationshipConcept) {
        this.relationshipConcept = relationshipConcept;
    }

}