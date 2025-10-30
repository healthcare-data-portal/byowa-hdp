package dev.byowa.hdp.repository;

import dev.byowa.hdp.model.clinical.PersonName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonNameRepository extends JpaRepository<PersonName, Long> {
}