USE employeeData;

/* ============================================================
   06_payroll_schema.sql
   Purpose:
   - Defines payroll structures for employees.
   - Separates payroll runs (monthly or periodic batches) from
     individual payroll entries.
   - Tracks salary changes in a dedicated history table for audit.
   - Supports reporting on pay statements, taxes, and deductions.
   ============================================================ */

-- Payroll runs (batch of payments for a given month/period)
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
  net_pay DECIMAL(12,2) AS (earnings - (fed_tax + fed_med + fed_ss + state_tax + retire_401k + health_care)) STORED, -- calculated net pay
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

