package com.companyz.ems.dao;

import java.util.List;
import java.util.Optional;

import com.companyz.ems.model.employee.JobTitle;

public interface JobTitleDao {
    Optional<JobTitle> findById(int jobTitleId);
    List<JobTitle> findAll();
    boolean createJobTitle(JobTitle jobTitle);
    boolean updateJobTitle(JobTitle jobTitle);
    boolean deleteJobTitle(int jobTitleId);
}
