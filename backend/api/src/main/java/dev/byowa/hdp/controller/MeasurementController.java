package dev.byowa.hdp.controller;

import dev.byowa.hdp.dto.MeasurementResponse;
import dev.byowa.hdp.model.User;
import dev.byowa.hdp.model.clinical.Measurement;
import dev.byowa.hdp.repository.MeasurementRepository;
import dev.byowa.hdp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/measurements")
public class MeasurementController {

    @Autowired
    private MeasurementRepository measurementRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Doctor/Admin view:
     * Get measurements for a specific patient by person_source_value (your SSN-like key, e.g. "patient002")
     * Example: GET /measurements/patient/patient002?limit=200
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<MeasurementResponse>> getMeasurementsForPatient(
            @PathVariable String patientId,
            @RequestParam(required = false, defaultValue = "200") int limit
    ) {
        int safeLimit = Math.max(1, Math.min(limit, 1000));

        // Fast path with limit via Pageable (requires repo method with Pageable)
        Pageable pageable = PageRequest.of(0, safeLimit);
        List<Measurement> list = measurementRepository
                .findByPerson_PersonSourceValueOrderByMeasurementDatetimeDesc(patientId, pageable);

        // Stable sort (handles null datetimes by falling back to date)
        list.sort(Comparator.comparing(this::sortInstant).reversed());

        List<MeasurementResponse> response = list.stream()
                .map(m -> toResponse(m, patientId))
                .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * Patient view:
     * Patient can only access his own measurements
     * Example: GET /measurements/me?limit=200
     */
    @GetMapping("/me")
    public ResponseEntity<List<MeasurementResponse>> getMyMeasurements(
            @RequestParam(required = false, defaultValue = "200") int limit
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(401).build();
        }

        // enforce patient role
        boolean isPatient = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_PATIENT".equals(a.getAuthority()));
        if (!isPatient) {
            return ResponseEntity.status(403).build();
        }

        String username = auth.getName(); // e.g. patient002@hdp.com

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty() || userOpt.get().getPerson() == null) {
            return ResponseEntity.status(404).build();
        }

        String patientId = userOpt.get().getPerson().getPersonSourceValue(); // e.g. patient002
        if (patientId == null || patientId.isBlank()) {
            return ResponseEntity.status(404).build();
        }

        int safeLimit = Math.max(1, Math.min(limit, 1000));
        Pageable pageable = PageRequest.of(0, safeLimit);

        List<Measurement> list = measurementRepository
                .findByPerson_PersonSourceValueOrderByMeasurementDatetimeDesc(patientId, pageable);

        // Stable sort (handles null datetimes by falling back to date)
        list.sort(Comparator.comparing(this::sortInstant).reversed());

        List<MeasurementResponse> response = list.stream()
                .map(m -> toResponse(m, patientId))
                .toList();

        return ResponseEntity.ok(response);
    }

    // --- helpers ---

    private MeasurementResponse toResponse(Measurement m, String patientId) {
        MeasurementResponse r = new MeasurementResponse();
        r.setId(m.getId());
        r.setPatientId(patientId);
        r.setMeasurementDate(m.getMeasurementDate());
        r.setMeasurementDatetime(m.getMeasurementDatetime());
        r.setSource(m.getMeasurementSourceValue());

        // numeric first, else text value
        if (m.getValueAsNumber() != null) {
            r.setValue(m.getValueAsNumber().toPlainString());
            r.setUnit(m.getUnitSourceValue());
        } else {
            r.setValue(m.getValueSourceValue());
            r.setUnit(m.getUnitSourceValue()); // often null for ABO/Rh
        }
        return r;
    }

    private Instant sortInstant(Measurement m) {
        if (m.getMeasurementDatetime() != null) return m.getMeasurementDatetime();
        if (m.getMeasurementDate() != null) return m.getMeasurementDate().atStartOfDay().toInstant(ZoneOffset.UTC);
        return Instant.EPOCH;
    }
}
