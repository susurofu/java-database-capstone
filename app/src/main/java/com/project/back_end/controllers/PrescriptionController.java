package com.project.back_end.controllers;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.Service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${api.path}prescription")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final Service service;
    private final AppointmentService appointmentService;

    public PrescriptionController(
            PrescriptionService prescriptionService,
            Service service,
            AppointmentService appointmentService
    ) {
        this.prescriptionService = prescriptionService;
        this.service = service;
        this.appointmentService = appointmentService;
    }

    /*
     * Save a prescription.
     * Only authenticated doctors are allowed.
     */
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> savePrescription(
            @PathVariable String token,
            @RequestBody Prescription prescription
    ) {

        ResponseEntity<Map<String, String>> tokenValidation =
                service.validateToken(token, "doctor");

        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            return tokenValidation;
        }

        ResponseEntity<Map<String, String>> result =
                prescriptionService.savePrescription(prescription);

        /*
         * If the prescription was successfully created,
         * mark the related appointment as completed.
         *
         * status:
         * 0 = scheduled
         * 1 = completed
         */
        if (result.getStatusCode().is2xxSuccessful()) {

            appointmentService.changeStatus(
                    prescription.getAppointmentID(),
                    1
            );
        }

        return result;
    }

    /*
     * Get prescription(s) associated with an appointment.
     */
    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<Map<String, Object>> getPrescription(
            @PathVariable Long appointmentId,
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

        return prescriptionService.getPrescription(
                appointmentId
        );
    }
}
