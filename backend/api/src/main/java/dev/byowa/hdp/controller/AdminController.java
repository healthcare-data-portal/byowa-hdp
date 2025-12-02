package dev.byowa.hdp.controller;

import dev.byowa.hdp.dto.AssignPatientRequest;
import dev.byowa.hdp.dto.UpdateRoleRequest;
import dev.byowa.hdp.dto.UserSummary;
import dev.byowa.hdp.model.Role;
import dev.byowa.hdp.model.User;
import dev.byowa.hdp.repository.UserRepository;
import dev.byowa.hdp.service.JwtService;
import dev.byowa.hdp.service.PatientDoctorAssignmentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PatientDoctorAssignmentService assignmentService;

    public AdminController(UserRepository userRepository,
                           JwtService jwtService,
                           PatientDoctorAssignmentService assignmentService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.assignmentService = assignmentService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserSummary>> listUsers(HttpServletRequest req) {
        if (!isAdmin(req)) {
            return ResponseEntity.status(403).build();
        }

        List<UserSummary> result = userRepository.findAll()
                .stream()
                .map(UserSummary::from)
                .toList();

        return ResponseEntity.ok(result);
    }

    @PostMapping("/role")
    public ResponseEntity<?> changeRole(@RequestBody UpdateRoleRequest request,
                                        HttpServletRequest req) {
        if (!isAdmin(req)) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        if (request.getUserId() == null || request.getRole() == null) {
            return ResponseEntity.badRequest().body("userId and role are required");
        }

        User user = userRepository.findById(request.getUserId()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        try {
            Role newRole = Role.valueOf(request.getRole());
            user.setRole(newRole);
            userRepository.save(user);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("Invalid role: " + request.getRole());
        }
    }

    @PostMapping("/assignments")
    public ResponseEntity<?> assignPatient(@RequestBody AssignPatientRequest body,
                                           HttpServletRequest req) {
        if (!isAdmin(req)) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        if (body.getPatientId() == null || body.getDoctorId() == null) {
            return ResponseEntity.badRequest().body("patientId and doctorId are required");
        }

        try {
            assignmentService.assignPatientToDoctor(body.getPatientId(), body.getDoctorId());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    private boolean isAdmin(HttpServletRequest req) {
        String authHeader = req.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false;
        }

        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);
        if (username == null) {
            return false;
        }

        User user = userRepository.findByUsername(username).orElse(null);
        return user != null && user.getRole() == Role.ADMIN;
    }
}
