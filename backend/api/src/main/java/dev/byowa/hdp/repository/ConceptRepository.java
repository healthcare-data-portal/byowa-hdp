package dev.byowa.hdp.repository;

import dev.byowa.hdp.model.vocabulary.Concept;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConceptRepository extends JpaRepository<Concept, Integer> {
}