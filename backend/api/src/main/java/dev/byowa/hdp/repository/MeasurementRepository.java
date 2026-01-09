package dev.byowa.hdp.repository;

import dev.byowa.hdp.model.clinical.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MeasurementRepository extends JpaRepository<Measurement, Integer> {

  @Query("SELECT MAX(m.id) FROM Measurement m")
  Integer findMaxId();

  List<Measurement> findByPerson_PersonSourceValueOrderByMeasurementDatetimeDesc(String personSourceValue);

  List<Measurement> findByPerson_PersonSourceValueOrderByMeasurementDateDesc(String personSourceValue);

  // ✅ NEW (limit support)
  List<Measurement> findByPerson_PersonSourceValueOrderByMeasurementDatetimeDesc(
          String personSourceValue,
          Pageable pageable
  );
}
