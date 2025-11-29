package com.companyz.ems.model;

/**
 * Represents a user account for application access.
 * <p>
 * Each user is associated with an employee and a role that determines
 * permissions and access levels within the system.
 * </p>
 */
public class User {
    /** Primary key identifier for the user account. */
    private final int userId;

    /** Unique login username for the user. */
    private final String username;

    /** Hashed password for authentication. */
    final String passwordHash;

    /** Salt used for password hashing. */
    final String passwordSalt;

    /** Role associated with this user account. */
    private final Role role;

    /** Employee record linked to this user account. */
    private final Employee employee;

    /**
     * Constructs a new User instance.
     *
     * @param userId        unique identifier
     * @param username      login username
     * @param passwordHash  hashed password
     * @param passwordSalt  salt for hashing
     * @param role          associated role
     * @param employee      linked employee
     */
    public User (int userId, String username, String passwordHash, String passwordSalt, Role role, Employee employee) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
        this.role = role;
        this.employee = employee;
    }

    /**
     * Returns the username.
     *
     * @return username or {@code null}
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the user's role.
     *
     * @return {@link Role} or {@code null}
     */
    public Role getRole() {
        return role;
    }

    /**
     * Returns the employee associated with this user.
     *
     * @return {@link Employee} or {@code null}
     */
    public Employee getEmployee() {
        return employee;
    }
}
