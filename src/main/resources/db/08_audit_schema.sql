USE employeeData;

/* ============================================================
   08_audit_schema.sql
   Purpose:
   - Provides auditing and change tracking for critical tables.
   - Records inserts, updates, and deletes with before/after values.
   - Links changes to the user who performed them for accountability.
   - Complements salary_history and auth_events for a full audit trail.
   ============================================================ */

-- General change log (captures modifications across tables)
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

