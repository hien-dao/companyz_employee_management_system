USE employeeData;

-- Seed data for basic testing. Safe dummy values only.

-- Reference data
INSERT INTO employment_types (employment_type_id, employment_type_name, is_active)
VALUES
  (1, 'FULL_TIME', 1),
  (2, 'PART_TIME', 1),
  (3, 'CONTRACTOR', 1),
  (4, 'INTERN', 1)
ON DUPLICATE KEY UPDATE employment_type_name = VALUES(employment_type_name);

INSERT INTO countries (countryid, country_abbreviation, country_name)
VALUES (1, 'US', 'United States')
ON DUPLICATE KEY UPDATE country_name = VALUES(country_name);

INSERT INTO states (stateid, state_abbreviation, state_name)
VALUES (1, 'CA', 'California')
ON DUPLICATE KEY UPDATE state_name = VALUES(state_name);

INSERT INTO cities (cityid, city_name, stateid, countryid)
VALUES (1, 'San Francisco', 1, 1)
ON DUPLICATE KEY UPDATE city_name = VALUES(city_name);

INSERT INTO contact_types (contact_type_id, type_name)
VALUES (1, 'EMAIL'), (2, 'PHONE'), (3, 'LINKEDIN')
ON DUPLICATE KEY UPDATE type_name = VALUES(type_name);

-- Security / RBAC seed
INSERT INTO roles (role_id, role_name, description)
VALUES (1, 'HR_ADMIN', 'HR administrators'), (2, 'REGULAR_EMPLOYEE', 'Standard employee role')
ON DUPLICATE KEY UPDATE role_name = VALUES(role_name);

INSERT INTO permissions (permission_id, permission_name, description)
VALUES (1, 'UPDATE_EMPLOYEE_DATA', 'Modify employee records'),
       (2, 'SEE_PAY_STATEMENTS', 'View pay statements'),
       (3, 'UPDATE_EMPLOYEE_SALARY', 'Change employee salary')
ON DUPLICATE KEY UPDATE permission_name = VALUES(permission_name);

INSERT INTO role_permissions (role_id, permission_id)
VALUES (1,1),(1,2),(1,3),(2,2)
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);

-- Admin user (dummy password hex for 'password')
INSERT INTO users (user_id, username, email, password_hash, password_salt, is_active)
VALUES (1, 'admin', 'admin@example.com', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1)
ON DUPLICATE KEY UPDATE username = VALUES(username);

INSERT INTO user_roles (user_id, role_id)
VALUES (1,1)
ON DUPLICATE KEY UPDATE user_id = VALUES(user_id);

-- Divisions and job titles
INSERT INTO divisions (divid, name, cityid, address_line1, address_line2, stateid, countryid, zip_code, is_active)
VALUES (1, 'Engineering', 1, '100 Market St', NULL, 1, 1, '94103', 1)
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO job_titles (job_title_id, job_title)
VALUES (1, 'Software Engineer')
ON DUPLICATE KEY UPDATE job_title = VALUES(job_title);

-- One employee (uses employment_type_id = 1)
INSERT INTO employees (empid, employee_number, fname, lname, employment_type_id, salary, ssn_last4, ssn_hash, ssn_enc, ssn_iv, created_at, updated_at)
VALUES (
  1,
  'EMP0001',
  'Alice',
  'Smith',
  1,
  5000.00,
  '1234',
  REPEAT('a',64),
  UNHEX('01'),
  UNHEX('00'),
  '2025-01-01 00:00:00',
  NULL
)
ON DUPLICATE KEY UPDATE employee_number = VALUES(employee_number);

-- Link user to employee for accountability
INSERT INTO user_employee_link (user_id, empid)
VALUES (1,1)
ON DUPLICATE KEY UPDATE user_id = VALUES(user_id);

-- Employee contacts
INSERT INTO employee_contact (contact_id, empid, contact_type_id, contact_value, is_primary)
VALUES (1, 1, 1, 'alice.smith@example.com', 1),
       (2, 1, 2, '+1-415-555-0100', 0)
ON DUPLICATE KEY UPDATE contact_value = VALUES(contact_value);

-- Employee demographic
INSERT INTO employee_demographic (empid, gender, race, dob)
VALUES (1, 'Female', 'Not Specified', '1990-01-01')
ON DUPLICATE KEY UPDATE dob = VALUES(dob);

-- Org assignments and job title history
INSERT INTO employee_division (empid, divid, effective_start, effective_end)
VALUES (1, 1, '2020-01-01', NULL)
ON DUPLICATE KEY UPDATE effective_start = VALUES(effective_start);

INSERT INTO employee_job_title (empid, job_title_id, effective_start, effective_end)
VALUES (1, 1, '2020-01-01', NULL)
ON DUPLICATE KEY UPDATE effective_start = VALUES(effective_start);

-- Payroll run and entry
INSERT INTO payroll_runs (payroll_run_id, run_year, run_month, run_date, description)
VALUES (1, 2025, 11, '2025-11-01 00:00:00', 'November 2025 payroll')
ON DUPLICATE KEY UPDATE run_date = VALUES(run_date);

INSERT INTO payroll (payid, payroll_run_id, empid, pay_date, earnings, fed_tax, fed_med, fed_ss, state_tax, retire_401k, health_care, created_at)
VALUES (1, 1, 1, '2025-11-15', 5000.00, 500.00, 72.50, 310.00, 200.00, 250.00, 100.00, '2025-11-15 00:00:00')
ON DUPLICATE KEY UPDATE earnings = VALUES(earnings);

-- Salary history
INSERT INTO salary_history (salary_history_id, empid, previous_salary, new_salary, change_reason, changed_by_user_id, changed_at)
VALUES (1, 1, 4800.00, 5000.00, 'Annual raise', 1, '2025-01-01 00:00:00')
ON DUPLICATE KEY UPDATE new_salary = VALUES(new_salary);

-- Auth event and audit log examples
INSERT INTO auth_events (auth_event_id, user_id, event_type, event_time, ip_address, user_agent)
VALUES (1, 1, 'LOGIN_SUCCESS', '2025-11-24 12:00:00', '127.0.0.1', 'seed-script')
ON DUPLICATE KEY UPDATE event_type = VALUES(event_type);

INSERT INTO change_log (change_id, table_name, record_pk, operation, changed_by_user_id, changed_at, old_values, new_values)
VALUES (1, 'employees', '1', 'UPDATE', 1, '2025-01-01 00:00:00', '{"salary":4800.00}', '{"salary":5000.00}')
ON DUPLICATE KEY UPDATE changed_at = VALUES(changed_at);

-- End of seed

SELECT 'Seed data loaded.' AS message;
