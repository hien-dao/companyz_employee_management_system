USE employeeData;

/* ============================================================
   05_org_structure_schema.sql
   Purpose:
   - Defines organizational structure tables including divisions
     and job titles.
   - Tracks employee assignments to divisions and job titles
     with effective start/end dates to support history.
   - Keeps organizational data separate from core employee data
     for clarity and scalability.
   ============================================================ */

-- Divisions (company units, linked to location)
CREATE TABLE divisions (
  divid INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,                  -- division name
  description VARCHAR(255) DEFAULT NULL,
  cityid INT NOT NULL,                         -- FK to cities
  address_line1 VARCHAR(100) NOT NULL,
  address_line2 VARCHAR(100) DEFAULT NULL,
  stateid INT DEFAULT NULL,                    -- FK to states
  countryid INT NOT NULL,                      -- FK to countries
  zip_code VARCHAR(15) NOT NULL,
  is_active TINYINT(1) NOT NULL DEFAULT 1,     -- flag to deactivate division
  FOREIGN KEY (cityid) REFERENCES cities(cityid),
  FOREIGN KEY (stateid) REFERENCES states(stateid),
  FOREIGN KEY (countryid) REFERENCES countries(countryid)
);

-- Job titles (roles employees can hold)
CREATE TABLE job_titles (
  job_title_id INT AUTO_INCREMENT PRIMARY KEY,
  job_title VARCHAR(125) NOT NULL UNIQUE,      -- e.g., 'Software Engineer'
  description VARCHAR(255) DEFAULT NULL
);

-- Employee assignment to divisions (effective-dated)
CREATE TABLE employee_division (
  empid INT NOT NULL,                          -- FK to employees
  divid INT NOT NULL,                          -- FK to divisions
  effective_start DATE NOT NULL,               -- when assignment began
  effective_end DATE DEFAULT NULL,             -- when assignment ended
  PRIMARY KEY (empid, divid, effective_start),
  FOREIGN KEY (empid) REFERENCES employees(empid) ON DELETE CASCADE,
  FOREIGN KEY (divid) REFERENCES divisions(divid) ON DELETE CASCADE
);

-- Employee job title history (effective-dated)
CREATE TABLE employee_job_title (
  empid INT NOT NULL,                          -- FK to employees
  job_title_id INT NOT NULL,                   -- FK to job_titles
  effective_start DATE NOT NULL,               -- when title began
  effective_end DATE DEFAULT NULL,             -- when title ended
  PRIMARY KEY (empid, job_title_id, effective_start),
  FOREIGN KEY (empid) REFERENCES employees(empid) ON DELETE CASCADE,
  FOREIGN KEY (job_title_id) REFERENCES job_titles(job_title_id) ON DELETE CASCADE
);

