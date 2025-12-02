USE employeeData;

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

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- REFERENCE DATA
-- ============================================================

-- Employment types
INSERT INTO employment_types (employment_type_id, employment_type_name, is_active)
VALUES
  (1, 'FULL_TIME', 1),
  (2, 'PART_TIME', 1),
  (3, 'CONTRACTOR', 1),
  (4, 'INTERN', 1)
ON DUPLICATE KEY UPDATE employment_type_name = VALUES(employment_type_name);

-- Countries (US, Canada, Mexico)
INSERT INTO countries (countryid, country_abbreviation, country_name)
VALUES
  (1, 'US', 'United States'),
  (2, 'CA', 'Canada'),
  (3, 'MX', 'Mexico')
ON DUPLICATE KEY UPDATE country_name = VALUES(country_name);

-- States (all 50 US states)
INSERT INTO states (stateid, state_abbreviation, state_name)
VALUES
  (1, 'AL', 'Alabama'), (2, 'AK', 'Alaska'), (3, 'AZ', 'Arizona'), (4, 'AR', 'Arkansas'),
  (5, 'CA', 'California'), (6, 'CO', 'Colorado'), (7, 'CT', 'Connecticut'), (8, 'DE', 'Delaware'),
  (9, 'FL', 'Florida'), (10, 'GA', 'Georgia'), (11, 'HI', 'Hawaii'), (12, 'ID', 'Idaho'),
  (13, 'IL', 'Illinois'), (14, 'IN', 'Indiana'), (15, 'IA', 'Iowa'), (16, 'KS', 'Kansas'),
  (17, 'KY', 'Kentucky'), (18, 'LA', 'Louisiana'), (19, 'ME', 'Maine'), (20, 'MD', 'Maryland'),
  (21, 'MA', 'Massachusetts'), (22, 'MI', 'Michigan'), (23, 'MN', 'Minnesota'), (24, 'MS', 'Mississippi'),
  (25, 'MO', 'Missouri'), (26, 'MT', 'Montana'), (27, 'NE', 'Nebraska'), (28, 'NV', 'Nevada'),
  (29, 'NH', 'New Hampshire'), (30, 'NJ', 'New Jersey'), (31, 'NM', 'New Mexico'), (32, 'NY', 'New York'),
  (33, 'NC', 'North Carolina'), (34, 'ND', 'North Dakota'), (35, 'OH', 'Ohio'), (36, 'OK', 'Oklahoma'),
  (37, 'OR', 'Oregon'), (38, 'PA', 'Pennsylvania'), (39, 'RI', 'Rhode Island'), (40, 'SC', 'South Carolina'),
  (41, 'SD', 'South Dakota'), (42, 'TN', 'Tennessee'), (43, 'TX', 'Texas'), (44, 'UT', 'Utah'),
  (45, 'VT', 'Vermont'), (46, 'VA', 'Virginia'), (47, 'WA', 'Washington'), (48, 'WV', 'West Virginia'),
  (49, 'WI', 'Wisconsin'), (50, 'WY', 'Wyoming')
ON DUPLICATE KEY UPDATE state_name = VALUES(state_name);

-- Major US cities (one per region for demo)
INSERT INTO cities (cityid, city_name, stateid, countryid)
VALUES
  (1, 'San Francisco', 5, 1),      -- CA
  (2, 'New York', 32, 1),          -- NY
  (3, 'Chicago', 13, 1),           -- IL
  (4, 'Los Angeles', 5, 1),        -- CA
  (5, 'Houston', 43, 1),           -- TX
  (6, 'Phoenix', 3, 1),            -- AZ
  (7, 'Denver', 6, 1),             -- CO
  (8, 'Seattle', 47, 1),           -- WA
  (9, 'Boston', 21, 1),            -- MA
  (10, 'Atlanta', 10, 1),          -- GA
  (11, 'Toronto', NULL, 2),        -- Canada
  (12, 'Vancouver', NULL, 2),      -- Canada
  (13, 'Mexico City', NULL, 3)     -- Mexico
ON DUPLICATE KEY UPDATE city_name = VALUES(city_name);

-- Contact types
INSERT INTO contact_types (contact_type_id, type_name)
VALUES (1, 'EMAIL'), (2, 'PHONE'), (3, 'LINKEDIN')
ON DUPLICATE KEY UPDATE type_name = VALUES(type_name);

-- ============================================================
-- SECURITY / RBAC SEED
-- ============================================================

-- Roles
INSERT INTO roles (role_id, role_name, description)
VALUES
  (1, 'HR_ADMIN', 'HR administrators'),
  (2, 'REGULAR_EMPLOYEE', 'Standard employee role')
ON DUPLICATE KEY UPDATE role_name = VALUES(role_name);

-- Admin user (username: admin, default password and salt)
-- Default password hash and salt (replace with real hashed values in production)
INSERT INTO users (user_id, username, password_hash, password_salt, is_active)
VALUES (1, 'admin', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1)
ON DUPLICATE KEY UPDATE username = VALUES(username);

INSERT INTO user_roles (user_id, role_id)
VALUES (1, 1)
ON DUPLICATE KEY UPDATE user_id = VALUES(user_id);

-- ============================================================
-- ORGANIZATIONAL STRUCTURE
-- ============================================================

-- Divisions
INSERT INTO divisions (divid, name, description, cityid, address_line1, address_line2, stateid, countryid, zip_code, is_active)
VALUES
  (1, 'Engineering', 'Engineering division responsible for product development', 1, '100 Market St', NULL, 5, 1, '94103', 1),
  (2, 'Sales', 'Sales and business development team', 2, '200 Park Ave', 'Suite 1500', 32, 1, '10166', 1),
  (3, 'Human Resources', 'HR and talent management', 3, '300 E Randolph Dr', NULL, 13, 1, '60601', 1),
  (4, 'Finance', 'Finance and accounting operations', 4, '400 S Hope St', NULL, 5, 1, '90071', 1),
  (5, 'Operations', 'Operations and facilities management', 5, '500 Main St', NULL, 43, 1, '77002', 1)
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- Job titles
INSERT INTO job_titles (job_title_id, job_title, description)
VALUES
  (1, 'Software Engineer', 'Designs, develops, and maintains software systems'),
  (2, 'Senior Software Engineer', 'Senior-level software development and mentoring'),
  (3, 'Product Manager', 'Product strategy and management'),
  (4, 'Sales Representative', 'Sales and account management'),
  (5, 'HR Specialist', 'HR operations and recruitment'),
  (6, 'Finance Analyst', 'Financial analysis and reporting'),
  (7, 'Operations Manager', 'Operations oversight and logistics'),
  (8, 'Data Scientist', 'Data analysis and machine learning'),
  (9, 'DevOps Engineer', 'Infrastructure and deployment automation'),
  (10, 'Quality Assurance', 'Software testing and quality assurance')
ON DUPLICATE KEY UPDATE job_title = VALUES(job_title);

-- ============================================================
-- EMPLOYEES (50 full-time employees)
-- ============================================================

INSERT INTO employees (empid, fname, lname, salary, ssn_last4, ssn_hash, ssn_enc, ssn_iv, created_at, updated_at)
VALUES
  (1, 'Alice', 'Anderson', 95000.00, '1001', REPEAT('a',64), UNHEX('01'), UNHEX('00'), '2020-01-01 00:00:00', NULL),
  (2, 'Bob', 'Brown', 88000.00, '1002', REPEAT('b',64), UNHEX('01'), UNHEX('00'), '2020-02-01 00:00:00', NULL),
  (3, 'Carol', 'Chen', 92000.00, '1003', REPEAT('c',64), UNHEX('01'), UNHEX('00'), '2020-03-01 00:00:00', NULL),
  (4, 'David', 'Davis', 85000.00, '1004', REPEAT('d',64), UNHEX('01'), UNHEX('00'), '2020-04-01 00:00:00', NULL),
  (5, 'Emma', 'Evans', 90000.00, '1005', REPEAT('e',64), UNHEX('01'), UNHEX('00'), '2020-05-01 00:00:00', NULL),
  (6, 'Frank', 'Foster', 87000.00, '1006', REPEAT('f',64), UNHEX('01'), UNHEX('00'), '2020-06-01 00:00:00', NULL),
  (7, 'Grace', 'Garcia', 93000.00, '1007', REPEAT('7',64), UNHEX('01'), UNHEX('00'), '2020-07-01 00:00:00', NULL),
  (8, 'Henry', 'Harris', 86000.00, '1008', REPEAT('8',64), UNHEX('01'), UNHEX('00'), '2020-08-01 00:00:00', NULL),
  (9, 'Iris', 'Ivanova', 91000.00, '1009', REPEAT('9',64), UNHEX('01'), UNHEX('00'), '2020-09-01 00:00:00', NULL),
  (10, 'James', 'Jones', 89000.00, '1010', REPEAT('a',64), UNHEX('01'), UNHEX('00'), '2020-10-01 00:00:00', NULL),
  (11, 'Karen', 'King', 84000.00, '1011', REPEAT('b',64), UNHEX('01'), UNHEX('00'), '2020-11-01 00:00:00', NULL),
  (12, 'Leo', 'Lee', 88000.00, '1012', REPEAT('c',64), UNHEX('01'), UNHEX('00'), '2020-12-01 00:00:00', NULL),
  (13, 'Mia', 'Martinez', 86000.00, '1013', REPEAT('d',64), UNHEX('01'), UNHEX('00'), '2021-01-01 00:00:00', NULL),
  (14, 'Nathan', 'Nelson', 92000.00, '1014', REPEAT('e',64), UNHEX('01'), UNHEX('00'), '2021-02-01 00:00:00', NULL),
  (15, 'Olivia', 'O''Connor', 94000.00, '1015', REPEAT('f',64), UNHEX('01'), UNHEX('00'), '2021-03-01 00:00:00', NULL),
  (16, 'Peter', 'Patel', 87000.00, '1016', REPEAT('a',64), UNHEX('01'), UNHEX('00'), '2021-04-01 00:00:00', NULL),
  (17, 'Quinn', 'Quinn', 89000.00, '1017', REPEAT('b',64), UNHEX('01'), UNHEX('00'), '2021-05-01 00:00:00', NULL),
  (18, 'Rachel', 'Robinson', 91000.00, '1018', REPEAT('c',64), UNHEX('01'), UNHEX('00'), '2021-06-01 00:00:00', NULL),
  (19, 'Samuel', 'Smith', 85000.00, '1019', REPEAT('d',64), UNHEX('01'), UNHEX('00'), '2021-07-01 00:00:00', NULL),
  (20, 'Tina', 'Taylor', 90000.00, '1020', REPEAT('e',64), UNHEX('01'), UNHEX('00'), '2021-08-01 00:00:00', NULL),
  (21, 'Ulrich', 'Underwood', 88000.00, '1021', REPEAT('f',64), UNHEX('01'), UNHEX('00'), '2021-09-01 00:00:00', NULL),
  (22, 'Victoria', 'Vaughn', 93000.00, '1022', REPEAT('a',64), UNHEX('01'), UNHEX('00'), '2021-10-01 00:00:00', NULL),
  (23, 'William', 'Wang', 86000.00, '1023', REPEAT('b',64), UNHEX('01'), UNHEX('00'), '2021-11-01 00:00:00', NULL),
  (24, 'Ximena', 'Xavier', 89000.00, '1024', REPEAT('c',64), UNHEX('01'), UNHEX('00'), '2021-12-01 00:00:00', NULL),
  (25, 'Yara', 'Young', 92000.00, '1025', REPEAT('d',64), UNHEX('01'), UNHEX('00'), '2022-01-01 00:00:00', NULL),
  (26, 'Zara', 'Zhao', 87000.00, '1026', REPEAT('e',64), UNHEX('01'), UNHEX('00'), '2022-02-01 00:00:00', NULL),
  (27, 'Adrian', 'Adams', 90000.00, '1027', REPEAT('f',64), UNHEX('01'), UNHEX('00'), '2022-03-01 00:00:00', NULL),
  (28, 'Bailey', 'Baker', 88000.00, '1028', REPEAT('a',64), UNHEX('01'), UNHEX('00'), '2022-04-01 00:00:00', NULL),
  (29, 'Cameron', 'Campbell', 91000.00, '1029', REPEAT('b',64), UNHEX('01'), UNHEX('00'), '2022-05-01 00:00:00', NULL),
  (30, 'Daisy', 'Davidson', 89000.00, '1030', REPEAT('c',64), UNHEX('01'), UNHEX('00'), '2022-06-01 00:00:00', NULL),
  (31, 'Ethan', 'Edwards', 85000.00, '1031', REPEAT('d',64), UNHEX('01'), UNHEX('00'), '2022-07-01 00:00:00', NULL),
  (32, 'Felicity', 'Fisher', 93000.00, '1032', REPEAT('e',64), UNHEX('01'), UNHEX('00'), '2022-08-01 00:00:00', NULL),
  (33, 'Gregory', 'Gordon', 86000.00, '1033', REPEAT('f',64), UNHEX('01'), UNHEX('00'), '2022-09-01 00:00:00', NULL),
  (34, 'Hannah', 'Hammond', 90000.00, '1034', REPEAT('a',64), UNHEX('01'), UNHEX('00'), '2022-10-01 00:00:00', NULL),
  (35, 'Isaac', 'Ingram', 88000.00, '1035', REPEAT('b',64), UNHEX('01'), UNHEX('00'), '2022-11-01 00:00:00', NULL),
  (36, 'Jessica', 'Jenkins', 92000.00, '1036', REPEAT('c',64), UNHEX('01'), UNHEX('00'), '2022-12-01 00:00:00', NULL),
  (37, 'Kevin', 'Knight', 89000.00, '1037', REPEAT('d',64), UNHEX('01'), UNHEX('00'), '2023-01-01 00:00:00', NULL),
  (38, 'Laura', 'Lewis', 91000.00, '1038', REPEAT('e',64), UNHEX('01'), UNHEX('00'), '2023-02-01 00:00:00', NULL),
  (39, 'Mason', 'Miller', 87000.00, '1039', REPEAT('f',64), UNHEX('01'), UNHEX('00'), '2023-03-01 00:00:00', NULL),
  (40, 'Natalie', 'Newman', 90000.00, '1040', REPEAT('a',64), UNHEX('01'), UNHEX('00'), '2023-04-01 00:00:00', NULL),
  (41, 'Owen', 'Owen', 88000.00, '1041', REPEAT('b',64), UNHEX('01'), UNHEX('00'), '2023-05-01 00:00:00', NULL),
  (42, 'Piper', 'Parker', 93000.00, '1042', REPEAT('c',64), UNHEX('01'), UNHEX('00'), '2023-06-01 00:00:00', NULL),
  (43, 'Quinton', 'Reeves', 86000.00, '1043', REPEAT('d',64), UNHEX('01'), UNHEX('00'), '2023-07-01 00:00:00', NULL),
  (44, 'Rosa', 'Roberts', 92000.00, '1044', REPEAT('e',64), UNHEX('01'), UNHEX('00'), '2023-08-01 00:00:00', NULL),
  (45, 'Samuel', 'Sanders', 89000.00, '1045', REPEAT('f',64), UNHEX('01'), UNHEX('00'), '2023-09-01 00:00:00', NULL),
  (46, 'Tanya', 'Thomas', 91000.00, '1046', REPEAT('a',64), UNHEX('01'), UNHEX('00'), '2023-10-01 00:00:00', NULL),
  (47, 'Urban', 'Usher', 87000.00, '1047', REPEAT('b',64), UNHEX('01'), UNHEX('00'), '2023-11-01 00:00:00', NULL),
  (48, 'Venus', 'Vickers', 90000.00, '1048', REPEAT('c',64), UNHEX('01'), UNHEX('00'), '2023-12-01 00:00:00', NULL),
  (49, 'Wallace', 'Wheeler', 88000.00, '1049', REPEAT('d',64), UNHEX('01'), UNHEX('00'), '2024-01-01 00:00:00', NULL),
  (50, 'Xenia', 'York', 92000.00, '1050', REPEAT('e',64), UNHEX('01'), UNHEX('00'), '2024-02-01 00:00:00', NULL)
ON DUPLICATE KEY UPDATE fname = VALUES(fname);

-- ============================================================
-- USER ACCOUNTS FOR 50 EMPLOYEES (plus admin)
-- ============================================================
-- Username format: first letter of first name + last name (lowercase)
-- Default password hash and salt for all (replace with real values in production)

INSERT INTO users (user_id, username, password_hash, password_salt, is_active)
VALUES
  (2, 'aanderson', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (3, 'bbrown', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (4, 'cchen', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (5, 'ddavis', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (6, 'eevans', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (7, 'ffoster', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (8, 'ggarcia', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (9, 'hharris', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (10, 'iivanova', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (11, 'jjones', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (12, 'kking', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (13, 'llee', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (14, 'mmartinez', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (15, 'nnelson', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (16, 'ooconnor', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (17, 'ppatel', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (18, 'qquinn', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (19, 'rrobinson', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (20, 'ssmith', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (21, 'ttaylor', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (22, 'uunderwood', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (23, 'vvaughn', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (24, 'wwang', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (25, 'xxavier', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (26, 'yyoung', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (27, 'zzhao', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (28, 'aadams', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (29, 'bbaker', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (30, 'ccampbell', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (31, 'ddavidson', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (32, 'eedwards', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (33, 'ffisher', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (34, 'ggordon', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (35, 'hhammond', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (36, 'iingram', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (37, 'jjenkins', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (38, 'kknight', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (39, 'llewis', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (40, 'mmiller', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (41, 'nnewman', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (42, 'oowen', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (43, 'pparker', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (44, 'qreeves', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (45, 'rroberts', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (46, 'ssanders', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (47, 'tthomas', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (48, 'uusher', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (49, 'vvickers', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (50, 'wwheeler', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1),
  (51, 'xyork', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1)
ON DUPLICATE KEY UPDATE username = VALUES(username);

-- User-employee links (employees 1-50 linked to users 2-51)
INSERT INTO user_employee_link (user_id, empid)
VALUES
  (2, 1), (3, 2), (4, 3), (5, 4), (6, 5), (7, 6), (8, 7), (9, 8), (10, 9), (11, 10),
  (12, 11), (13, 12), (14, 13), (15, 14), (16, 15), (17, 16), (18, 17), (19, 18), (20, 19), (21, 20),
  (22, 21), (23, 22), (24, 23), (25, 24), (26, 25), (27, 26), (28, 27), (29, 28), (30, 29), (31, 30),
  (32, 31), (33, 32), (34, 33), (35, 34), (36, 35), (37, 36), (38, 37), (39, 38), (40, 39), (41, 40),
  (42, 41), (43, 42), (44, 43), (45, 44), (46, 45), (47, 46), (48, 47), (49, 48), (50, 49), (51, 50)
ON DUPLICATE KEY UPDATE user_id = VALUES(user_id);

-- Assign all employees to REGULAR_EMPLOYEE role
INSERT INTO user_roles (user_id, role_id)
VALUES
  (2, 2), (3, 2), (4, 2), (5, 2), (6, 2), (7, 2), (8, 2), (9, 2), (10, 2),
  (11, 2), (12, 2), (13, 2), (14, 2), (15, 2), (16, 2), (17, 2), (18, 2), (19, 2), (20, 2),
  (21, 2), (22, 2), (23, 2), (24, 2), (25, 2), (26, 2), (27, 2), (28, 2), (29, 2), (30, 2),
  (31, 2), (32, 2), (33, 2), (34, 2), (35, 2), (36, 2), (37, 2), (38, 2), (39, 2), (40, 2),
  (41, 2), (42, 2), (43, 2), (44, 2), (45, 2), (46, 2), (47, 2), (48, 2), (49, 2), (50, 2), (51, 2)
ON DUPLICATE KEY UPDATE user_id = VALUES(user_id);

-- ============================================================
-- EMPLOYEE CONTACTS
-- ============================================================
-- Email: firstname.lastname@example.com, Phone: +1-xxx-555-[0001-0050]

INSERT INTO employee_contact (contact_id, empid, contact_type_id, contact_value, is_primary)
VALUES
  (1, 1, 1, 'alice.anderson@example.com', 1), (2, 1, 2, '+1-415-555-0001', 0),
  (3, 2, 1, 'bob.brown@example.com', 1), (4, 2, 2, '+1-415-555-0002', 0),
  (5, 3, 1, 'carol.chen@example.com', 1), (6, 3, 2, '+1-415-555-0003', 0),
  (7, 4, 1, 'david.davis@example.com', 1), (8, 4, 2, '+1-415-555-0004', 0),
  (9, 5, 1, 'emma.evans@example.com', 1), (10, 5, 2, '+1-415-555-0005', 0),
  (11, 6, 1, 'frank.foster@example.com', 1), (12, 6, 2, '+1-415-555-0006', 0),
  (13, 7, 1, 'grace.garcia@example.com', 1), (14, 7, 2, '+1-415-555-0007', 0),
  (15, 8, 1, 'henry.harris@example.com', 1), (16, 8, 2, '+1-415-555-0008', 0),
  (17, 9, 1, 'iris.ivanova@example.com', 1), (18, 9, 2, '+1-415-555-0009', 0),
  (19, 10, 1, 'james.jones@example.com', 1), (20, 10, 2, '+1-415-555-0010', 0),
  (21, 11, 1, 'karen.king@example.com', 1), (22, 11, 2, '+1-415-555-0011', 0),
  (23, 12, 1, 'leo.lee@example.com', 1), (24, 12, 2, '+1-415-555-0012', 0),
  (25, 13, 1, 'mia.martinez@example.com', 1), (26, 13, 2, '+1-415-555-0013', 0),
  (27, 14, 1, 'nathan.nelson@example.com', 1), (28, 14, 2, '+1-415-555-0014', 0),
  (29, 15, 1, 'olivia.oconnor@example.com', 1), (30, 15, 2, '+1-415-555-0015', 0),
  (31, 16, 1, 'peter.patel@example.com', 1), (32, 16, 2, '+1-415-555-0016', 0),
  (33, 17, 1, 'quinn.quinn@example.com', 1), (34, 17, 2, '+1-415-555-0017', 0),
  (35, 18, 1, 'rachel.robinson@example.com', 1), (36, 18, 2, '+1-415-555-0018', 0),
  (37, 19, 1, 'samuel.smith@example.com', 1), (38, 19, 2, '+1-415-555-0019', 0),
  (39, 20, 1, 'tina.taylor@example.com', 1), (40, 20, 2, '+1-415-555-0020', 0),
  (41, 21, 1, 'ulrich.underwood@example.com', 1), (42, 21, 2, '+1-415-555-0021', 0),
  (43, 22, 1, 'victoria.vaughn@example.com', 1), (44, 22, 2, '+1-415-555-0022', 0),
  (45, 23, 1, 'william.wang@example.com', 1), (46, 23, 2, '+1-415-555-0023', 0),
  (47, 24, 1, 'ximena.xavier@example.com', 1), (48, 24, 2, '+1-415-555-0024', 0),
  (49, 25, 1, 'yara.young@example.com', 1), (50, 25, 2, '+1-415-555-0025', 0),
  (51, 26, 1, 'zara.zhao@example.com', 1), (52, 26, 2, '+1-415-555-0026', 0),
  (53, 27, 1, 'adrian.adams@example.com', 1), (54, 27, 2, '+1-415-555-0027', 0),
  (55, 28, 1, 'bailey.baker@example.com', 1), (56, 28, 2, '+1-415-555-0028', 0),
  (57, 29, 1, 'cameron.campbell@example.com', 1), (58, 29, 2, '+1-415-555-0029', 0),
  (59, 30, 1, 'daisy.davidson@example.com', 1), (60, 30, 2, '+1-415-555-0030', 0),
  (61, 31, 1, 'ethan.edwards@example.com', 1), (62, 31, 2, '+1-415-555-0031', 0),
  (63, 32, 1, 'felicity.fisher@example.com', 1), (64, 32, 2, '+1-415-555-0032', 0),
  (65, 33, 1, 'gregory.gordon@example.com', 1), (66, 33, 2, '+1-415-555-0033', 0),
  (67, 34, 1, 'hannah.hammond@example.com', 1), (68, 34, 2, '+1-415-555-0034', 0),
  (69, 35, 1, 'isaac.ingram@example.com', 1), (70, 35, 2, '+1-415-555-0035', 0),
  (71, 36, 1, 'jessica.jenkins@example.com', 1), (72, 36, 2, '+1-415-555-0036', 0),
  (73, 37, 1, 'kevin.knight@example.com', 1), (74, 37, 2, '+1-415-555-0037', 0),
  (75, 38, 1, 'laura.lewis@example.com', 1), (76, 38, 2, '+1-415-555-0038', 0),
  (77, 39, 1, 'mason.miller@example.com', 1), (78, 39, 2, '+1-415-555-0039', 0),
  (79, 40, 1, 'natalie.newman@example.com', 1), (80, 40, 2, '+1-415-555-0040', 0),
  (81, 41, 1, 'owen.owen@example.com', 1), (82, 41, 2, '+1-415-555-0041', 0),
  (83, 42, 1, 'piper.parker@example.com', 1), (84, 42, 2, '+1-415-555-0042', 0),
  (85, 43, 1, 'quinton.reeves@example.com', 1), (86, 43, 2, '+1-415-555-0043', 0),
  (87, 44, 1, 'rosa.roberts@example.com', 1), (88, 44, 2, '+1-415-555-0044', 0),
  (89, 45, 1, 'samuel.sanders@example.com', 1), (90, 45, 2, '+1-415-555-0045', 0),
  (91, 46, 1, 'tanya.thomas@example.com', 1), (92, 46, 2, '+1-415-555-0046', 0),
  (93, 47, 1, 'urban.usher@example.com', 1), (94, 47, 2, '+1-415-555-0047', 0),
  (95, 48, 1, 'venus.vickers@example.com', 1), (96, 48, 2, '+1-415-555-0048', 0),
  (97, 49, 1, 'wallace.wheeler@example.com', 1), (98, 49, 2, '+1-415-555-0049', 0),
  (99, 50, 1, 'xenia.york@example.com', 1), (100, 50, 2, '+1-415-555-0050', 0)
ON DUPLICATE KEY UPDATE contact_value = VALUES(contact_value);

-- ============================================================
-- EMPLOYEE DEMOGRAPHICS (with new address fields)
-- ============================================================

INSERT INTO employee_demographic (empid, gender, race, dob, address_line1, address_line2, city_id, state_id, country_id)
VALUES
  (1, 'Female', 'Caucasian', '1985-03-15', '123 Main St', 'Apt 4A', 1, 5, 1),
  (2, 'Male', 'African American', '1988-07-22', '456 Oak Ave', NULL, 2, 32, 1),
  (3, 'Female', 'Asian', '1990-11-08', '789 Pine Rd', 'Suite 200', 3, 13, 1),
  (4, 'Male', 'Hispanic', '1987-05-10', '321 Elm St', NULL, 4, 5, 1),
  (5, 'Female', 'Caucasian', '1992-09-14', '654 Maple Ln', 'Apt 3B', 5, 43, 1),
  (6, 'Male', 'Asian', '1989-01-25', '987 Cedar Blvd', NULL, 6, 3, 1),
  (7, 'Female', 'Hispanic', '1986-04-19', '147 Birch Way', 'Suite 105', 7, 6, 1),
  (8, 'Male', 'Caucasian', '1991-06-30', '258 Spruce Ave', NULL, 8, 47, 1),
  (9, 'Female', 'African American', '1993-08-12', '369 Walnut Dr', 'Apt 2C', 9, 21, 1),
  (10, 'Male', 'Asian', '1987-10-05', '741 Chestnut St', NULL, 10, 10, 1),
  (11, 'Female', 'Caucasian', '1989-12-20', '852 Ash Rd', 'Suite 301', 1, 5, 1),
  (12, 'Male', 'Hispanic', '1994-02-11', '963 Poplar Ave', NULL, 2, 32, 1),
  (13, 'Female', 'Asian', '1988-03-16', '159 Willow Ln', 'Apt 1A', 3, 13, 1),
  (14, 'Male', 'Caucasian', '1991-05-28', '264 Sycamore St', NULL, 4, 5, 1),
  (15, 'Female', 'African American', '1985-07-09', '375 Hickory Way', 'Suite 400', 5, 43, 1),
  (16, 'Male', 'Asian', '1990-09-17', '486 Magnolia Dr', NULL, 6, 3, 1),
  (17, 'Female', 'Hispanic', '1992-11-04', '597 Laurel Ave', 'Apt 5D', 7, 6, 1),
  (18, 'Male', 'Caucasian', '1989-01-13', '608 Pecan Blvd', NULL, 8, 47, 1),
  (19, 'Female', 'Asian', '1987-03-26', '719 Olive St', 'Suite 200', 9, 21, 1),
  (20, 'Male', 'African American', '1993-06-18', '820 Palm Ave', NULL, 10, 10, 1),
  (21, 'Female', 'Caucasian', '1986-08-29', '931 Oak St', 'Apt 6E', 1, 5, 1),
  (22, 'Male', 'Hispanic', '1991-10-14', '042 Pine Way', NULL, 2, 32, 1),
  (23, 'Female', 'Asian', '1989-12-07', '153 Elm Ave', 'Suite 500', 3, 13, 1),
  (24, 'Male', 'Caucasian', '1994-02-19', '264 Maple Rd', NULL, 4, 5, 1),
  (25, 'Female', 'African American', '1987-04-22', '375 Cedar St', 'Apt 2B', 5, 43, 1),
  (26, 'Male', 'Asian', '1990-06-10', '486 Birch Ave', NULL, 6, 3, 1),
  (27, 'Female', 'Hispanic', '1992-08-05', '597 Spruce Ln', 'Suite 600', 7, 6, 1),
  (28, 'Male', 'Caucasian', '1988-10-16', '608 Walnut Way', NULL, 8, 47, 1),
  (29, 'Female', 'Asian', '1991-12-28', '719 Chestnut Dr', 'Apt 3C', 9, 21, 1),
  (30, 'Male', 'African American', '1986-01-11', '820 Ash Ave', NULL, 10, 10, 1),
  (31, 'Female', 'Caucasian', '1989-03-20', '931 Poplar St', 'Suite 300', 1, 5, 1),
  (32, 'Male', 'Hispanic', '1993-05-09', '042 Willow Ave', NULL, 2, 32, 1),
  (33, 'Female', 'Asian', '1987-07-17', '153 Sycamore Ln', 'Apt 7F', 3, 13, 1),
  (34, 'Male', 'Caucasian', '1990-09-24', '264 Hickory St', NULL, 4, 5, 1),
  (35, 'Female', 'African American', '1992-11-02', '375 Magnolia Way', 'Suite 700', 5, 43, 1),
  (36, 'Male', 'Asian', '1988-01-14', '486 Laurel Ave', NULL, 6, 3, 1),
  (37, 'Female', 'Hispanic', '1991-03-31', '597 Pecan Dr', 'Apt 4D', 7, 6, 1),
  (38, 'Male', 'Caucasian', '1989-06-15', '608 Olive Blvd', NULL, 8, 47, 1),
  (39, 'Female', 'Asian', '1994-08-07', '719 Palm St', 'Suite 150', 9, 21, 1),
  (40, 'Male', 'African American', '1986-10-19', '820 Oak Ave', NULL, 10, 10, 1),
  (41, 'Female', 'Caucasian', '1987-12-29', '931 Pine St', 'Apt 8G', 1, 5, 1),
  (42, 'Male', 'Hispanic', '1992-02-08', '042 Elm Ln', NULL, 2, 32, 1),
  (43, 'Female', 'Asian', '1990-04-16', '153 Maple Ave', 'Suite 800', 3, 13, 1),
  (44, 'Male', 'Caucasian', '1993-06-23', '264 Cedar Way', NULL, 4, 5, 1),
  (45, 'Female', 'African American', '1988-08-12', '375 Birch St', 'Apt 5E', 5, 43, 1),
  (46, 'Male', 'Asian', '1991-10-01', '486 Spruce Ave', NULL, 6, 3, 1),
  (47, 'Female', 'Hispanic', '1989-12-18', '597 Walnut Ln', 'Suite 250', 7, 6, 1),
  (48, 'Male', 'Caucasian', '1994-01-09', '608 Chestnut Way', NULL, 8, 47, 1),
  (49, 'Female', 'Asian', '1986-03-25', '719 Ash Dr', 'Apt 6F', 9, 21, 1),
  (50, 'Male', 'African American', '1992-05-14', '820 Poplar Ave', NULL, 10, 10, 1)
ON DUPLICATE KEY UPDATE dob = VALUES(dob);

-- ============================================================
-- ORGANIZATIONAL ASSIGNMENTS
-- ============================================================

-- Assign employees to divisions (round-robin across 5 divisions, 10 each)
INSERT INTO employee_division (empid, divid, effective_start, effective_end)
VALUES
  (1, 1, '2020-01-01', NULL), (2, 2, '2020-02-01', NULL), (3, 3, '2020-03-01', NULL), (4, 4, '2020-04-01', NULL), (5, 5, '2020-05-01', NULL),
  (6, 1, '2020-06-01', NULL), (7, 2, '2020-07-01', NULL), (8, 3, '2020-08-01', NULL), (9, 4, '2020-09-01', NULL), (10, 5, '2020-10-01', NULL),
  (11, 1, '2020-11-01', NULL), (12, 2, '2020-12-01', NULL), (13, 3, '2021-01-01', NULL), (14, 4, '2021-02-01', NULL), (15, 5, '2021-03-01', NULL),
  (16, 1, '2021-04-01', NULL), (17, 2, '2021-05-01', NULL), (18, 3, '2021-06-01', NULL), (19, 4, '2021-07-01', NULL), (20, 5, '2021-08-01', NULL),
  (21, 1, '2021-09-01', NULL), (22, 2, '2021-10-01', NULL), (23, 3, '2021-11-01', NULL), (24, 4, '2021-12-01', NULL), (25, 5, '2022-01-01', NULL),
  (26, 1, '2022-02-01', NULL), (27, 2, '2022-03-01', NULL), (28, 3, '2022-04-01', NULL), (29, 4, '2022-05-01', NULL), (30, 5, '2022-06-01', NULL),
  (31, 1, '2022-07-01', NULL), (32, 2, '2022-08-01', NULL), (33, 3, '2022-09-01', NULL), (34, 4, '2022-10-01', NULL), (35, 5, '2022-11-01', NULL),
  (36, 1, '2022-12-01', NULL), (37, 2, '2023-01-01', NULL), (38, 3, '2023-02-01', NULL), (39, 4, '2023-03-01', NULL), (40, 5, '2023-04-01', NULL),
  (41, 1, '2023-05-01', NULL), (42, 2, '2023-06-01', NULL), (43, 3, '2023-07-01', NULL), (44, 4, '2023-08-01', NULL), (45, 5, '2023-09-01', NULL),
  (46, 1, '2023-10-01', NULL), (47, 2, '2023-11-01', NULL), (48, 3, '2023-12-01', NULL), (49, 4, '2024-01-01', NULL), (50, 5, '2024-02-01', NULL)
ON DUPLICATE KEY UPDATE effective_start = VALUES(effective_start);

-- Assign employees to job titles (round-robin across 10 titles, 5 each)
INSERT INTO employee_job_title (empid, job_title_id, effective_start, effective_end)
VALUES
  (1, 1, '2020-01-01', NULL), (2, 2, '2020-02-01', NULL), (3, 3, '2020-03-01', NULL), (4, 4, '2020-04-01', NULL), (5, 5, '2020-05-01', NULL),
  (6, 6, '2020-06-01', NULL), (7, 7, '2020-07-01', NULL), (8, 8, '2020-08-01', NULL), (9, 9, '2020-09-01', NULL), (10, 10, '2020-10-01', NULL),
  (11, 1, '2020-11-01', NULL), (12, 2, '2020-12-01', NULL), (13, 3, '2021-01-01', NULL), (14, 4, '2021-02-01', NULL), (15, 5, '2021-03-01', NULL),
  (16, 6, '2021-04-01', NULL), (17, 7, '2021-05-01', NULL), (18, 8, '2021-06-01', NULL), (19, 9, '2021-07-01', NULL), (20, 10, '2021-08-01', NULL),
  (21, 1, '2021-09-01', NULL), (22, 2, '2021-10-01', NULL), (23, 3, '2021-11-01', NULL), (24, 4, '2021-12-01', NULL), (25, 5, '2022-01-01', NULL),
  (26, 6, '2022-02-01', NULL), (27, 7, '2022-03-01', NULL), (28, 8, '2022-04-01', NULL), (29, 9, '2022-05-01', NULL), (30, 10, '2022-06-01', NULL),
  (31, 1, '2022-07-01', NULL), (32, 2, '2022-08-01', NULL), (33, 3, '2022-09-01', NULL), (34, 4, '2022-10-01', NULL), (35, 5, '2022-11-01', NULL),
  (36, 6, '2022-12-01', NULL), (37, 7, '2023-01-01', NULL), (38, 8, '2023-02-01', NULL), (39, 9, '2023-03-01', NULL), (40, 10, '2023-04-01', NULL),
  (41, 1, '2023-05-01', NULL), (42, 2, '2023-06-01', NULL), (43, 3, '2023-07-01', NULL), (44, 4, '2023-08-01', NULL), (45, 5, '2023-09-01', NULL),
  (46, 6, '2023-10-01', NULL), (47, 7, '2023-11-01', NULL), (48, 8, '2023-12-01', NULL), (49, 9, '2024-01-01', NULL), (50, 10, '2024-02-01', NULL)
ON DUPLICATE KEY UPDATE effective_start = VALUES(effective_start);

-- ============================================================
-- End of comprehensive seed data
-- ============================================================

SELECT 'Comprehensive seed data loaded successfully.' AS message;

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
-- Note: granular permissions and role_permissions mapping removed.
-- Role assignments are seeded below using `user_roles` only.

-- Admin user (dummy password hex for 'password')
INSERT INTO users (user_id, username, password_hash, password_salt, is_active)
VALUES (1, 'admin', UNHEX('70617373776F7264'), UNHEX('73616C74'), 1)
ON DUPLICATE KEY UPDATE username = VALUES(username);

INSERT INTO user_roles (user_id, role_id)
VALUES (1,1)
ON DUPLICATE KEY UPDATE user_id = VALUES(user_id);

-- Divisions and job titles
INSERT INTO divisions (divid, name, description, cityid, address_line1, address_line2, stateid, countryid, zip_code, is_active)
VALUES (1, 'Engineering', 'Engineering division responsible for product development', 1, '100 Market St', NULL, 1, 1, '94103', 1)
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO job_titles (job_title_id, job_title, description)
VALUES (1, 'Software Engineer', 'Designs, develops, and maintains software systems')
ON DUPLICATE KEY UPDATE job_title = VALUES(job_title);

-- (Removed duplicate single-employee insert that referenced employment_type_id)

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
