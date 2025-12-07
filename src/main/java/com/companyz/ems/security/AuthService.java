package com.companyz.ems.security;

import java.util.Optional;

import com.companyz.ems.config.SecurityConfig;
import com.companyz.ems.dao.AuthEventLogger;
import com.companyz.ems.dao.UserDao;
import com.companyz.ems.model.User;

/**
 * Handles authentication and password management.
 */
public class AuthService {
    private final UserDao userDao;
    private final PasswordHasher hasher;
    private final int sessionTimeoutMinutes;

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
        this.hasher = new PasswordHasher(SecurityConfig.getPasswordStrength());
        this.sessionTimeoutMinutes = SecurityConfig.getSessionTimeoutMinutes();
    }

    /**
     * Authenticate a user by username and password.
     */
    public Optional<SessionContext> login(String username, char[] password) {
        Optional<User> userOpt = userDao.findByUsername(username);
        AuthEventLogger logger = new AuthEventLogger();

        if (userOpt.isEmpty()) {
            logger.logEvent(0, "LOGIN_FAILURE", null, "JavaFX");
            return Optional.empty();
        }

        User user = userOpt.get();
        if (!user.isActive() || !hasher.verify(password, user.getPasswordHash(), user.getPasswordSalt())) {
            logger.logEvent(user.getUserId(), "LOGIN_FAILURE", null, "JavaFX");
            return Optional.empty();
        }

        logger.logEvent(user.getUserId(), "LOGIN_SUCCESS", null, "JavaFX");
        return Optional.of(new SessionContext(user.getUserId(),
                                            user.getRoles().isEmpty() ? "EMPLOYEE" : user.getRoles().get(0).getRoleName(),
                                            user.getEmpId(),
                                            sessionTimeoutMinutes));
    }

    public void logout(SessionContext session) {
        new AuthEventLogger().logEvent(session.getUserId(), "LOGOUT", null, "JavaFX");
        session.invalidate();
    }



    /**
     * Update a user's password (hash + salt).
     */
    public boolean updatePassword(int userId, char[] newPassword) {
        PasswordHasher.HashedPassword hp = hasher.hashWithSalt(newPassword);
        return userDao.updatePassword(userId, hp.getHash(), hp.getSalt());
    }
}
