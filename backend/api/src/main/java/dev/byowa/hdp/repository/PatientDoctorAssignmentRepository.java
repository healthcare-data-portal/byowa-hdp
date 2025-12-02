package dev.byowa.hdp.repository;

import dev.byowa.hdp.model.PatientDoctorAssignment;
import dev.byowa.hdp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientDoctorAssignmentRepository extends JpaRepository<PatientDoctorAssignment, Long> {

    boolean existsByDoctorAndPatient(User doctor, User patient);

    long countByDoctor(User doctor);

    List<PatientDoctorAssignment> findByDoctor(User doctor);
}
