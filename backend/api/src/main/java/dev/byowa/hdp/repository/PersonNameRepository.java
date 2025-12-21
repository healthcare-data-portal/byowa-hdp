package dev.byowa.hdp.repository;

import dev.byowa.hdp.model.clinical.PersonName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonNameRepository extends JpaRepository<PersonName, Long> {
    Optional<PersonName> findTopByPerson_IdOrderByIdDesc(Integer personId);
}