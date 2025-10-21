package dev.byowa.hdp.dto;

public class UserResponse {
    private Long id;
    private String username;
    private String role;
    private Integer personId;
    public UserResponse() {}

    public UserResponse(Long id, String username, String role, Integer personId) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.personId = personId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    public Integer getPersonId() {
        return personId;
    }
    public void setPersonId(Integer personId) {
        this.personId = personId;
    }
}