package dev.byowa.hdp.repository;

import dev.byowa.hdp.model.healthsystem.Provider;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Integer> {
    @EntityGraph(attributePaths = {
        "specialtyConcept",
        "careSite",
        "genderConcept",
        "specialtySourceConcept",
        "genderSourceConcept"
    })
    Optional<Provider> findById(Integer id);

    // used by FhirImportService: find provider by provider_source_value
    Optional<Provider> findByProviderSourceValue(String providerSourceValue);

    // used by FhirImportService: get current max provider_id
    @Query("select max(p.id) from Provider p")
    Integer findMaxId();
}
