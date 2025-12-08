package com.companyz.ems.security;

/**
 * Provides role and ownership checks for all services.
 */
public class AuthorizationService {

    /** Require HR admin privileges */
    public void requireAdmin(SessionContext ctx) {
        if (!"HR_ADMIN".equalsIgnoreCase(ctx.getRole())) {
            throw new SecurityException("Admin privileges required");
        }
    }

    /** Require that the user is the owner of the resource (their own employee record) */
    public void requireSelf(SessionContext ctx, int employeeId) {
        if (ctx.getEmployeeId() == null || ctx.getEmployeeId() != employeeId) {
            throw new SecurityException("Access denied: employees can only access their own record");
        }
    }

    /** Allow either HR admin or the employee themselves */
    public void requireSelfOrAdmin(SessionContext ctx, int employeeId) {
        if ("HR_ADMIN".equalsIgnoreCase(ctx.getRole())) {
            return; // admin always allowed
        }
        if (ctx.getEmployeeId() != null && ctx.getEmployeeId() == employeeId) {
            return; // employee accessing their own record
        }
        throw new SecurityException("Access denied");
    }

    /** Convenience: get the employeeId of the current session */
    public int getSessionEmployeeId(SessionContext ctx) {
        if (ctx.getEmployeeId() == null) {
            throw new SecurityException("Session not linked to an employee");
        }
        return ctx.getEmployeeId();
    }
}
