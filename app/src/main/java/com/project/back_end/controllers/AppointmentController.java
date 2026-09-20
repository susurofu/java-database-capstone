package com.project.back_end.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final Service service;

    public AppointmentController(
            AppointmentService appointmentService,
            Service service
    ) {
        this.appointmentService = appointmentService;
        this.service = service;
    }

    /*
     * Get appointments for a doctor on a specific date,
     * optionally filtered by patient name.
     */
    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<Map<String, Object>> getAppointments(
            @PathVariable LocalDate date,
            @PathVariable String patientName,
            @PathVariable String token
    ) {

        ResponseEntity<Map<String, String>> tokenValidation =
                service.validateToken(token, "doctor");

        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {

            Map<String, Object> response = new HashMap<>();

            if (tokenValidation.getBody() != null) {
                response.putAll(tokenValidation.getBody());
            }

            return ResponseEntity
                    .status(tokenValidation.getStatusCode())
                    .body(response);
        }

        Map<String, Object> appointments =
                appointmentService.getAppointment(
                        patientName,
                        date,
                        token
                );

        return ResponseEntity.ok(appointments);
    }

    /*
     * Book a new appointment.
     */
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> bookAppointment(
            @PathVariable String token,
            @RequestBody Appointment appointment
    ) {

        Map<String, String> response = new HashMap<>();

        ResponseEntity<Map<String, String>> tokenValidation =
                service.validateToken(token, "patient");

        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            return tokenValidation;
        }

        int validation =
                service.validateAppointment(appointment);

        /*
         * Doctor does not exist.
         */
        if (validation == -1) {

            response.put(
                    "message",
                    "Doctor not found"
            );

            return ResponseEntity
                    .badRequest()
                    .body(response);
        }

        /*
         * Requested time is unavailable.
         */
        if (validation == 0) {

            response.put(
                    "message",
                    "Appointment time is not available"
            );

            return ResponseEntity
                    .badRequest()
                    .body(response);
        }

        /*
         * validation == 1, so try to save it.
         */
        int result =
                appointmentService.bookAppointment(appointment);

        if (result == 1) {

            response.put(
                    "message",
                    "Appointment booked successfully"
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);
        }

        response.put(
                "message",
                "Unable to book appointment"
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    /*
     * Update an existing appointment.
     */
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateAppointment(
            @PathVariable String token,
            @RequestBody Appointment appointment
    ) {

        ResponseEntity<Map<String, String>> tokenValidation =
                service.validateToken(token, "patient");

        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            return tokenValidation;
        }

        return appointmentService.updateAppointment(
                appointment
        );
    }

    /*
     * Cancel/delete an appointment.
     */
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> cancelAppointment(
            @PathVariable long id,
            @PathVariable String token
    ) {

        ResponseEntity<Map<String, String>> tokenValidation =
                service.validateToken(token, "patient");

        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            return tokenValidation;
        }

        return appointmentService.cancelAppointment(
                id,
                token
        );
    }
}