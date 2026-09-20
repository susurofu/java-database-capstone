package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final TokenService tokenService;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            TokenService tokenService
    ) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.tokenService = tokenService;
    }

    /*
     * Book a new appointment.
     * Returns 1 if successful, 0 if an error occurs.
     */
    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    /*
     * Update an existing appointment.
     */
    @Transactional
    public ResponseEntity<Map<String, String>> updateAppointment(
            Appointment appointment
    ) {

        Map<String, String> response = new HashMap<>();

        // Appointment ID must be present
        if (appointment.getId() == null) {
            response.put("message", "Appointment ID is required.");
            return ResponseEntity
                    .badRequest()
                    .body(response);
        }

        Optional<Appointment> existingAppointment =
                appointmentRepository.findById(appointment.getId());

        if (existingAppointment.isEmpty()) {
            response.put("message", "Appointment not found.");
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(response);
        }

        // Validate doctor
        if (appointment.getDoctor() == null
                || appointment.getDoctor().getId() == null) {

            response.put("message", "Invalid doctor.");
            return ResponseEntity
                    .badRequest()
                    .body(response);
        }

        Optional<Doctor> doctor =
                doctorRepository.findById(
                        appointment.getDoctor().getId()
                );

        if (doctor.isEmpty()) {
            response.put("message", "Doctor not found.");
            return ResponseEntity
                    .badRequest()
                    .body(response);
        }

        // Validate patient
        if (appointment.getPatient() == null
                || appointment.getPatient().getId() == null) {

            response.put("message", "Invalid patient.");
            return ResponseEntity
                    .badRequest()
                    .body(response);
        }

        Optional<Patient> patient =
                patientRepository.findById(
                        appointment.getPatient().getId()
                );

        if (patient.isEmpty()) {
            response.put("message", "Patient not found.");
            return ResponseEntity
                    .badRequest()
                    .body(response);
        }

        /*
         * Check whether the requested appointment time
         * is already occupied for this doctor.
         */
        if (appointment.getAppointmentTime() != null) {

            LocalDateTime start =
                    appointment.getAppointmentTime();

            LocalDateTime end =
                    appointment.getAppointmentTime().plusHours(1);

            List<Appointment> appointments =
                    appointmentRepository
                            .findByDoctorIdAndAppointmentTimeBetween(
                                    doctor.get().getId(),
                                    start,
                                    end
                            );

            boolean alreadyBooked = appointments.stream()
                    .anyMatch(a ->
                            !a.getId().equals(appointment.getId())
                    );

            if (alreadyBooked) {
                response.put(
                        "message",
                        "The selected appointment time is already booked."
                );

                return ResponseEntity
                        .badRequest()
                        .body(response);
            }
        }

        try {
            appointment.setDoctor(doctor.get());
            appointment.setPatient(patient.get());

            appointmentRepository.save(appointment);

            response.put(
                    "message",
                    "Appointment updated successfully."
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            response.put(
                    "message",
                    "Unable to update appointment."
            );

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    /*
     * Cancel an appointment.
     */
    @Transactional
    public ResponseEntity<Map<String, String>> cancelAppointment(
            long id,
            String token
    ) {

        Map<String, String> response = new HashMap<>();

        Optional<Appointment> appointmentOptional =
                appointmentRepository.findById(id);

        if (appointmentOptional.isEmpty()) {
            response.put("message", "Appointment not found.");

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(response);
        }

        Appointment appointment = appointmentOptional.get();

        /*
         * Extract the patient ID from the token.
         *
         * IMPORTANT:
         * Rename this line if your TokenService uses a
         * different method name.
         */
        String patientEmail = tokenService.extractIdentifier(token);
        Patient patient = patientRepository.findByEmail(patientEmail);
        Long patientId = patient != null ? patient.getId() : null;

        if (patientId == null) {
            response.put("message", "Invalid token.");

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }

        if (appointment.getPatient() == null
                || !appointment.getPatient().getId().equals(patientId)) {

            response.put(
                    "message",
                    "You are not authorized to cancel this appointment."
            );

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(response);
        }

        try {
            appointmentRepository.delete(appointment);

            response.put(
                    "message",
                    "Appointment cancelled successfully."
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            response.put(
                    "message",
                    "Unable to cancel appointment."
            );

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    /*
     * Get appointments for the logged-in doctor
     * on a particular date.
     *
     * pname can optionally filter by patient name.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getAppointment(
            String pname,
            LocalDate date,
            String token
    ) {

        Map<String, Object> response = new HashMap<>();

        /*
         * Extract doctor ID from token.
         *
         * Rename this call if your TokenService uses
         * another method name.
         */
        String doctorEmail = tokenService.extractIdentifier(token);
        Doctor doctor = doctorRepository.findByEmail(doctorEmail);
        Long doctorId = doctor != null ? doctor.getId() : null;

        if (doctorId == null) {
            response.put("message", "Invalid token.");
            response.put("appointments", List.of());

            return response;
        }

        LocalDateTime start =
                date.atStartOfDay();

        LocalDateTime end =
                date.plusDays(1).atStartOfDay();

        List<Appointment> appointments;

        if (pname != null && !pname.trim().isEmpty()
                && !pname.equalsIgnoreCase("null")) {

            appointments =
                    appointmentRepository
                            .findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                                    doctorId,
                                    pname.trim(),
                                    start,
                                    end
                            );

        } else {

            appointments =
                    appointmentRepository
                            .findByDoctorIdAndAppointmentTimeBetween(
                                    doctorId,
                                    start,
                                    end
                            );
        }

        response.put("appointments", appointments);

        return response;
    }

    /*
     * Change appointment status.
     */
    @Transactional
    public ResponseEntity<Map<String, String>> changeStatus(
            long id,
            int status
    ) {

        Map<String, String> response = new HashMap<>();

        Optional<Appointment> appointmentOptional =
                appointmentRepository.findById(id);

        if (appointmentOptional.isEmpty()) {
            response.put("message", "Appointment not found.");

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(response);
        }

        Appointment appointment = appointmentOptional.get();

        appointment.setStatus(status);
        appointmentRepository.save(appointment);

        response.put(
                "message",
                "Appointment status updated successfully."
        );

        return ResponseEntity.ok(response);
    }
}
