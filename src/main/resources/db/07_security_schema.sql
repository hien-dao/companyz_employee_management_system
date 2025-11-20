USE employeeData;

/* ============================================================
   07_security_schema.sql
   Purpose:
   - Defines user accounts and role-based access control (RBAC).
   - Supports permissions assigned to roles, and roles assigned
     to users.
   - Links users to employees for accountability.
   - Tracks authentication events for auditing and security.
   ============================================================ */

-- User accounts
CREATE TABLE users (
  user_id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(64) NOT NULL UNIQUE,         -- login name
  email VARCHAR(128) NOT NULL UNIQUE,           -- contact email
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
CREATE TABLE permissions (
  permission_id INT AUTO_INCREMENT PRIMARY KEY,
  permission_name VARCHAR(64) NOT NULL UNIQUE,  -- e.g., 'UPDATE_EMPLOYEE_DATA', 'UPDATE_EMPLOYEE_SALARY', 'SEE_PAY_STATEMENTS'
  description VARCHAR(255) DEFAULT NULL
);

-- Mapping of roles to permissions
CREATE TABLE role_permissions (
  role_id INT NOT NULL,
  permission_id INT NOT NULL,
  PRIMARY KEY (role_id, permission_id),
  FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE,
  FOREIGN KEY (permission_id) REFERENCES permissions(permission_id) ON DELETE CASCADE
);

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

