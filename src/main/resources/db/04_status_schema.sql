USE employeeData;

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
  effective_start DATE NOT NULL DEFAULT CURRENT_DATE, -- when this status became effective
  effective_end DATE DEFAULT NULL,             -- when this status ended (NULL if current)
  FOREIGN KEY (empid) REFERENCES employees(empid) ON DELETE CASCADE
);

-- Index to quickly query status history by employee and start date
CREATE INDEX idx_employee_status ON employee_status (empid, status, effective_start);

