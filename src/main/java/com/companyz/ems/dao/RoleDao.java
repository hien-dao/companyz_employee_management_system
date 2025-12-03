package com.companyz.ems.dao;

import java.util.List;
import java.util.Optional;

import com.companyz.ems.model.Role;

public interface RoleDao {
    Optional<Role> findById(int roleId);
    Optional<Role> findByName(String roleName);
    List<Role> findAll();
    boolean createRole(Role role);
    boolean updateRole(Role role);
    boolean deleteRole(int roleId);
}
