package dev.byowa.hdp.service;

import dev.byowa.hdp.dto.UserSummary;
import dev.byowa.hdp.model.Role;
import dev.byowa.hdp.model.User;
import dev.byowa.hdp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserSummary> listUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserSummary::from)
                .toList();
    }

    public UserSummary changeRole(Long userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setRole(newRole);
        userRepository.save(user);

        return UserSummary.from(user);
    }
}
