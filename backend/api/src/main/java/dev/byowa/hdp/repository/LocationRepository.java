package dev.byowa.hdp.repository;

import dev.byowa.hdp.model.healthsystem.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
}
