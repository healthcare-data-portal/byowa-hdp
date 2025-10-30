package dev.byowa.hdp.service;

import dev.byowa.hdp.dto.UserSummary;
import dev.byowa.hdp.model.Role;
import dev.byowa.hdp.model.User;
import dev.byowa.hdp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    public List<UserSummary> listUsers() {
        return userRepository.findAll().stream()
                .map(u -> new UserSummary(u.getId(), u.getUsername(), u.getRole().name()))
                .toList();
    }

    public UserSummary changeRole(Long userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setRole(newRole);
        userRepository.save(user);
        return new UserSummary(user.getId(), user.getUsername(), user.getRole().name());
    }
}
