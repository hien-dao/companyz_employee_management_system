USE employeeData;

-- =========================
-- Core employee table
-- Stores basic employee identity and salary.
-- Hybrid SSN storage: last 4 digits (display), hash (search), encrypted (rare admin access).
-- =========================

CREATE TABLE employees (
  empid INT AUTO_INCREMENT PRIMARY KEY,
  employee_number VARCHAR(32) NOT NULL UNIQUE,      -- stable external ID
  fname VARCHAR(65) NOT NULL,
  lname VARCHAR(65) NOT NULL,
  employment_type_id INT NOT NULL,
  salary DECIMAL(12,2) NOT NULL,
  ssn_last4 CHAR(4) NOT NULL,                       -- last 4 digits for display
  ssn_hash CHAR(64) NOT NULL,                       -- irreversible hash for exact match
  ssn_enc VARBINARY(256) NOT NULL,                  -- encrypted SSN for rare admin use
  ssn_iv VARBINARY(16) NOT NULL,                    -- initialization vector for encryption
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (employment_type_id) REFERENCES employment_types(employment_type_id)
);

-- Indexes for fast search
CREATE INDEX idx_employees_name ON employees (lname, fname);
CREATE INDEX idx_employees_ssn_hash ON employees (ssn_hash);

