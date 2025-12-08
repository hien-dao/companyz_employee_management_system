package com.companyz.ems.security;

import java.time.Duration;
import java.time.Instant;

/**
 * Represents a logged-in user session with timeout enforcement.
 */
public class SessionContext {
    private final int userId;
    private final String role;
    private final Integer employeeId;
    private final int timeoutMinutes;
    private final Instant loginTime;
    private Instant lastActivityTime;
    private boolean active;

    public SessionContext(int userId, String role, Integer employeeId, int timeoutMinutes) {
        this.userId = userId;
        this.role = role;
        this.employeeId = employeeId;
        this.timeoutMinutes = timeoutMinutes;
        this.loginTime = Instant.now();
        this.lastActivityTime = loginTime;
        this.active = true;
    }

    public int getUserId() { return userId; }
    public String getRole() { return role; }
    public Integer getEmployeeId() { return employeeId; }

    /**
     * Update last activity timestamp (e.g., on any user action).
     */
    public void touch() {
        this.lastActivityTime = Instant.now();
    }

    /**
     * Returns the absolute expiry time based on last activity.
     */
    public Instant getExpiryTime() {
        return lastActivityTime.plus(Duration.ofMinutes(timeoutMinutes));
    }

    /**
     * Check if the session has expired.
     */
    public boolean isExpired() {
        return Instant.now().isAfter(getExpiryTime());
    }

    /**
     * Check if the session is still active (not invalidated and not expired).
     */
    public boolean isActive() {
        return active && !isExpired();
    }

    /**
     * Invalidate the session manually (e.g., on logout).
     */
    public void inValidated() {
        this.active = false;
    }

    /**
     * Remaining minutes until expiration (for UI display or warnings).
     */
    public long remainingMinutes() {
        if (!isActive()) return 0;
        Duration remaining = Duration.between(Instant.now(), getExpiryTime());
        return Math.max(0, remaining.toMinutes());
    }
}
