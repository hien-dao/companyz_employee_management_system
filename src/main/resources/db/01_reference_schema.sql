-- Database and defaults
CREATE DATABASE IF NOT EXISTS employeeData CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE employeeData;

-- Optional: enforce foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- =========================
-- Reference tables
-- These define countries, states, cities, and employment types.
-- They are used as lookup tables for employees and divisions.
-- =========================

CREATE TABLE countries (
  countryid INT AUTO_INCREMENT PRIMARY KEY,
  country_abbreviation VARCHAR(3) NOT NULL UNIQUE, -- e.g., 'US'
  country_name VARCHAR(125) NOT NULL               -- e.g., 'United States'
);

CREATE TABLE states (
  stateid INT AUTO_INCREMENT PRIMARY KEY,
  state_abbreviation VARCHAR(3) NOT NULL UNIQUE,   -- e.g., 'GA'
  state_name VARCHAR(125) NOT NULL                 -- e.g., 'Georgia'
);

CREATE TABLE cities (
  cityid INT AUTO_INCREMENT PRIMARY KEY,
  city_name VARCHAR(125) NOT NULL,
  stateid INT NULL,                                -- optional link to state
  countryid INT NULL,                              -- optional link to country
  FOREIGN KEY (stateid) REFERENCES states(stateid) ON DELETE SET NULL,
  FOREIGN KEY (countryid) REFERENCES countries(countryid) ON DELETE SET NULL
);

CREATE TABLE employment_types (
  employment_type_id INT AUTO_INCREMENT PRIMARY KEY,
  employment_type_name VARCHAR(64) NOT NULL UNIQUE, -- 'FULL_TIME','PART_TIME','CONTRACTOR', 'INTERN'
  is_active TINYINT(1) NOT NULL DEFAULT 1           -- flag to deactivate types without deleting
);

