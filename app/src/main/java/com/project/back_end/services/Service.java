package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@org.springframework.stereotype.Service
public class Service {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    public Service(
            TokenService tokenService,
            AdminRepository adminRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository,
            DoctorService doctorService,
            PatientService patientService
    ) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    /*
     * Validate a token for the given user role.
     */
    public ResponseEntity<Map<String, String>> validateToken(
            String token,
            String user
    ) {

        Map<String, String> response = new HashMap<>();

        try {

            boolean valid = tokenService.validateToken(token, user);

            if (!valid) {
                response.put(
                        "message",
                        "Invalid or expired token"
                );

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(response);
            }

            /*
             * Empty map means validation succeeded.
             * This is useful if controllers check:
             *
             * if (service.validateToken(...).getBody().isEmpty())
             */
            return ResponseEntity.ok(response);

        } catch (Exception e) {

            response.put(
                    "message",
                    "Invalid or expired token"
            );

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }
    }

    /*
     * Validate admin login.
     */
    public ResponseEntity<Map<String, String>> validateAdmin(
            Admin receivedAdmin
    ) {

        Map<String, String> response = new HashMap<>();

        try {

            Admin admin =
                    adminRepository.findByUsername(
                            receivedAdmin.getUserName()
                    );

            if (admin == null) {
                response.put(
                        "message",
                        "Invalid username or password"
                );

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(response);
            }

            if (!admin.getPassword().equals(
                    receivedAdmin.getPassword()
            )) {

                response.put(
                        "message",
                        "Invalid username or password"
                );

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(response);
            }

            String token = tokenService.generateToken(
                    admin.getUserName()
            );

            response.put("token", token);

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            response.put(
                    "message",
                    "Internal server error"
            );

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    /*
     * Filter doctors according to any combination of:
     *
     * name
     * specialty
     * AM/PM time
     */
    public Map<String, Object> filterDoctor(
            String name,
            String specialty,
            String time
    ) {

        boolean hasName =
                name != null && !name.trim().isEmpty();

        boolean hasSpecialty =
                specialty != null && !specialty.trim().isEmpty();

        boolean hasTime =
                time != null && !time.trim().isEmpty();

        /*
         * name + specialty + time
         */
        if (hasName && hasSpecialty && hasTime) {

            return doctorService
                    .filterDoctorsByNameSpecilityandTime(
                            name,
                            specialty,
                            time
                    );
        }

        /*
         * name + time
         */
        if (hasName && hasTime) {

            return doctorService
                    .filterDoctorByNameAndTime(
                            name,
                            time
                    );
        }

        /*
         * name + specialty
         */
        if (hasName && hasSpecialty) {

            return doctorService
                    .filterDoctorByNameAndSpecility(
                            name,
                            specialty
                    );
        }

        /*
         * specialty + time
         */
        if (hasSpecialty && hasTime) {

            return doctorService
                    .filterDoctorByTimeAndSpecility(
                            specialty,
                            time
                    );
        }

        /*
         * name only
         */
        if (hasName) {
            return doctorService.findDoctorByName(name);
        }

        /*
         * specialty only
         */
        if (hasSpecialty) {

            return doctorService
                    .filterDoctorBySpecility(
                            specialty
                    );
        }

        /*
         * time only
         */
        if (hasTime) {

            return doctorService
                    .filterDoctorsByTime(time);
        }

        /*
         * No filters -> return every doctor.
         */
        Map<String, Object> response = new HashMap<>();

        response.put(
                "doctors",
                doctorService.getDoctors()
        );

        return response;
    }

    /*
     * Validate whether the requested appointment
     * time is available for the selected doctor.
     *
     *  1 = valid
     *  0 = unavailable
     * -1 = doctor does not exist
     */
    public int validateAppointment(Appointment appointment) {

        if (appointment == null
                || appointment.getDoctor() == null
                || appointment.getDoctor().getId() == null
                || appointment.getAppointmentTime() == null) {

            return 0;
        }

        Long doctorId =
                appointment.getDoctor().getId();

        Optional<Doctor> doctor =
                doctorRepository.findById(doctorId);

        if (doctor.isEmpty()) {
            return -1;
        }

        LocalDate date =
                appointment
                        .getAppointmentTime()
                        .toLocalDate();

        LocalTime requestedTime =
                appointment
                        .getAppointmentTime()
                        .toLocalTime();

        List<String> availableSlots =
                doctorService.getDoctorAvailability(
                        doctorId,
                        date
                );

        /*
         * Compare requested LocalTime against the
         * doctor's available slot strings.
         */
        for (String slot : availableSlots) {

            try {

                LocalTime availableTime =
                        LocalTime.parse(slot);

                if (availableTime.equals(requestedTime)) {
                    return 1;
                }

            } catch (Exception ignored) {
            }
        }

        return 0;
    }

    /*
     * Check whether a patient with this email
     * or phone number already exists.
     *
     * true  = patient may be registered
     * false = duplicate patient exists
     */
    public boolean validatePatient(Patient patient) {

        try {

            Patient existingPatient =
                    patientRepository.findByEmailOrPhone(
                            patient.getEmail(),
                            patient.getPhone()
                    );

            return existingPatient == null;

        } catch (Exception e) {
            return false;
        }
    }

    /*
     * Validate patient login.
     */
    public ResponseEntity<Map<String, String>>
    validatePatientLogin(Login login) {

        Map<String, String> response = new HashMap<>();

        try {

            Patient patient =
                    patientRepository.findByEmail(
                            login.getIdentifier()
                    );

            if (patient == null) {

                response.put(
                        "message",
                        "Invalid email or password"
                );

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(response);
            }

            if (!patient.getPassword().equals(
                    login.getPassword()
            )) {

                response.put(
                        "message",
                        "Invalid email or password"
                );

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(response);
            }

            String token = tokenService.generateToken(
                    patient.getEmail()
            );

            response.put("token", token);

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            response.put(
                    "message",
                    "Internal server error"
            );

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    /*
     * Filter appointments belonging to the
     * patient represented by the token.
     */
    public ResponseEntity<Map<String, Object>> filterPatient(
            String condition,
            String name,
            String token
    ) {

        Map<String, Object> response = new HashMap<>();

        try {

            /*
             * Assignment states that the patient token
             * contains the email address.
             *
             * Change extractEmail() if your TokenService
             * uses a different method name.
             */
            String email =
                    tokenService.extractIdentifier(token);

            if (email == null) {

                response.put(
                        "message",
                        "Invalid token"
                );

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(response);
            }

            Patient patient =
                    patientRepository.findByEmail(email);

            if (patient == null) {

                response.put(
                        "message",
                        "Patient not found"
                );

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(response);
            }

            Long patientId = patient.getId();

            boolean hasCondition =
                    condition != null
                            && !condition.trim().isEmpty();

            boolean hasName =
                    name != null
                            && !name.trim().isEmpty();

            /*
             * condition + doctor name
             */
            if (hasCondition && hasName) {

                return patientService
                        .filterByDoctorAndCondition(
                                condition,
                                name,
                                patientId
                        );
            }

            /*
             * condition only
             */
            if (hasCondition) {

                return patientService
                        .filterByCondition(
                                condition,
                                patientId
                        );
            }

            /*
             * doctor name only
             */
            if (hasName) {

                return patientService
                        .filterByDoctor(
                                name,
                                patientId
                        );
            }

            /*
             * No filter -> return every appointment
             * belonging to the authenticated patient.
             */
            return patientService
                    .getPatientAppointment(
                            patientId,
                            token
                    );

        } catch (Exception e) {

            response.put(
                    "message",
                    "Unable to filter appointments"
            );

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }
}
