package dev.byowa.hdp.dto;

public class UpdateRoleRequest {

    private Long userId;
    private String role;

    public UpdateRoleRequest() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
