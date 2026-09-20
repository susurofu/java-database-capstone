package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public DoctorService(
            DoctorRepository doctorRepository,
            AppointmentRepository appointmentRepository,
            TokenService tokenService
    ) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    /*
     * Get available slots for a doctor on a specific date.
     */
    @Transactional(readOnly = true)
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {

        Optional<Doctor> doctorOptional = doctorRepository.findById(doctorId);

        if (doctorOptional.isEmpty()) {
            return new ArrayList<>();
        }

        Doctor doctor = doctorOptional.get();

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        List<Appointment> appointments =
                appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                        doctorId,
                        start,
                        end
                );

        /*
         * Extract already-booked times.
         */
        List<String> bookedSlots = appointments.stream()
                .map(Appointment::getAppointmentTime)
                .map(LocalDateTime::toLocalTime)
                .map(LocalTime::toString)
                .collect(Collectors.toList());

        /*
         * Return only available slots that are not already booked.
         */
        return doctor.getAvailableTimes()
                .stream()
                .filter(slot -> !bookedSlots.contains(slot))
                .collect(Collectors.toList());
    }

    /*
     * Save a new doctor.
     *
     *  1 = success
     * -1 = doctor already exists
     *  0 = internal error
     */
    @Transactional
    public int saveDoctor(Doctor doctor) {

        try {
            Doctor existingDoctor =
                    doctorRepository.findByEmail(doctor.getEmail());

            if (existingDoctor != null) {
                return -1;
            }

            doctorRepository.save(doctor);
            return 1;

        } catch (Exception e) {
            return 0;
        }
    }

    /*
     * Update an existing doctor.
     *
     *  1 = success
     * -1 = doctor not found
     *  0 = internal error
     */
    @Transactional
    public int updateDoctor(Doctor doctor) {

        try {
            if (doctor.getId() == null) {
                return -1;
            }

            Optional<Doctor> existingDoctor =
                    doctorRepository.findById(doctor.getId());

            if (existingDoctor.isEmpty()) {
                return -1;
            }

            doctorRepository.save(doctor);
            return 1;

        } catch (Exception e) {
            return 0;
        }
    }

    /*
     * Get all doctors.
     */
    @Transactional(readOnly = true)
    public List<Doctor> getDoctors() {
        return doctorRepository.findAllWithAvailableTimes();
    }

    /*
     * Delete doctor and all appointments belonging to the doctor.
     *
     *  1 = success
     * -1 = doctor not found
     *  0 = internal error
     */
    @Transactional
    public int deleteDoctor(long id) {

        try {
            Optional<Doctor> doctor = doctorRepository.findById(id);

            if (doctor.isEmpty()) {
                return -1;
            }

            appointmentRepository.deleteAllByDoctorId(id);

            doctorRepository.delete(doctor.get());

            return 1;

        } catch (Exception e) {
            return 0;
        }
    }

    /*
     * Validate doctor login.
     */
    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {

        Map<String, String> response = new HashMap<>();

        Doctor doctor =
                doctorRepository.findByEmail(login.getIdentifier());

        if (doctor == null) {
            response.put("message", "Doctor not found.");

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }

        if (!doctor.getPassword().equals(login.getPassword())) {
            response.put("message", "Invalid password.");

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }

        /*
         * Adjust this method call if your TokenService
         * uses a different method name/signature.
         */
        String token = tokenService.generateToken(
                doctor.getEmail()
        );

        response.put("message", "Login successful.");
        response.put("token", token);

        return ResponseEntity.ok(response);
    }

    /*
     * Find doctor by partial name.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> findDoctorByName(String name) {

        Map<String, Object> response = new HashMap<>();

        List<Doctor> doctors =
                doctorRepository.findByNameLike(name);

        response.put("doctors", doctors);

        return response;
    }

    /*
     * Filter by name + specialty + AM/PM.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorsByNameSpecilityandTime(
            String name,
            String specialty,
            String amOrPm
    ) {

        Map<String, Object> response = new HashMap<>();

        List<Doctor> doctors =
                doctorRepository
                        .findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(
                                name,
                                specialty
                        );

        doctors = filterDoctorByTime(doctors, amOrPm);

        response.put("doctors", doctors);

        return response;
    }

    /*
     * Filter by name + AM/PM.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorByNameAndTime(
            String name,
            String amOrPm
    ) {

        Map<String, Object> response = new HashMap<>();

        List<Doctor> doctors =
                doctorRepository.findByNameLike(name);

        doctors = filterDoctorByTime(doctors, amOrPm);

        response.put("doctors", doctors);

        return response;
    }

    /*
     * Filter by name + specialty.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorByNameAndSpecility(
            String name,
            String specilty
    ) {

        Map<String, Object> response = new HashMap<>();

        List<Doctor> doctors =
                doctorRepository
                        .findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(
                                name,
                                specilty
                        );

        response.put("doctors", doctors);

        return response;
    }

    /*
     * Filter by specialty + AM/PM.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorByTimeAndSpecility(
            String specilty,
            String amOrPm
    ) {

        Map<String, Object> response = new HashMap<>();

        List<Doctor> doctors =
                doctorRepository.findBySpecialtyIgnoreCase(specilty);

        doctors = filterDoctorByTime(doctors, amOrPm);

        response.put("doctors", doctors);

        return response;
    }

    /*
     * Filter only by specialty.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorBySpecility(String specilty) {

        Map<String, Object> response = new HashMap<>();

        List<Doctor> doctors =
                doctorRepository.findBySpecialtyIgnoreCase(specilty);

        response.put("doctors", doctors);

        return response;
    }

    /*
     * Filter all doctors by AM/PM.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorsByTime(String amOrPm) {

        Map<String, Object> response = new HashMap<>();

        List<Doctor> doctors =
                doctorRepository.findAll();

        doctors = filterDoctorByTime(doctors, amOrPm);

        response.put("doctors", doctors);

        return response;
    }

    /*
     * Private helper method for filtering doctors
     * according to available AM/PM slots.
     */
    private List<Doctor> filterDoctorByTime(
            List<Doctor> doctors,
            String amOrPm
    ) {

        if (amOrPm == null || amOrPm.isBlank()) {
            return doctors;
        }

        boolean searchAM =
                amOrPm.equalsIgnoreCase("AM");

        boolean searchPM =
                amOrPm.equalsIgnoreCase("PM");

        if (!searchAM && !searchPM) {
            return doctors;
        }

        return doctors.stream()
                .filter(doctor -> {

                    if (doctor.getAvailableTimes() == null) {
                        return false;
                    }

                    return doctor.getAvailableTimes()
                            .stream()
                            .anyMatch(slot -> {

                                try {
                                    LocalTime time =
                                            LocalTime.parse(slot.split("-")[0]);

                                    if (searchAM) {
                                        return time.isBefore(
                                                LocalTime.NOON
                                        );
                                    }

                                    return !time.isBefore(
                                            LocalTime.NOON
                                    );

                                } catch (Exception e) {
                                    return false;
                                }
                            });
                })
                .collect(Collectors.toList());
    }
}
