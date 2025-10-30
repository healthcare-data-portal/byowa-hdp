package dev.byowa.hdp.repository;

import dev.byowa.hdp.model.vocabulary.ConceptClass;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConceptClassRepository extends JpaRepository<ConceptClass, String> {
}