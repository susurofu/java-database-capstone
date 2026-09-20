package com.project.back_end.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.back_end.models.Doctor;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // Find doctor by email
    @EntityGraph(attributePaths = "availableTimes")
    Doctor findByEmail(String email);


    // Find doctors whose name contains the given text
    @Query("""
        SELECT d
        FROM Doctor d
        WHERE d.name LIKE CONCAT('%', :name, '%')
        """)
    @EntityGraph(attributePaths = "availableTimes")
    List<Doctor> findByNameLike(
            @Param("name") String name
    );


    // Find doctors by partial name and exact specialty,
    // both case-insensitive
    @Query("""
        SELECT d
        FROM Doctor d
        WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))
          AND LOWER(d.specialty) = LOWER(:specialty)
        """)
    @EntityGraph(attributePaths = "availableTimes")
    List<Doctor> findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(
            @Param("name") String name,
            @Param("specialty") String specialty
    );


    // Find doctors by specialty, ignoring case
    @EntityGraph(attributePaths = "availableTimes")
    List<Doctor> findBySpecialtyIgnoreCase(String specialty);

    @EntityGraph(attributePaths = "availableTimes")
    @Query("SELECT d FROM Doctor d")
    List<Doctor> findAllWithAvailableTimes();
}
