package dev.byowa.hdp.repository;

import dev.byowa.hdp.model.healthsystem.Provider;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // convenience used elsewhere
    Optional<Provider> findByProviderSourceValue(String providerSourceValue);

    @Query("select max(p.id) from Provider p")
    Integer findMaxId();

    // count patients assigned to this provider (Person.provider_id)
    @Query(value = "select coalesce(count(*),0) from omop_cdm.person p where p.provider_id = :id", nativeQuery = true)
    Integer countPatientsAssigned(@Param("id") Integer id);

    // total records created by this provider across common clinical tables where provider_id exists
    @Query(value =
        "select ( " +
            " (select coalesce(count(*),0) from omop_cdm.person where provider_id = :id) + " +
            " (select coalesce(count(*),0) from omop_cdm.visit_occurrence where provider_id = :id) + " +
            " (select coalesce(count(*),0) from omop_cdm.condition_occurrence where provider_id = :id) + " +
            " (select coalesce(count(*),0) from omop_cdm.measurement where provider_id = :id) + " +
            " (select coalesce(count(*),0) from omop_cdm.observation where provider_id = :id) + " +
            " (select coalesce(count(*),0) from omop_cdm.procedure_occurrence where provider_id = :id) + " +
            " (select coalesce(count(*),0) from omop_cdm.drug_exposure where provider_id = :id) + " +
            " (select coalesce(count(*),0) from omop_cdm.device_exposure where provider_id = :id) + " +
            " (select coalesce(count(*),0) from omop_cdm.note where provider_id = :id) " +
        ") as total", nativeQuery = true)
    Integer countRecordsCreated(@Param("id") Integer id);
}
