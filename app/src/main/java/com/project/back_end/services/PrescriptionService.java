package com.project.back_end.services;

import com.project.back_end.models.Prescription;
import com.project.back_end.repo.PrescriptionRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionService(
            PrescriptionRepository prescriptionRepository
    ) {
        this.prescriptionRepository = prescriptionRepository;
    }

    /*
     * Save a new prescription.
     */
    public ResponseEntity<Map<String, String>> savePrescription(
            Prescription prescription
    ) {

        Map<String, String> response = new HashMap<>();

        try {

            /*
             * Check whether a prescription already exists
             * for this appointment.
             */
            List<Prescription> existingPrescriptions =
                    prescriptionRepository.findByAppointmentId(
                            prescription.getAppointmentId()
                    );

            if (existingPrescriptions != null
                    && !existingPrescriptions.isEmpty()) {

                response.put(
                        "message",
                        "Prescription already exists"
                );

                return ResponseEntity
                        .badRequest()
                        .body(response);
            }

            prescriptionRepository.save(prescription);

            response.put(
                    "message",
                    "Prescription saved"
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);

        } catch (Exception e) {

            e.printStackTrace();

            response.put(
                    "message",
                    "Error saving prescription"
            );

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    /*
     * Get prescriptions associated with an appointment.
     */
    public ResponseEntity<Map<String, Object>> getPrescription(
            Long appointmentId
    ) {

        Map<String, Object> response = new HashMap<>();

        try {

            List<Prescription> prescriptions =
                    prescriptionRepository.findByAppointmentId(
                            appointmentId
                    );

            response.put(
                    "prescription",
                    prescriptions
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            e.printStackTrace();

            response.put(
                    "message",
                    "Error retrieving prescription"
            );

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }
}