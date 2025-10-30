package dev.byowa.hdp.dto;

public class UserSummary {
    private Long id;
    private String username;
    private String role;

    public UserSummary() {}
    public UserSummary(Long id, String username, String role) {
        this.id = id; this.username = username; this.role = role;
    }
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public void setId(Long id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setRole(String role) { this.role = role; }
}


