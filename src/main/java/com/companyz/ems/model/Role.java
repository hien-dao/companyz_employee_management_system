package com.companyz.ems.model;

/**
 * Represents a role that defines permissions and access levels within the system.
 * <p>
 * Roles are stored in the database and can be expanded without code changes.
 * Multiple users can be assigned to the same role to simplify permission management.
 * </p>
 */
public class Role {
    /** Primary key identifier for the role. */
    private int roleId;

    /** Unique role name (e.g., "HR_ADMIN", "PAYROLL_MANAGER", "REGULAR_EMPLOYEE"). */
    private String roleName;

    /** Human-readable description of the role's purpose and responsibilities. */
    private String description;

    /**
     * Constructs an empty Role with default values.
     */
    public Role() {
    }

    /**
     * Constructs a Role with specified values.
     *
     * @param roleId the primary key identifier
     * @param roleName the name of the role
     * @param description a description of the role
     */
    public Role(int roleId, String roleName, String description) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.description = description;
    }

    /**
     * Returns the role identifier.
     *
     * @return the role id
     */
    public int getRoleId() {
        return roleId;
    }

    /**
     * Sets the role identifier.
     *
     * @param roleId the id to set
     */
    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    /**
     * Returns the role name.
     *
     * @return the role name or {@code null} if not set
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * Sets the role name.
     *
     * @param roleName the name to set
     */
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    /**
     * Returns the role description.
     *
     * @return the description or {@code null} if not set
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the role description.
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
