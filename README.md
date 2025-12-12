# companyz_employee_management_system
This is a employee management system for Company Z HR Department
## Purpose
The Employee Management System (EMS) for Company Z is designed to replace manual MySQL script management with a secure, and user-friendly application. The system is scalable, company Z can expand the number of employees, increase types of employees (Full time, Part Time, Contractor, etc) easier. HR can add status of employees (Active, On Leave, etc) if needed. More types of employee contacts in the future is possible (LinkedIn, etc). Upgrade to include other user roles (HR Admin, Regular Employee, Division Manager, Region Manager, etc) can be added easily.
## Capabilities
##### The system provides:
###### •	Login and authentication: System require username and password to login
###### •	HR Administrators: Full CRUD capabilities over employee data and payroll, salary adjustments, and reporting.
###### •	Employees: Secure access to view their personal information and pay history.
The intended audience includes HR administrators and employees.
## Description
##### The Employee Management System (EMS) has a JavaFX GUI and a MySQL database.
##### The system is separated into several main layers:
###### •	UI Layer: Interact with the user
###### •	Service Layer: Handle business logic
###### •	DAO Layer: Connect with the SQL server
###### •	Model Layder: Carry data and connect different layers

## How to use
##### src\main\java\com\companyz\ems\Main.java: Main file to run the program from
##### src\main\resources\db: Contain database schemas and seed data. You can run these script on your own computer to create the database "employeedata". Make sure to run the script in that order.
##### src\main\resources\application.properties: Config the program accordingly to your needs. You must change the db.username and db.password for the program to run
##### Credentials: There are 3 users created in the seed data
###### •	Username: admin; Password: StrongPASS123!
###### •	Username: jdoe; Password: StrongPASS123!
###### •	Username: asmith; Password: StrongPASS123!