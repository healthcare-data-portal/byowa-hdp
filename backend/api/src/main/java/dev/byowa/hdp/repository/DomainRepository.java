package dev.byowa.hdp.repository;

import dev.byowa.hdp.model.vocabulary.Domain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DomainRepository extends JpaRepository<Domain, String> {
}