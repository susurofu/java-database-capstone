This is an application for Smart Clinic operations which allows manages patient appointments and prescriptions, keeping these records. This Spring Boot application uses both MVC and REST controllers. Thymeleaf templates are used for the Admin and Doctor dashboards, while REST APIs serve all other modules. The application interacts with two databases—MySQL (for patient, doctor, appointment, and admin data) and MongoDB (for prescriptions). All controllers route requests through a common service layer, which in turn delegates to the appropriate repositories. MySQL uses JPA entities while MongoDB uses document models.

1. User accesses AdminDashboard or Appointment pages.
2. The action is routed to the appropriate Thymeleaf or REST controller.
3. The controller calls the service layer.
4. The service later modifies or reads from MySQL database for patient, doctor, appointment, and admin data and MongoDB for prescriptions.
5. On the completion of DB modification or getting the data, the service layer returns responses to Thymeleaf which generates json and HTML returning them to the Dashboards
6. RestAPI, using Appointment, PatientDashboard, and PatientRecord analogically sends responses to the service layer.