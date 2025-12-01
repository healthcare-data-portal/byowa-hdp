package dev.byowa.hdp.controller;

import dev.byowa.hdp.dto.DoctorPatientDto;
import dev.byowa.hdp.dto.ProviderDto;
import dev.byowa.hdp.model.Role;
import dev.byowa.hdp.model.User;
import dev.byowa.hdp.repository.UserRepository;
import dev.byowa.hdp.service.PatientDoctorAssignmentService;
import dev.byowa.hdp.service.ProviderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/providers")
public class ProviderController {

    private final ProviderService providerService;
    private final UserRepository userRepository;
    private final PatientDoctorAssignmentService assignmentService;

    public ProviderController(ProviderService providerService,
                              UserRepository userRepository,
                              PatientDoctorAssignmentService assignmentService) {
        this.providerService = providerService;
        this.userRepository = userRepository;
        this.assignmentService = assignmentService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProviderDto> getProvider(@PathVariable Integer id) {
        return providerService.getProviderById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    public ResponseEntity<ProviderDto> getCurrentProvider() {
        return providerService.getCurrentProviderForLoggedInUser()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/patients")
    public ResponseEntity<?> getPatientsForCurrentDoctor(@PathVariable Integer id,
                                                         Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String username = authentication.getName();
        User doctor = userRepository.findByUsername(username).orElse(null);
        if (doctor == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        if (doctor.getRole() != Role.DOCTOR && doctor.getRole() != Role.ADMIN) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        List<DoctorPatientDto> patients = assignmentService.getPatientsForDoctor(doctor);
        return ResponseEntity.ok(patients);
    }
}
