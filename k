Think critically about what data a Smart Clinic System must manage.
Create a markdown file named schema-design.md in your GitHub repository (java-database-capstone).
Define a MySQL database design section:
Include at least 4 tables (e.g., patients, doctors, appointments, and admin)
For each table, specify column names, data types, primary and foreign keys, and constraints (e.g., NOT NULL, UNIQUE)
Define a MongoDB collection design section:
Choose a suitable document collection (e.g., prescriptions, feedback, and logs)
Provide a realistic JSON example of a document with nested fields or arrays.
Justify your design decisions using inline comments if needed.
Commit and push your file to GitHub.



Admin model: Define an Admin model with fields such as id, username, and password. Apply annotations like @Entity, @Id, @NotNull, and @JsonProperty.
Appointment model: Define an Appointment model, linking Doctor and Patient entities. Implement helper methods like getEndTime().
Doctor model: Create a Doctor model with fields like name, specialty, and availableTimes. Add validation annotations.
Patient model: Define a Patient model with fields like name, email, and address.
Prescription model: Define a MongoDB Prescription model with fields like patientName, medication, and appointmentId.
Push to GitHub: Commit your changes and push them to your GitHub repository.


Assignment Overview: Adding Stored Procedures
Learning objectives
Determine the requirements for writing stored procedures to generate reports.
List the tasks and expected deliverables for completing the Adding Stored Procedures assignment.
Estimated time: 4 minutes

Introduction
In any data-driven application, reporting is a crucial task. A well-structured report allows stakeholders to make informed decisions. By using stored procedures in SQL, you can automate the process of generating reports, saving time, and ensuring consistency. In the Adding Stored Procedures lab, you'll write stored procedures to generate daily reports, as well as monthly and yearly summaries based on patient appointments. Additionally, you will add sample data to your tables to test these procedures effectively.

Tasks
Create stored procedures:

Write a stored procedure to generate a report for daily appointments, grouped by doctor .
Create a procedure to identify the doctor who saw the most patients in a specific month.
Develop a procedure to identify the doctor who saw the most patients in a given year.
Insert sample data:

Add data to the appointment, doctor, and patient tables to simulate real-world data and test your stored procedures.
Test the procedures:

Execute the stored procedures with different parameters to generate reports based on real data.
Deliverables
For evaluation in the final project, you will submit the output of the:

SQL statement that runs the GetDailyAppointmentReportByDoctor stored procedure
SQL statement that runs the GetDoctorWithMostPatientsByMonth stored procedure
SQL statement that runs the GetDoctorWithMostPatientsByYear stored procedure




mysql> CALL GetDailyAppointmentReportByDoctor('2025-04-15');
+------------------+----------------------------+--------+----------------+---------------+
| doctor_name      | appointment_time           | status | patient_name   | patient_phone |
+------------------+----------------------------+--------+----------------+---------------+
| Dr. Ava Hall     | 2025-04-15 11:00:00.000000 |      1 | Lucas Turner   | 889-666-6666  |
| Dr. Mark Johnson | 2025-04-15 12:00:00.000000 |      1 | Michael Jordan | 888-444-4444  |
| Dr. Mark Johnson | 2025-04-15 13:00:00.000000 |      1 | Olivia Moon    | 888-555-5555  |
+------------------+----------------------------+--------+----------------+---------------+
3 rows in set (0.004 sec)

Query OK, 0 rows affected (0.004 sec)




mysql> CALL GetDoctorWithMostPatientsByMonth(4, 2025);
+-----------+---------------+
| doctor_id | patients_seen |
+-----------+---------------+
|         2 |            31 |
+-----------+---------------+
1 row in set (0.005 sec)

Query OK, 0 rows affected (0.005 sec)

mysql> CALL GetDoctorWithMostPatientsByYear(2025);
+-----------+---------------+
| doctor_id | patients_seen |
+-----------+---------------+
|         1 |            34 |
+-----------+---------------+
1 row in set (0.001 sec)





Tasks
Task 1: Implement Admin Controls
Modify adminDashboard.js and adminDashboard.html to:

Display all doctors.
Add a search bar to filter doctors by name.
Add filters for filtering doctors by time and specialty.
Modal for adding a doctor.
Task 2: Implement Doctor Controls
Update doctorDashboard.js and doctorDashboard.html to:

Display all appointments for the logged-in doctor.
Include key details: patient name, appointment date/time, etc.
Allow viewing full patient's previous prescriptions.
Add a search bar to filter appointments by patient's name.
Add a filter to view doctor's appointments by date.
Task 3: Use Modular Components & Services
Reuse/update JS components like:

doctorCard.js to display doctor's to admin and patient.
header.js for role-specific navigation.
modal.js to open modals according to the roles and events.
Task 4: Style and Optimize
Ensure visual consistency by updating:

/assets/css/doctorDashboard.css
/assets/css/adminDashboard.css
/assets/css/style.css
Maintain responsive design, clean UI, and intuitive layout.

Task 5: Common Services and Utils
Index.js to handle role selection, login and localStorage for tokens
doctorService.js to abstract the doctor CRUD operations and search features
patientService.js to fetch and filter on patients
Reusable functions for admin and doctor dashboards
Deliverables
API-integrated JavaScript logic in adminDashboard.js and doctorDashboard.js.
Reusable UI components and updated service functions for lab and prescription workflows.
Updated CSS files reflecting role-based lab functionality.
Functional role-specific lab views with routing and modal integrations.
Completed frontend setup, ready for backend lab API integration.
Modular service files (index.js, doctorServices.js, patientServices.js) with all the APIs and error handling.


