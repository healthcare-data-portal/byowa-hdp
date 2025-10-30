package dev.byowa.hdp.dto;

import dev.byowa.hdp.model.Role;

public class ChangeRoleRequest {
    private Role newRole;

    public ChangeRoleRequest() { }

    public ChangeRoleRequest(Role newRole) { this.newRole = newRole; }

    public Role getNewRole() { return newRole; }
    public void setNewRole(Role newRole) { this.newRole = newRole; }
}
