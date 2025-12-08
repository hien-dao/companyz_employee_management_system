package com.companyz.ems.services;

import java.util.List;
import java.util.Optional;

import com.companyz.ems.config.SecurityConfig;
import com.companyz.ems.dao.AuthEventLogger;
import com.companyz.ems.dao.ChangeLogger;
import com.companyz.ems.dao.RoleDao;
import com.companyz.ems.dao.RoleDaoImpl;
import com.companyz.ems.dao.UserDao;
import com.companyz.ems.dao.UserDaoImpl;
import com.companyz.ems.model.Role;
import com.companyz.ems.model.User;
import com.companyz.ems.model.employee.Employee;
import com.companyz.ems.security.AuthService;
import com.companyz.ems.security.AuthorizationService;
import com.companyz.ems.security.PasswordHasher;
import com.companyz.ems.security.PasswordHasher.HashedPassword;
import com.companyz.ems.security.SessionContext;

public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final RoleDao roleDao;
    private final ChangeLogger changeLogger;
    private final AuthEventLogger authEventLogger;
    private final AuthService authService;
    private final AuthorizationService authzService;

    public UserServiceImpl(UserDao userDao,
                           RoleDao roleDao,
                           ChangeLogger changeLogger,
                           AuthEventLogger authEventLogger,
                           AuthService authService,
                           AuthorizationService authzService) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.changeLogger = changeLogger;
        this.authEventLogger = authEventLogger;
        this.authService = authService;
        this.authzService = authzService;
    }

    public UserServiceImpl() {
        this.userDao = new UserDaoImpl();
        this.roleDao = new RoleDaoImpl();
        this.changeLogger = new ChangeLogger();
        this.authEventLogger = new AuthEventLogger();
        this.authService = new AuthService(this.userDao);
        this.authzService = new AuthorizationService();
    }

    @Override
    public Optional<SessionContext> authenticateUser(String username, String password) {
        Optional<SessionContext> ctx = authService.login(username, password.toCharArray());
        ctx.ifPresent(session -> {
            // Use null for IP and "javafx" for user agent
            authEventLogger.logEvent(session.getUserId(), "LOGIN", null, "javafx");
        });
        return ctx;
    }


    @Override
    public boolean createUser(SessionContext ctx, String username, Employee employee, char[] password, String role) {
        authzService.requireAdmin(ctx);

        HashedPassword hp = new PasswordHasher(SecurityConfig.getPasswordStrength())
                        .hashWithSalt(password);
        Optional<Role> r = roleDao.findByName(role);
        if (r.isEmpty()) {
            throw new IllegalArgumentException("Role not found: " + role);
        }

        User user = new User(
            0, // let DB assign ID
            username,
            hp.getHash(),
            hp.getSalt(),
            true,
            List.of(r.get()),
            employee.getEmpId(),
            null,
            null
        );

        boolean created = userDao.createUser(user, employee.getEmpId(), List.of(r.get()));

        if (created) {
            changeLogger.logChange("users", username, "CREATE",
                    ctx.getUserId(), "{}", "{username:" + username + ", role:" + role + "}");
        }
        return created;
    }

    @Override
    public boolean assignRoles(SessionContext ctx, String username, String role) {
        authzService.requireAdmin(ctx);

        Optional<User> userOpt = userDao.findByUsername(username);
        Optional<Role> roleOpt = roleDao.findByName(role);
        if (userOpt.isEmpty() || roleOpt.isEmpty()) {
            return false;
        }

        boolean added = userDao.addRoleToUser(userOpt.get().getUserId(), roleOpt.get().getRoleId());
        if (added) {
            changeLogger.logChange("user_roles", username, "ASSIGN",
                    ctx.getUserId(), "{}", "{role:" + role + "}");
        }
        return added;
    }

    @Override
    public boolean deactivateUser(SessionContext ctx, String username) {
        authzService.requireAdmin(ctx);

        Optional<User> userOpt = userDao.findByUsername(username);
        if (userOpt.isEmpty()) {
            return false;
        }

        boolean deactivated = userDao.deactivateUser(userOpt.get().getUserId());
        if (deactivated) {
            changeLogger.logChange("users", username, "DEACTIVATE",
                    ctx.getUserId(), "{active:true}", "{active:false}");
        }
        return deactivated;
    }

    @Override
    public boolean updatePassword(SessionContext ctx, String username, char[] oldPassword, char[] newPassword) {
        Optional<User> userOpt = userDao.findByUsername(username);
        if (userOpt.isEmpty()) {
            return false;
        }
        User user = userOpt.get();

        authzService.requireSelfOrAdmin(ctx, user.getEmpId());

        boolean updated = authService.updatePassword(user.getUserId(), newPassword);
        if (updated) {
            changeLogger.logChange("users", username, "UPDATE_PASSWORD",
                    ctx.getUserId(), "{password:old}", "{password:new}");
        }
        return updated;
    }
}
