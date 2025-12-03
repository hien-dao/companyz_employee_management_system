package com.companyz.ems.dao;

import java.util.List;
import java.util.Optional;

import com.companyz.ems.model.Role;

public interface RoleDao {
    List<Role> findAll();
    Optional<Role> findById(int roleId);
    boolean createRole(Role role);
    boolean updateRole(Role role);
    boolean deleteRole(int roleId);
}
