package com.companyz.ems.services;

import java.util.Optional;

import com.companyz.ems.security.SessionContext;
import com.companyz.ems.model.employee.Employee;

public interface UserService {
    Optional<SessionContext> authenticateUser(String username, char[] password);

    boolean createUser(SessionContext ctx, String username, Employee employee, char[] password, String role);

    boolean assignRoles(SessionContext ctx, String username, String role);

    boolean deactivateUser(SessionContext ctx, String username);

    boolean updatePassword(SessionContext ctx, String username, char[] oldPassword, char[] newPassword);
}
