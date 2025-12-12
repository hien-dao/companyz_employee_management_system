USE employeedata;

-- Clear existing data to allow re-running the seed safely.
-- Deletes are ordered to avoid foreign key constraint errors.
SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM user_roles;
DELETE FROM user_employee_link;
DELETE FROM employee_contact;
DELETE FROM employee_demographic;
DELETE FROM employee_division;
DELETE FROM employee_job_title;
DELETE FROM payroll;
DELETE FROM payroll_runs;
DELETE FROM salary_history;
DELETE FROM auth_events;
DELETE FROM change_log;
DELETE FROM employees;
DELETE FROM job_titles;
DELETE FROM divisions;
DELETE FROM users;
DELETE FROM roles;
DELETE FROM contact_types;
DELETE FROM cities;
DELETE FROM states;
DELETE FROM countries;
DELETE FROM employment_types;
DELETE FROM employee_status;
DELETE FROM employee_employment_types;

SET FOREIGN_KEY_CHECKS = 1;

-- =========================
-- ROLES
-- =========================
INSERT INTO employeedata.roles (role_id, role_name, description)
VALUES (1, 'HR_ADMIN', 'Human Resources Administrator'),
       (2, 'REGULAR_EMPLOYEE', 'Standard employee role');

-- =========================
-- USERS: default password is StrongPASS123!
-- =========================
INSERT INTO employeedata.users (user_id, username, password_hash, password_salt, is_active, created_at, updated_at)
VALUES 
(100, 'admin', UNHEX('24326124313224644A596F43733676386B73725078386B67397A4C767571484D4C2F7848527139683567383354493365575A346969366355476D3665'), UNHEX('223977A5B23FE0791B533D8448F1275E'), 1, NOW(), NOW()),   -- HR admin
(101, 'jdoe', UNHEX('2432612431322468797753304C524E525830724545666461504854674F764F5659386E7279504350706D33672F63356E6D6B702F4A446D756B313153'), UNHEX('3989B2A832A279C57B793AFE00AA8953'), 1, NOW(), NOW()),   -- Regular employee
(102, 'asmith', UNHEX('24326124313224486A696B327750704D372F2F3576505A432F7248452E443142456944424275586A794C576878784A79466736394874644467444879'), UNHEX('3DF0FF2CFF500C2A8B76C3546F3ED741'), 1, NOW(), NOW());   -- Regular employee

INSERT INTO employeedata.user_roles (user_id, role_id)
VALUES 
(100, 1),
(101, 2),
(102, 2);

-- =========================
-- COUNTRIES / STATES / CITIES
-- =========================
INSERT INTO employeedata.countries (countryid, country_abbreviation, country_name)
VALUES (1, 'USA', 'United States');

INSERT INTO employeedata.states (stateid, state_abbreviation, state_name)
VALUES (1, 'GA', 'Georgia');

INSERT INTO employeedata.cities (cityid, city_name, stateid, countryid)
VALUES (1, 'Atlanta', 1, 1);

-- =========================
-- EMPLOYEES
-- =========================
-- Example SSNs: John Doe = 123-45-6789, Anna Smith = 987-65-4321
-- Values below are SHA-256 hashes (32 bytes), AES ciphertext (hex), and IV (hex).
-- Replace with actual outputs from your SsnEncryptor.

INSERT INTO employeedata.employees 
(empid, fname, lname, salary, ssn_last4, ssn_hash, ssn_enc, ssn_iv, created_at, updated_at)
VALUES
(201, 'John', 'Doe',   60000.00, '6789',
  UNHEX('01a54629efb952287e554eb23ef69c52097a75aecc0e3a93ca0855ab6d7a31a0'), -- SHA-256 hash
  UNHEX('d3795a4eaa1bc085f93b7084f5baf176'),                                -- AES ciphertext
  UNHEX('051a105fc49fb3bc22c2f6d1acacf980'),                                -- IV
  NOW(), NOW()),

(202, 'Anna', 'Smith', 62000.00, '4321',
  UNHEX('529c541de0d562cc3cd965e67cbf73f59ad3763373490f886d83465d924a4a0a'), -- SHA-256 hash
  UNHEX('a4b5c6d7e8f90123456789abcdef012'),                                -- AES ciphertext
  UNHEX('9f8e7d6c5b4a39281726354433221100'),                                -- IV
  NOW(), NOW());


-- Link users to employees
INSERT INTO employeedata.user_employee_link (user_id, empid)
VALUES 
(101, 201),
(102, 202);

-- =========================
-- CONTACT TYPES
-- =========================
INSERT INTO employeedata.contact_types (contact_type_id, type_name)
VALUES (1, 'MOBILE'),
       (2, 'EMAIL');

-- Employee contacts (1 mobile + 1 email each, both primary)
INSERT INTO employeedata.employee_contact (contact_id, empid, contact_type_id, contact_value, is_primary)
VALUES
(1, 201, 1, '555-111-2222', 1),
(2, 201, 2, 'jdoe@example.com', 1),
(3, 202, 1, '555-333-4444', 1),
(4, 202, 2, 'asmith@example.com', 1);

-- =========================
-- DEMOGRAPHIC
-- =========================
INSERT INTO employeedata.employee_demographic 
(empid, gender, race, dob, address_line1, address_line2, city_id, state_id, country_id, zip_code)
VALUES
(201, 'Male', 'White', '1990-05-15', '123 Main St', '', 1, 1, 1, '30058'),
(202, 'Female', 'Asian', '1992-08-20', '456 Oak Ave', 'Apt 2', 1, 1, 1, '30059');

-- =========================
-- DIVISIONS
-- =========================
INSERT INTO employeedata.divisions (divid, name, description, cityid, address_line1, address_line2, stateid, countryid, zip_code, is_active)
VALUES (1, 'IT Division', 'Handles IT operations', 1, '789 Tech Blvd', '', 1, 1, '30301', 1);

INSERT INTO employeedata.employee_division (empid, divid, effective_start, effective_end)
VALUES
(201, 1, '2021-06-01', NULL),
(202, 1, '2022-03-01', NULL);

-- =========================
-- JOB TITLES
-- =========================
INSERT INTO employeedata.job_titles (job_title_id, job_title, description)
VALUES 
(1, 'Software Engineer', 'Develops software applications'),
(2, 'Data Analyst', 'Analyzes company data');

INSERT INTO employeedata.employee_job_title (empid, job_title_id, effective_start, effective_end)
VALUES
(201, 1, '2021-06-01', NULL),
(202, 2, '2022-03-01', NULL);

-- =========================
-- EMPLOYMENT TYPES
-- =========================
INSERT INTO employeedata.employment_types (employment_type_id, employment_type_name)
VALUES (1, 'FULL_TIME'),
       (2, 'PART_TIME'),
       (3, 'CONTRACTOR');

INSERT INTO employeedata.employee_employment_types (employment_type_id, empid, is_active)
VALUES
(1, 201, 1),
(1, 202, 1);

-- =========================
-- STATUS
-- =========================
INSERT INTO employeedata.employee_status (status_id, empid, status, hire_date, effective_start, effective_end)
VALUES
(1, 201, 'ACTIVE', '2021-06-01', '2021-06-01', NULL),
(2, 202, 'ACTIVE', '2022-03-01', '2022-03-01', NULL);

-- =========================
-- PAYROLL RUNS
-- =========================
INSERT INTO employeedata.payroll_runs (payroll_run_id, run_year, run_month, run_date, description)
VALUES (1, 2025, 12, NOW(), 'December Payroll');

-- Payroll entries
INSERT INTO employeedata.payroll (payid, payroll_run_id, empid, pay_date, earnings, fed_tax, fed_med, fed_ss, state_tax, retire_401k, health_care, net_pay, created_at)
VALUES
(1, 1, 201, '2025-12-01', 5000.00, 500.00, 75.00, 310.00, 200.00, 250.00, 300.00, 3365.00, NOW()),
(2, 1, 202, '2025-12-01', 5200.00, 520.00, 78.00, 322.00, 210.00, 260.00, 310.00, 3500.00, NOW());

-- =========================
-- SALARY HISTORY
-- =========================
INSERT INTO employeedata.salary_history (salary_history_id, empid, previous_salary, new_salary, change_reason, changed_by_user_id, changed_at)
VALUES
(1, 201, 55000.00, 60000.00, 'Annual raise', 100, NOW()),
(2, 202, 58000.00, 62000.00, 'Annual raise', 100, NOW());

-- =========================
-- CHANGE LOG (example)
-- =========================
INSERT INTO employeedata.change_log (change_id, table_name, record_pk, operation, changed_by_user_id, changed_at, old_values, new_values)
VALUES
(1, 'employees', '201', 'UPDATE', 100, NOW(), '{"salary":55000}', '{"salary":60000}'),
(2, 'employees', '202', 'UPDATE', 100, NOW(), '{"salary":58000}', '{"salary":62000}');

-- =========================
-- AUTH EVENTS (example login)
-- =========================
INSERT INTO employeedata.auth_events (auth_event_id, user_id, event_type, event_time, ip_address, user_agent)
VALUES
(1, 100, 'LOGIN', NOW(), NULL, 'JavaFX'),
(2, 101, 'LOGIN', NOW(), NULL, 'JavaFX'),
(3, 102, 'LOGIN', NOW(), NULL, 'JavaFX');
