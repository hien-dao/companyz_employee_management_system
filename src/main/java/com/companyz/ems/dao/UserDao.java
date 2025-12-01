package com.companyz.ems.dao;

import java.util.List;
import java.util.Optional;

import com.companyz.ems.model.Role;
import com.companyz.ems.model.User;

public interface UserDao {
    Optional<User> findById(int userId);
    Optional<User> findByUsername(String username);
    boolean createUser(User user, int empId, List<Role> roles);
    boolean updatePassword(int userId, byte[] newHash, byte[] newSalt);
    boolean deactivateUser(int userId);
    List<Role> getUserRoles(int userId);
}
