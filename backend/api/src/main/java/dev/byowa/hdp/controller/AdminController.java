package dev.byowa.hdp.controller;

import dev.byowa.hdp.dto.ChangeRoleRequest;
import dev.byowa.hdp.dto.UserSummary;
import dev.byowa.hdp.model.Role;
import dev.byowa.hdp.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<List<UserSummary>> getAllUsers() {
        return ResponseEntity.ok(adminService.listUsers());
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<UserSummary> changeRole(@PathVariable Long id, @RequestBody ChangeRoleRequest req) {
        Role newRole = req.getNewRole();
        if (newRole == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(adminService.changeRole(id, newRole));
    }
}
