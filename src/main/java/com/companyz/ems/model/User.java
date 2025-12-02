package com.companyz.ems.model;

import java.time.LocalDateTime;
import java.util.List;

// Employee link is stored by id (empId) to avoid embedding employee objects

/**
 * Represents a user account for application access.
 * <p>
 * Each user is associated with an employee record and one or more roles
 * that determine permissions and access levels within the system.
 * Roles are stored in the database and can be expanded without code changes.
 * </p>
 */
public class User {
    /** Primary key identifier for the user account. */
    private int userId;

    /** Unique login username for the user. */
    private String username;

    /** Hashed password for authentication (stored as varbinary in DB). */
    private byte[] passwordHash;

    /** Salt used for password hashing (stored as varbinary in DB). */
    private byte[] passwordSalt;

    /** Flag indicating whether the account is active. */
    private boolean active;

    /** Roles associated with this user account (many-to-many via user_roles). */
    private List<Role> roles;

    /** Employee id linked to this user account (via user_employee_link). */
    private Integer empId;

    /** Timestamp when the user account was created. */
    private LocalDateTime createdAt;

    /** Timestamp when the user account was last updated. */
    private LocalDateTime updatedAt;

    /**
     * Constructs an empty User with default values.
     */
    public User() {
    }

    /**
     * Constructs a User with all specified values.
     *
     * @param userId the primary key identifier
     * @param username the login username
     * @param passwordHash the hashed password bytes
     * @param passwordSalt the salt bytes for password hashing
     * @param active whether the account is active
         * @param roles list of roles assigned to this user
         * @param empId the associated employee id (or {@code null})
     * @param createdAt when the account was created
     * @param updatedAt when the account was last updated
     */
        public User(int userId, String username, byte[] passwordHash, byte[] passwordSalt,
            boolean active, List<Role> roles, Integer empId,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
        this.active = active;
        this.roles = roles;
        this.empId = empId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Returns the user identifier.
     *
     * @return the user id
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the user identifier.
     *
     * @param userId the id to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Returns the username.
     *
     * @return the username or {@code null} if not set
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     *
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the hashed password.
     *
     * @return the password hash bytes or {@code null} if not set
     */
    public byte[] getPasswordHash() {
        return passwordHash;
    }

    /**
     * Sets the hashed password.
     *
     * @param passwordHash the password hash bytes to set
     */
    public void setPasswordHash(byte[] passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * Returns the password salt.
     *
     * @return the salt bytes or {@code null} if not set
     */
    public byte[] getPasswordSalt() {
        return passwordSalt;
    }

    /**
     * Sets the password salt.
     *
     * @param passwordSalt the salt bytes to set
     */
    public void setPasswordSalt(byte[] passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    /**
     * Returns whether the user account is active.
     *
     * @return {@code true} if active, {@code false} if inactive
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets whether the user account is active.
     *
     * @param active {@code true} to activate, {@code false} to deactivate
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Returns the list of roles assigned to this user.
     *
     * @return list of {@link Role} objects or {@code null} if not set
     */
    public List<Role> getRoles() {
        return roles;
    }

    /**
     * Sets the list of roles for this user.
     *
     * @param roles list of {@link Role} objects to assign
     */
    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    /**
     * Returns the employee associated with this user.
     *
     * @return the employee id or {@code null} if not linked
     */
    public Integer getEmpId() {
        return empId;
    }

    /**
     * Sets the employee id associated with this user.
     *
     * @param empId the employee id to link
     */
    public void setEmpId(Integer empId) {
        this.empId = empId;
    }

    /**
     * Returns the account creation timestamp.
     *
     * @return the creation date/time or {@code null} if not set
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the account creation timestamp.
     *
     * @param createdAt the creation date/time to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Returns the account last-update timestamp.
     *
     * @return the update date/time or {@code null} if not set
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the account last-update timestamp.
     *
     * @param updatedAt the update date/time to set
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
