package com.companyz.ems.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.companyz.ems.model.employee.JobTitle;

public class JobTitleDaoImpl extends AbstractDao implements JobTitleDao {

    @Override
    public Optional<JobTitle> findById(int jobTitleId) {
        String sql = "SELECT job_title_id, title_name, description FROM job_titles WHERE job_title_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = prepareStatement(conn, sql, jobTitleId);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return Optional.of(mapJobTitle(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<JobTitle> findByName(String jobTitleName) {
        String sql = "SELECT job_title_id, title_name, description FROM job_titles WHERE title_name = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = prepareStatement(conn, sql, jobTitleName);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return Optional.of(mapJobTitle(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<JobTitle> findAll() {
        List<JobTitle> titles = new ArrayList<>();
        String sql = "SELECT job_title_id, title_name, description FROM job_titles";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                titles.add(mapJobTitle(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return titles;
    }

    @Override
    public JobTitle createJobTitle(JobTitle jobTitle) {
        String sql = "INSERT INTO job_titles (title_name, description) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, jobTitle.getTitleName());
            stmt.setString(2, jobTitle.getDescription());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    jobTitle.setJobTitleId(generatedKeys.getInt(1));
                }
            }
            return jobTitle;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public JobTitle updateJobTitle(JobTitle jobTitle) {
        String sql = "UPDATE job_titles SET title_name = ?, description = ? WHERE job_title_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, jobTitle.getTitleName());
            stmt.setString(2, jobTitle.getDescription());
            stmt.setInt(3, jobTitle.getJobTitleId());
            stmt.executeUpdate();
            return jobTitle;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean deleteJobTitle(int jobTitleId) {
        String sql = "DELETE FROM job_titles WHERE job_title_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = prepareStatement(conn, sql, jobTitleId)) {
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private JobTitle mapJobTitle(ResultSet rs) throws SQLException {
        JobTitle jobTitle = new JobTitle();
        jobTitle.setJobTitleId(rs.getInt("job_title_id"));
        jobTitle.setTitleName(rs.getString("title_name"));
        jobTitle.setDescription(rs.getString("description"));
        return jobTitle;
    }
}
