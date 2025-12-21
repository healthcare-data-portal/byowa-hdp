package dev.byowa.hdp.repository;

import dev.byowa.hdp.model.healthsystem.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Integer> {
    Optional<Location> findByLocationSourceValue(String locationSourceValue);

    @Query("select max(l.id) from Location l")
    Integer findMaxId();


}
