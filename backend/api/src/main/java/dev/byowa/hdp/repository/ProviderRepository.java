package dev.byowa.hdp.repository;

import dev.byowa.hdp.model.healthsystem.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProviderRepository extends JpaRepository<Provider, Integer> {

    Optional<Provider> findByProviderSourceValue(String providerSourceValue);

    @Query("select max(p.id) from Provider p")
    Integer findMaxId();
}
