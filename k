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