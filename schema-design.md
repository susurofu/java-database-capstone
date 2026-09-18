
## MySQL Database Design


### Table: appointments
- id: INT, Primary Key, Auto Increment
- doctor_id: INT, Foreign Key → doctors(id)
- patient_id: INT, Foreign Key → patients(id)
- appointment_time: DATETIME, Not Null
- status: INT (0 = Scheduled, 1 = Completed, 2 = Cancelled)
- previous_appointsments: STR (DATETIME - doctor_id, ...)


### Table: doctors_slots

-id: INT, PRIMARY KEY, AUTO_INCREMENT
-doctor_id: INT NOT NULL, Foreign Key → doctors(id)
-slot_time: DATETIME, NOT NULL
-status: TINYINT, NOT NULL, DEFAULT 0 (0 free, 1 occupied)


### Table: doctors

- id: INT, PRIMARY KEY, AUTO_INCREMENT
- first_name: VARCHAR(100), NOT NULL
- last_name: VARCHAR(100), NOT NULL


### Table: patients
- id: INT, PRIMARY KEY, AUTO_INCREMENT
- first_name: VARCHAR(100), NOT NULL
- last_name: VARCHAR(100), NOT NULL

### Table: admin
- id: INT, PRIMARY KEY, AUTO_INCREMENT
- first_name: VARCHAR(100), NOT NULL
- last_name: VARCHAR(100), NOT NULL


### Collection: prescriptions
```json
{
  "_id": "ObjectId('64abc123456')",
  "patientName": "John Smith",
  "appointmentId": 51,
  "medication": "Paracetamol",
  "dosage": "500mg",
  "doctorNotes": "Take 1 tablet every 6 hours.",
  "refillCount": 2,
  "pharmacy": {
    "name": "Walgreens SF",
    "location": "Market Street"
  }
}