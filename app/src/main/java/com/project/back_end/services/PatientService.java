package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public PatientService(
            PatientRepository patientRepository,
            AppointmentRepository appointmentRepository,
            TokenService tokenService
    ) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    /*
     * Create a new patient.
     *
     * 1 = success
     * 0 = error
     */
    @Transactional
    public int createPatient(Patient patient) {

        try {
            patientRepository.save(patient);
            return 1;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /*
     * Get all appointments for a patient.
     * The patient's identity is verified using the token.
     */
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getPatientAppointment(
            Long id,
            String token
    ) {

        Map<String, Object> response = new HashMap<>();

        try {

            /*
             * The assignment says that the token contains
             * the patient's email.
             *
             * Change this method name if your TokenService
             * uses a different method.
             */
            String email = tokenService.extractIdentifier(token);

            if (email == null) {
                response.put("message", "Invalid token.");

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(response);
            }

            Patient patient =
                    patientRepository.findByEmail(email);

            if (patient == null) {
                response.put("message", "Patient not found.");

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(response);
            }

            /*
             * The patient can retrieve only their own
             * appointments.
             */
            if (!patient.getId().equals(id)) {
                response.put(
                        "message",
                        "Unauthorized access."
                );

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(response);
            }

            List<Appointment> appointments =
                    appointmentRepository.findByPatientId(id);

            List<AppointmentDTO> appointmentDTOs =
                    convertToDTOList(appointments);

            response.put(
                    "appointments",
                    appointmentDTOs
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            e.printStackTrace();

            response.put(
                    "message",
                    "Unable to retrieve appointments."
            );

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    /*
     * Filter appointments by condition:
     *
     * past   -> status = 1
     * future -> status = 0
     */
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> filterByCondition(
            String condition,
            Long id
    ) {

        Map<String, Object> response = new HashMap<>();

        try {

            int status;

            if ("past".equalsIgnoreCase(condition)) {
                status = 1;

            } else if ("future".equalsIgnoreCase(condition)) {
                status = 0;

            } else {
                response.put(
                        "message",
                        "Invalid condition. Use 'past' or 'future'."
                );

                return ResponseEntity
                        .badRequest()
                        .body(response);
            }

            List<Appointment> appointments =
                    appointmentRepository
                            .findByPatient_IdAndStatusOrderByAppointmentTimeAsc(
                                    id,
                                    status
                            );

            response.put(
                    "appointments",
                    convertToDTOList(appointments)
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            e.printStackTrace();

            response.put(
                    "message",
                    "Unable to filter appointments."
            );

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    /*
     * Filter patient's appointments by doctor name.
     */
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> filterByDoctor(
            String name,
            Long patientId
    ) {

        Map<String, Object> response = new HashMap<>();

        try {

            List<Appointment> appointments =
                    appointmentRepository
                            .filterByDoctorNameAndPatientId(
                                    name,
                                    patientId
                            );

            response.put(
                    "appointments",
                    convertToDTOList(appointments)
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            e.printStackTrace();

            response.put(
                    "message",
                    "Unable to filter appointments by doctor."
            );

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    /*
     * Filter patient's appointments by doctor name
     * and past/future condition.
     */
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(
            String condition,
            String name,
            long patientId
    ) {

        Map<String, Object> response = new HashMap<>();

        try {

            int status;

            if ("past".equalsIgnoreCase(condition)) {
                status = 1;

            } else if ("future".equalsIgnoreCase(condition)) {
                status = 0;

            } else {
                response.put(
                        "message",
                        "Invalid condition. Use 'past' or 'future'."
                );

                return ResponseEntity
                        .badRequest()
                        .body(response);
            }

            List<Appointment> appointments =
                    appointmentRepository
                            .filterByDoctorNameAndPatientIdAndStatus(
                                    name,
                                    patientId,
                                    status
                            );

            response.put(
                    "appointments",
                    convertToDTOList(appointments)
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            e.printStackTrace();

            response.put(
                    "message",
                    "Unable to filter appointments."
            );

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    /*
     * Get patient details using email extracted
     * from the authentication token.
     */
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getPatientDetails(
            String token
    ) {

        Map<String, Object> response = new HashMap<>();

        try {

            /*
             * Change extractEmail() if your TokenService
             * uses another method name.
             */
            String email = tokenService.extractIdentifier(token);

            if (email == null) {
                response.put("message", "Invalid token.");

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(response);
            }

            Patient patient =
                    patientRepository.findByEmail(email);

            if (patient == null) {
                response.put("message", "Patient not found.");

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(response);
            }

            response.put("patient", patient);

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            e.printStackTrace();

            response.put(
                    "message",
                    "Unable to retrieve patient details."
            );

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    /*
     * Convert a list of Appointment entities
     * into AppointmentDTO objects.
     */
    private List<AppointmentDTO> convertToDTOList(
            List<Appointment> appointments
    ) {

        if (appointments == null) {
            return new ArrayList<>();
        }

        return appointments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /*
     * Convert one Appointment entity to AppointmentDTO.
     */
    private AppointmentDTO convertToDTO(
            Appointment appointment
    ) {

        return new AppointmentDTO(
                appointment.getId(),

                appointment.getDoctor().getId(),
                appointment.getDoctor().getName(),

                appointment.getPatient().getId(),
                appointment.getPatient().getName(),
                appointment.getPatient().getEmail(),
                appointment.getPatient().getPhone(),
                appointment.getPatient().getAddress(),

                appointment.getAppointmentTime(),
                appointment.getStatus()
        );
    }
}
