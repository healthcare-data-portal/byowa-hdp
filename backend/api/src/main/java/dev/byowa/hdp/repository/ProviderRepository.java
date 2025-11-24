package dev.byowa.hdp.repository;

import dev.byowa.hdp.model.healthsystem.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Integer> {

    Optional<Provider> findByProviderSourceValue(String providerSourceValue);

    @Query("select max(p.id) from Provider p")
    Integer findMaxId();
}
