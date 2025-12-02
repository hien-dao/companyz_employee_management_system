USE employeeData;

/* ============================================================
- Defines flexible employee contact storage using a reference
  table for contact types (so new types can be added later).
- Separates phone and email into their own columns if needed,
  but also allows other contact methods via employee_contact.
- Stores demographic details (gender, race, DOB) separately
  to keep the employees table clean.
   ============================================================ */

-- Reference table for contact types (extensible)
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

