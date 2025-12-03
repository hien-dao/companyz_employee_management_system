package com.companyz.ems.dao;

import java.util.List;
import java.util.Optional;

import com.companyz.ems.model.employee.EmploymentStatus;

public interface EmploymentStatusDao {
    Optional<EmploymentStatus> findById(int statusId);
    Optional<EmploymentStatus> findByName(String statusName);
    List<EmploymentStatus> findAll();
    boolean createStatus(EmploymentStatus status);
    boolean updateStatus(EmploymentStatus status);
    boolean deleteStatus(int statusId);
}
