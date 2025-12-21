package dev.byowa.hdp.repository;

import dev.byowa.hdp.model.clinical.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Integer> {
    @Query("SELECT MAX(p.id) FROM Person p")
    Integer findMaxId();

    boolean existsByPersonSourceValue(String personSourceValue);
    Optional<Person> findByPersonSourceValue(String personSourceValue);

}