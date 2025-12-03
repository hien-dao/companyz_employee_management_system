package com.companyz.ems.dao;

import java.util.List;
import java.util.Optional;

import com.companyz.ems.model.employee.JobTitle;

public interface JobTitleDao {
    Optional<JobTitle> findById(int jobTitleId);
    Optional<JobTitle> findByName(String jobTitleName);
    List<JobTitle> findAll();
    JobTitle createJobTitle(JobTitle jobTitle);
    JobTitle updateJobTitle(JobTitle jobTitle);
    boolean deleteJobTitle(int jobTitleId);
}
