package com.project.back_end.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
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

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;

@RestController
@RequestMapping("${api.path}doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final Service service;

    public DoctorController(
            DoctorService doctorService,
            Service service
    ) {
        this.doctorService = doctorService;
        this.service = service;
    }

    /*
     * Get doctor availability for a given date.
     */
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<Map<String, Object>> getDoctorAvailability(
            @PathVariable String user,
            @PathVariable Long doctorId,
            @PathVariable LocalDate date,
            @PathVariable String token
    ) {

        ResponseEntity<Map<String, String>> tokenValidation =
                service.validateToken(token, user);

        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {

            Map<String, Object> response = new HashMap<>();

            if (tokenValidation.getBody() != null) {
                response.putAll(tokenValidation.getBody());
            }

            return ResponseEntity
                    .status(tokenValidation.getStatusCode())
                    .body(response);
        }

        List<String> availability =
                doctorService.getDoctorAvailability(
                        doctorId,
                        date
                );

        Map<String, Object> response = new HashMap<>();
        response.put("availability", availability);

        return ResponseEntity.ok(response);
    }

    /*
     * Get all doctors.
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDoctor() {

        Map<String, Object> response = new HashMap<>();

        response.put(
                "doctors",
                doctorService.getDoctors()
        );

        return ResponseEntity.ok(response);
    }

    /*
     * Add a new doctor.
     * Requires an admin token.
     */
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> saveDoctor(
            @PathVariable String token,
            @RequestBody Doctor doctor
    ) {

        Map<String, String> response = new HashMap<>();

        ResponseEntity<Map<String, String>> tokenValidation =
                service.validateToken(token, "admin");

        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            return tokenValidation;
        }

        int result = doctorService.saveDoctor(doctor);

        if (result == 1) {

            response.put(
                    "message",
                    "Doctor added to db"
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);
        }

        if (result == -1) {

            response.put(
                    "message",
                    "Doctor already exists"
            );

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(response);
        }

        response.put(
                "message",
                "Some internal error occurred"
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    /*
     * Doctor login.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> doctorLogin(
            @RequestBody Login login
    ) {

        return doctorService.validateDoctor(login);
    }

    /*
     * Update doctor information.
     * Requires an admin token.
     */
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateDoctor(
            @PathVariable String token,
            @RequestBody Doctor doctor
    ) {

        Map<String, String> response = new HashMap<>();

        ResponseEntity<Map<String, String>> tokenValidation =
                service.validateToken(token, "admin");

        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            return tokenValidation;
        }

        int result = doctorService.updateDoctor(doctor);

        if (result == 1) {

            response.put(
                    "message",
                    "Doctor updated"
            );

            return ResponseEntity.ok(response);
        }

        if (result == -1) {

            response.put(
                    "message",
                    "Doctor not found"
            );

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(response);
        }

        response.put(
                "message",
                "Some internal error occurred"
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    /*
     * Delete doctor.
     * Requires an admin token.
     */
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> deleteDoctor(
            @PathVariable long id,
            @PathVariable String token
    ) {

        Map<String, String> response = new HashMap<>();

        ResponseEntity<Map<String, String>> tokenValidation =
                service.validateToken(token, "admin");

        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            return tokenValidation;
        }

        int result = doctorService.deleteDoctor(id);

        if (result == 1) {

            response.put(
                    "message",
                    "Doctor deleted successfully"
            );

            return ResponseEntity.ok(response);
        }

        if (result == -1) {

            response.put(
                    "message",
                    "Doctor not found with id"
            );

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(response);
        }

        response.put(
                "message",
                "Some internal error occurred"
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    /*
     * Filter doctors by name, time and specialty.
     */
    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<Map<String, Object>> filter(
            @PathVariable String name,
            @PathVariable String time,
            @PathVariable String speciality
    ) {

        Map<String, Object> result =
                service.filterDoctor(
                        name,
                        speciality,
                        time
                );

        return ResponseEntity.ok(result);
    }
}