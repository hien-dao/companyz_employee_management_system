package com.companyz.ems.security;

/**
 * Provides role and ownership checks.
 */
public class AuthorizationService {

    public void requireAdmin(SessionContext ctx) {
        if (!"HR_ADMIN".equals(ctx.getRole())) {
            throw new SecurityException("Admin privileges required");
        }
    }

    public void requireEmployee(SessionContext ctx, int empId) {
        if (!"EMPLOYEE".equals(ctx.getRole()) || ctx.getEmployeeId() != empId) {
            throw new SecurityException("Access denied");
        }
    }
}
