package dev.byowa.hdp.dto;

import dev.byowa.hdp.model.Role;
import dev.byowa.hdp.model.User;

public class UserSummary {

    private Long id;
    private String email;
    private String username;
    private String role;

    public UserSummary() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public static UserSummary from(User user) {
        UserSummary dto = new UserSummary();
        dto.setId(user.getId());

        String login = user.getUsername();
        dto.setEmail(login);
        dto.setUsername(login);

        Role r = user.getRole();
        dto.setRole(r != null ? r.name() : null);

        return dto;
    }
}
