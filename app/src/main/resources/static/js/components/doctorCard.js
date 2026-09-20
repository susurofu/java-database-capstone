// doctorCard.js

/*
 * Import the overlay function used when a logged-in patient
 * wants to book an appointment.
 */
import { showBookingOverlay } from "../services/loggedPatient.js";

/*
 * Import the API function used by admins to delete doctors.
 */
import { deleteDoctor } from "../services/doctorServices.js";

/*
 * Import the function used to fetch patient details.
 */
import { getPatientData } from "../services/patientServices.js";


/*
 * Function to create and return a DOM element
 * representing one doctor card.
 */
export function createDoctorCard(doctor) {

    /* =========================
       Main Card Container
       ========================= */

    const card = document.createElement("div");
    card.classList.add("doctor-card");


    /* =========================
       Get Current User Role
       ========================= */

    const role = localStorage.getItem("userRole");


    /* =========================
       Doctor Information
       ========================= */

    const infoDiv = document.createElement("div");
    infoDiv.classList.add("doctor-info");


    /* Doctor Name */
    const name = document.createElement("h3");
    name.textContent = doctor.name;


    /* Doctor Specialization */
    const specialization = document.createElement("p");
    specialization.textContent =
        `Specialization: ${doctor.specialization}`;


    /* Doctor Email */
    const email = document.createElement("p");
    email.textContent =
        `Email: ${doctor.email}`;


    /* Doctor Availability */
    const availability = document.createElement("p");

    if (Array.isArray(doctor.availability)) {
        availability.textContent =
            `Availability: ${doctor.availability.join(", ")}`;
    } else {
        availability.textContent =
            `Availability: ${doctor.availability}`;
    }


    /* Add doctor details to info container */
    infoDiv.appendChild(name);
    infoDiv.appendChild(specialization);
    infoDiv.appendChild(email);
    infoDiv.appendChild(availability);


    /* =========================
       Action Buttons Container
       ========================= */

    const actionsDiv = document.createElement("div");
    actionsDiv.classList.add("card-actions");


    /* =====================================================
       ADMIN ROLE ACTIONS
       ===================================================== */

    if (role === "admin") {

        /* Create Delete Button */
        const deleteBtn = document.createElement("button");

        deleteBtn.textContent = "Delete";
        deleteBtn.classList.add("delete-btn");


        /* Delete Button Click Handler */
        deleteBtn.addEventListener("click", async () => {

            const confirmed = confirm(
                `Are you sure you want to delete ${doctor.name}?`
            );

            if (!confirmed) {
                return;
            }


            /* Get admin token */
            const token = localStorage.getItem("token");

            if (!token) {
                alert(
                    "Session expired. Please log in again."
                );

                localStorage.removeItem("userRole");

                window.location.href = "/";
                return;
            }


            try {

                /*
                 * Call the API to delete the doctor.
                 */
                const result = await deleteDoctor(
                    doctor.id,
                    token
                );


                /*
                 * If deletion succeeds, remove the card
                 * from the page.
                 */
                if (result) {

                    alert(
                        "Doctor deleted successfully."
                    );

                    card.remove();

                } else {

                    alert(
                        "Unable to delete doctor."
                    );
                }

            } catch (error) {

                console.error(
                    "Error deleting doctor:",
                    error
                );

                alert(
                    "An error occurred while deleting the doctor."
                );
            }
        });


        /* Add button to action container */
        actionsDiv.appendChild(deleteBtn);
    }


    /* =====================================================
       PATIENT - NOT LOGGED IN
       ===================================================== */

    else if (role === "patient") {

        /* Create Book Now Button */
        const bookNowBtn = document.createElement("button");

        bookNowBtn.textContent = "Book Now";
        bookNowBtn.classList.add("book-btn");


        /*
         * Patient must log in before booking.
         */
        bookNowBtn.addEventListener("click", () => {

            alert(
                "Patient needs to login first."
            );
        });


        /* Add button to actions container */
        actionsDiv.appendChild(bookNowBtn);
    }


    /* =====================================================
       LOGGED-IN PATIENT
       ===================================================== */

    else if (role === "loggedPatient") {

        /* Create Book Now Button */
        const bookNowBtn = document.createElement("button");

        bookNowBtn.textContent = "Book Now";
        bookNowBtn.classList.add("book-btn");


        /*
         * Booking logic for logged-in patients.
         */
        bookNowBtn.addEventListener(
            "click",
            async (event) => {

                /*
                 * Get the patient token.
                 */
                const token =
                    localStorage.getItem("token");


                /*
                 * Redirect if there is no token.
                 */
                if (!token) {

                    alert(
                        "Session expired. Please log in again."
                    );

                    localStorage.setItem(
                        "userRole",
                        "patient"
                    );

                    window.location.href =
                        "/pages/patientDashboard.html";

                    return;
                }


                try {

                    /*
                     * Fetch patient details.
                     */
                    const patientData =
                        await getPatientData(token);


                    /*
                     * Display the booking UI.
                     */
                    showBookingOverlay(
                        event,
                        doctor,
                        patientData
                    );

                } catch (error) {

                    console.error(
                        "Error fetching patient data:",
                        error
                    );

                    alert(
                        "Unable to load patient information."
                    );
                }
            }
        );


        /* Add button to actions container */
        actionsDiv.appendChild(bookNowBtn);
    }


    /* =========================
       Final Card Assembly
       ========================= */

    card.appendChild(infoDiv);
    card.appendChild(actionsDiv);


    /* Return completed doctor card */
    return card;
}