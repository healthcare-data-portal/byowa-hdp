// dev.byowa.hdp.controller.AdminController.java
package dev.byowa.hdp.controller;

import dev.byowa.hdp.dto.UpdateRoleRequest;
import dev.byowa.hdp.dto.UserSummary;
import dev.byowa.hdp.model.Role;
import dev.byowa.hdp.model.User;
import dev.byowa.hdp.repository.UserRepository;
import dev.byowa.hdp.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AdminController(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    private boolean isAdmin(HttpServletRequest req) {
        String auth = req.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) return false;
        String token = auth.substring(7);
        try {
            return "ADMIN".equalsIgnoreCase(jwtService.extractRole(token)) && !jwtService.isExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> listUsers(HttpServletRequest req) {
        if (!isAdmin(req)) return ResponseEntity.status(403).body("Forbidden");
        List<UserSummary> users = userRepository.findAll()
                .stream()
                .map(u -> new UserSummary(u.getId(), u.getUsername(), u.getRole().name()))
                .toList();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> changeRole(@PathVariable Long id,
                                        @RequestBody UpdateRoleRequest body,
                                        HttpServletRequest req) {
        if (!isAdmin(req)) return ResponseEntity.status(403).body("Forbidden");

        String newRoleRaw = body.getRole();
        if (newRoleRaw == null) return ResponseEntity.badRequest().body("role is required");

        Role newRole;
        try { newRole = Role.valueOf(newRoleRaw.toUpperCase()); }
        catch (IllegalArgumentException ex) { return ResponseEntity.badRequest().body("Invalid role"); }

        return userRepository.findById(id)
                .map(u -> {
                    u.setRole(newRole);
                    userRepository.save(u);
                    return ResponseEntity.ok(new UserSummary(u.getId(), u.getUsername(), u.getRole().name()));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
