package com.project.back_end.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.Service;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;
    private final Service service;

    public PatientController(
            PatientService patientService,
            Service service
    ) {
        this.patientService = patientService;
        this.service = service;
    }

    /*
     * Get patient details using the authentication token.
     */
    @GetMapping("/{token}")
    public ResponseEntity<Map<String, Object>> getPatient(
            @PathVariable String token
    ) {

        ResponseEntity<Map<String, String>> tokenValidation =
                service.validateToken(token, "patient");

        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {

            Map<String, Object> response = new HashMap<>();

            if (tokenValidation.getBody() != null) {
                response.putAll(tokenValidation.getBody());
            }

            return ResponseEntity
                    .status(tokenValidation.getStatusCode())
                    .body(response);
        }

        return patientService.getPatientDetails(token);
    }

    /*
     * Register a new patient.
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> createPatient(
            @RequestBody Patient patient
    ) {

        Map<String, String> response = new HashMap<>();

        boolean validPatient =
                service.validatePatient(patient);

        if (!validPatient) {

            response.put(
                    "message",
                    "Patient with email id or phone no already exist"
            );

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(response);
        }

        int result =
                patientService.createPatient(patient);

        if (result == 1) {

            response.put(
                    "message",
                    "Signup successful"
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);
        }

        response.put(
                "message",
                "Internal server error"
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    /*
     * Patient login.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @RequestBody Login login
    ) {

        return service.validatePatientLogin(login);
    }

    /*
     * Get all appointments belonging to a patient.
     */
    @GetMapping("/{id}/{token}")
    public ResponseEntity<Map<String, Object>> getPatientAppointment(
            @PathVariable Long id,
            @PathVariable String token
    ) {

        ResponseEntity<Map<String, String>> tokenValidation =
                service.validateToken(token, "patient");

        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {

            Map<String, Object> response = new HashMap<>();

            if (tokenValidation.getBody() != null) {
                response.putAll(tokenValidation.getBody());
            }

            return ResponseEntity
                    .status(tokenValidation.getStatusCode())
                    .body(response);
        }

        return patientService.getPatientAppointment(
                id,
                token
        );
    }

    /*
     * Filter patient's appointments.
     */
    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<Map<String, Object>> filterPatientAppointment(
            @PathVariable String condition,
            @PathVariable String name,
            @PathVariable String token
    ) {

        ResponseEntity<Map<String, String>> tokenValidation =
                service.validateToken(token, "patient");

        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {

            Map<String, Object> response = new HashMap<>();

            if (tokenValidation.getBody() != null) {
                response.putAll(tokenValidation.getBody());
            }

            return ResponseEntity
                    .status(tokenValidation.getStatusCode())
                    .body(response);
        }

        return service.filterPatient(
                condition,
                name,
                token
        );
    }
}