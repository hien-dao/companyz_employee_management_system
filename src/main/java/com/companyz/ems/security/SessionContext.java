package com.companyz.ems.security;

/**
 * Represents a logged-in user session with timeout enforcement.
 */
public class SessionContext {
    private final int userId;
    private final String role;
    private final Integer employeeId;
    private final int timeoutMinutes;
    private final long loginTime;
    private long lastActivityTime;
    private boolean active;

    public SessionContext(int userId, String role, Integer employeeId, int timeoutMinutes) {
        this.userId = userId;
        this.role = role;
        this.employeeId = employeeId;
        this.timeoutMinutes = timeoutMinutes;
        this.loginTime = System.currentTimeMillis();
        this.lastActivityTime = loginTime;
        this.active = true;
    }

    public int getUserId() { return userId; }
    public String getRole() { return role; }
    public Integer getEmployeeId() { return employeeId; }

    public void touch() { this.lastActivityTime = System.currentTimeMillis(); }

    public boolean isExpired() {
        long elapsed = System.currentTimeMillis() - lastActivityTime;
        return elapsed > timeoutMinutes * 60_000L;
    }

    public boolean isActive() { return active && !isExpired(); }

    public void invalidate() { this.active = false; }
}
