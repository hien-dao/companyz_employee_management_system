USE employeeData;

-- =========================
-- Core employee table
-- Stores basic employee identity and salary.
-- Hybrid SSN storage: last 4 digits (display), hash (search), encrypted (rare admin access).
-- =========================

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

