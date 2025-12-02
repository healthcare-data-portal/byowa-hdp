package dev.byowa.hdp.service;

import dev.byowa.hdp.dto.DoctorPatientDto;
import dev.byowa.hdp.model.PatientDoctorAssignment;
import dev.byowa.hdp.model.Role;
import dev.byowa.hdp.model.User;
import dev.byowa.hdp.model.clinical.Person;
import dev.byowa.hdp.repository.PatientDoctorAssignmentRepository;
import dev.byowa.hdp.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PatientDoctorAssignmentService {

    private final PatientDoctorAssignmentRepository assignmentRepository;
    private final UserRepository userRepository;

    public PatientDoctorAssignmentService(PatientDoctorAssignmentRepository assignmentRepository,
                                          UserRepository userRepository) {
        this.assignmentRepository = assignmentRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void assignPatientToDoctor(Long patientUserId, Long doctorUserId) {
        User patient = userRepository.findById(patientUserId)
                .orElseThrow(() -> new IllegalArgumentException("Patient user not found"));

        User doctor = userRepository.findById(doctorUserId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor user not found"));

        if (patient.getRole() != Role.PATIENT) {
            throw new IllegalArgumentException("Selected patient user does not have PATIENT role");
        }
        if (doctor.getRole() != Role.DOCTOR) {
            throw new IllegalArgumentException("Selected doctor user does not have DOCTOR role");
        }

        if (assignmentRepository.existsByDoctorAndPatient(doctor, patient)) {
            return;
        }

        PatientDoctorAssignment assignment = new PatientDoctorAssignment(patient, doctor);
        assignmentRepository.save(assignment);
    }

    @Transactional(readOnly = true)
    public long countPatientsForDoctor(User doctor) {
        return assignmentRepository.countByDoctor(doctor);
    }

    @Transactional(readOnly = true)
    public List<DoctorPatientDto> getPatientsForDoctor(User doctor) {
        List<PatientDoctorAssignment> assignments = assignmentRepository.findByDoctor(doctor);

        return assignments.stream()
                .map(a -> {
                    User patient = a.getPatient();
                    DoctorPatientDto dto = new DoctorPatientDto();
                    dto.setId(patient.getId());

                    String name = patient.getFullName();
                    if (name == null || name.isBlank()) {
                        name = patient.getUsername();
                    }
                    dto.setName(name);
                    dto.setEmail(patient.getUsername());

                    Person person = patient.getPerson();
                    if (person != null) {
                        Integer year = person.getYearOfBirth();
                        Integer month = person.getMonthOfBirth();
                        Integer day = person.getDayOfBirth();
                        if (year != null && month != null && day != null) {
                            LocalDate date = LocalDate.of(year, month, day);
                            dto.setDob(date.toString());
                        }

                        if (person.getGenderSourceValue() != null) {
                            dto.setGender(person.getGenderSourceValue());
                        }
                    }

                    dto.setConsentGiven(false);

                    return dto;
                })
                .toList();
    }
}
