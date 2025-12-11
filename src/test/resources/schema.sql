CREATE DATABASE employeeData CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
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
  employment_type_name VARCHAR(64) NOT NULL UNIQUE -- 'FULL_TIME','PART_TIME','CONTRACTOR', 'INTERN'
);

CREATE TABLE employees (
  empid INT AUTO_INCREMENT PRIMARY KEY,
  fname VARCHAR(65) NOT NULL,
  lname VARCHAR(65) NOT NULL,
  salary DECIMAL(12,2) NOT NULL,
  ssn_last4 CHAR(4) NOT NULL,            -- last 4 digits for display
  ssn_hash BINARY(32) NOT NULL,          -- raw SHA-256 digest (32 bytes)
  ssn_enc VARBINARY(256) NOT NULL,       -- AES encrypted SSN (multiple of 16 bytes)
  ssn_iv BINARY(16) NOT NULL,            -- AES IV (always 16 bytes)
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE employee_employment_types (
  employment_type_id INT NOT NULL,
  empid INT NOT NULL PRIMARY KEY,
  is_active TINYINT(1) NOT NULL DEFAULT 1,           -- flag to deactivate types without deleting
  FOREIGN KEY (empid) REFERENCES employees(empid) ON DELETE CASCADE,
  FOREIGN KEY (employment_type_id) REFERENCES employment_types(employment_type_id) ON DELETE CASCADE
);

-- Indexes for fast search
CREATE INDEX idx_employees_name ON employees (lname, fname);
CREATE INDEX idx_employees_ssn_hash ON employees (ssn_hash);

CREATE TABLE contact_types (
  contact_type_id INT AUTO_INCREMENT PRIMARY KEY,
  type_name VARCHAR(64) NOT NULL UNIQUE -- e.g., 'EMAIL','PHONE','FAX','LINKEDIN'
);

-- Employee contact info (supports multiple contacts per employee)
CREATE TABLE employee_contact (
  contact_id INT AUTO_INCREMENT PRIMARY KEY,
  empid INT NOT NULL,                          -- FK to employees
  contact_type_id INT NOT NULL,                -- FK to contact_types
  contact_value VARCHAR(128) NOT NULL,         -- actual email, phone, etc.
  is_primary TINYINT(1) DEFAULT 0,             -- flag for preferred contact
  FOREIGN KEY (empid) REFERENCES employees(empid) ON DELETE CASCADE,
  FOREIGN KEY (contact_type_id) REFERENCES contact_types(contact_type_id) ON DELETE CASCADE
);

-- Employee demographic info
CREATE TABLE employee_demographic (
  empid INT PRIMARY KEY,                       -- FK to employees
  gender VARCHAR(50) DEFAULT NULL,
  race VARCHAR(50) DEFAULT NULL,
  dob DATE NOT NULL,                           -- required for reporting
  address_line1 VARCHAR(255) DEFAULT NULL,     -- street address
  address_line2 VARCHAR(255) DEFAULT NULL,     -- apartment, suite, etc.
  city_id INT DEFAULT NULL,                    -- FK to cities
  state_id INT DEFAULT NULL,                   -- FK to states
  country_id INT DEFAULT NULL,                 -- FK to countries
  FOREIGN KEY (empid) REFERENCES employees(empid) ON DELETE CASCADE,
  FOREIGN KEY (city_id) REFERENCES cities(cityid) ON DELETE SET NULL,
  FOREIGN KEY (state_id) REFERENCES states(stateid) ON DELETE SET NULL,
  FOREIGN KEY (country_id) REFERENCES countries(countryid) ON DELETE SET NULL
);

-- Helpful indexes
CREATE INDEX idx_employee_contact_type ON employee_contact (empid, contact_type_id);
CREATE INDEX idx_employee_dob ON employee_demographic (dob);


/* ============================================================
   04_status_schema.sql
   Purpose:
   - Tracks the lifecycle and current status of employees.
   - Differentiates between voluntary termination (resignation),
     involuntary termination (fired/laid off), and neutral endings
     (contract expired).
   - Keeps hire and termination dates separate from the core
     employees table for clarity and history tracking.
   ============================================================ */

CREATE TABLE employee_status (
  status_id INT AUTO_INCREMENT PRIMARY KEY,
  empid INT NOT NULL,                          -- FK to employees
  status ENUM(
    'ACTIVE',          -- currently working
    'ON_LEAVE',        -- temporary leave
    'PAID_LEAVE',      -- leave with pay
    'RESIGNED',        -- voluntary termination
    'FIRED',           -- involuntary termination for cause
    'LAID_OFF',        -- involuntary termination not for cause
    'CONTRACT_ENDED',  -- neutral termination (end of contract/project)
    'TERMINATED'       -- generic termination if reason not specified
  ) NOT NULL,
  hire_date DATE NOT NULL,                     -- initial hire date
  termination_date DATE DEFAULT NULL,          -- final termination date if applicable
  effective_start DATE NOT NULL DEFAULT (CURRENT_DATE), -- when this status became effective
  effective_end DATE DEFAULT NULL,             -- when this status ended (NULL if current)
  FOREIGN KEY (empid) REFERENCES employees(empid) ON DELETE CASCADE
);

-- Index to quickly query status history by employee and start date
CREATE INDEX idx_employee_status ON employee_status (empid, status, effective_start);

CREATE TABLE divisions (
  divid INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,                  -- division name
  description VARCHAR(255) DEFAULT NULL,
  cityid INT NOT NULL,                         -- FK to cities
  address_line1 VARCHAR(100) NOT NULL,
  address_line2 VARCHAR(100) DEFAULT NULL,
  stateid INT DEFAULT NULL,                    -- FK to states
  countryid INT NOT NULL,                      -- FK to countries
  zip_code VARCHAR(15) NOT NULL,
  is_active TINYINT(1) NOT NULL DEFAULT 1,     -- flag to deactivate division
  FOREIGN KEY (cityid) REFERENCES cities(cityid),
  FOREIGN KEY (stateid) REFERENCES states(stateid),
  FOREIGN KEY (countryid) REFERENCES countries(countryid)
);

-- Job titles (roles employees can hold)
CREATE TABLE job_titles (
  job_title_id INT AUTO_INCREMENT PRIMARY KEY,
  job_title VARCHAR(125) NOT NULL UNIQUE,      -- e.g., 'Software Engineer'
  description VARCHAR(255) DEFAULT NULL
);

-- Employee assignment to divisions (effective-dated)
CREATE TABLE employee_division (
  empid INT NOT NULL,                          -- FK to employees
  divid INT NOT NULL,                          -- FK to divisions
  effective_start DATE NOT NULL,               -- when assignment began
  effective_end DATE DEFAULT NULL,             -- when assignment ended
  PRIMARY KEY (empid, divid, effective_start),
  FOREIGN KEY (empid) REFERENCES employees(empid) ON DELETE CASCADE,
  FOREIGN KEY (divid) REFERENCES divisions(divid) ON DELETE CASCADE
);

-- Employee job title history (effective-dated)
CREATE TABLE employee_job_title (
  empid INT NOT NULL,                          -- FK to employees
  job_title_id INT NOT NULL,                   -- FK to job_titles
  effective_start DATE NOT NULL,               -- when title began
  effective_end DATE DEFAULT NULL,             -- when title ended
  PRIMARY KEY (empid, job_title_id, effective_start),
  FOREIGN KEY (empid) REFERENCES employees(empid) ON DELETE CASCADE,
  FOREIGN KEY (job_title_id) REFERENCES job_titles(job_title_id) ON DELETE CASCADE
);

CREATE TABLE users (
  user_id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(64) NOT NULL UNIQUE,         -- login name
  password_hash VARBINARY(256) NOT NULL,        -- hashed password
  password_salt VARBINARY(64) NOT NULL,         -- per-user salt
  is_active TINYINT(1) NOT NULL DEFAULT 1,      -- active flag
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP
);

-- Roles (groups of permissions)
CREATE TABLE roles (
  role_id INT AUTO_INCREMENT PRIMARY KEY,
  role_name VARCHAR(64) NOT NULL UNIQUE,        -- e.g., 'HR_ADMIN','REGULAR_EMPLOYEE'
  description VARCHAR(255) DEFAULT NULL
);

-- Permissions (granular actions)
-- Note: granular 'permissions' table and 'role_permissions' mapping
-- were removed as part of simplification of the RBAC model.
-- If fine-grained permissions are required in future, re-introduce
-- these tables and update role/user assignment logic accordingly.

-- Mapping of users to roles
CREATE TABLE user_roles (
  user_id INT NOT NULL,
  role_id INT NOT NULL,
  PRIMARY KEY (user_id, role_id),
  FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
  FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE
);

-- Link between users and employees (accountability)
CREATE TABLE user_employee_link (
  user_id INT NOT NULL,
  empid INT NOT NULL,
  PRIMARY KEY (user_id, empid),
  FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
  FOREIGN KEY (empid) REFERENCES employees(empid) ON DELETE CASCADE
);

-- Authentication events (audit trail)
CREATE TABLE auth_events (
  auth_event_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NULL,                             -- FK to users
  event_type VARCHAR(32) NOT NULL,              -- e.g., 'LOGIN_SUCCESS','LOGIN_FAILURE'
  event_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  ip_address VARCHAR(45) DEFAULT NULL,          -- IPv4/IPv6
  user_agent VARCHAR(255) DEFAULT NULL,         -- browser/device info
  FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL
);

CREATE TABLE payroll_runs (
  payroll_run_id INT AUTO_INCREMENT PRIMARY KEY,
  run_year YEAR NOT NULL,                       -- year of payroll run
  run_month TINYINT NOT NULL,                   -- month of payroll run (1-12)
  run_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, -- when run was executed
  description VARCHAR(255) DEFAULT NULL         -- optional notes
);

-- Payroll entries per employee
CREATE TABLE payroll (
  payid INT AUTO_INCREMENT PRIMARY KEY,
  payroll_run_id INT NOT NULL,                  -- FK to payroll_runs
  empid INT NOT NULL,                           -- FK to employees
  pay_date DATE NOT NULL,                       -- actual pay date
  earnings DECIMAL(12,2) NOT NULL,              -- gross earnings
  fed_tax DECIMAL(12,2) NOT NULL DEFAULT 0,     -- federal tax withheld
  fed_med DECIMAL(12,2) NOT NULL DEFAULT 0,     -- Medicare withheld
  fed_ss DECIMAL(12,2) NOT NULL DEFAULT 0,      -- Social Security withheld
  state_tax DECIMAL(12,2) NOT NULL DEFAULT 0,   -- state tax withheld
  retire_401k DECIMAL(12,2) NOT NULL DEFAULT 0, -- retirement contribution
  health_care DECIMAL(12,2) NOT NULL DEFAULT 0, -- healthcare deduction
  net_pay DECIMAL(12,2) NOT NULL, --  net pay
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (payroll_run_id) REFERENCES payroll_runs(payroll_run_id) ON DELETE CASCADE,
  FOREIGN KEY (empid) REFERENCES employees(empid) ON DELETE CASCADE
);

-- Salary history (audit trail of changes)
CREATE TABLE salary_history (
  salary_history_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  empid INT NOT NULL,                           -- FK to employees
  previous_salary DECIMAL(12,2) NOT NULL,       -- salary before change
  new_salary DECIMAL(12,2) NOT NULL,            -- salary after change
  change_reason VARCHAR(255) DEFAULT NULL,      -- reason for change
  changed_by_user_id INT NOT NULL,              -- FK to users (who made change)
  changed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (empid) REFERENCES employees(empid) ON DELETE CASCADE,
  FOREIGN KEY (changed_by_user_id) REFERENCES users(user_id) ON DELETE RESTRICT
);

CREATE TABLE change_log (
  change_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  table_name VARCHAR(128) NOT NULL,             -- name of table changed
  record_pk VARCHAR(128) NOT NULL,              -- primary key of affected record
  operation ENUM('INSERT','UPDATE','DELETE') NOT NULL, -- type of change
  changed_by_user_id INT NOT NULL,              -- FK to users (who made change)
  changed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  old_values JSON DEFAULT NULL,                 -- snapshot before change
  new_values JSON DEFAULT NULL,                 -- snapshot after change
  FOREIGN KEY (changed_by_user_id) REFERENCES users(user_id) ON DELETE RESTRICT
);

-- Helpful index to query changes by table and time
CREATE INDEX idx_change_log_table_time ON change_log (table_name, changed_at);

