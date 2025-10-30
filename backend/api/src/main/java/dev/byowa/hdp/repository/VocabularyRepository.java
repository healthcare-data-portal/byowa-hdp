package dev.byowa.hdp.repository;

import dev.byowa.hdp.model.vocabulary.Vocabulary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VocabularyRepository extends JpaRepository<Vocabulary, String> {
}