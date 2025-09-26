package dev.byowa.hdp.model.vocabulary;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "domain", schema = "omop_cdm")
public class Domain {
    @Id
    @Size(max = 20)
    @Column(name = "domain_id", nullable = false, length = 20)
    private String domainId;

    @Size(max = 255)
    @NotNull
    @Column(name = "domain_name", nullable = false)
    private String domainName;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "domain_concept_id", nullable = false)
    private Concept domainConcept;

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public Concept getDomainConcept() {
        return domainConcept;
    }

    public void setDomainConcept(Concept domainConcept) {
        this.domainConcept = domainConcept;
    }

}