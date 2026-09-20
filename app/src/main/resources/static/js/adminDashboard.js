// adminDashboard.js

import { openModal } from "./components/modals.js";
import {
    getDoctors,
    filterDoctors,
    saveDoctor
} from "./services/doctorServices.js";
import { createDoctorCard } from "./components/doctorCard.js";


/*
 * Open the Add Doctor modal
 */
/*
 * Load doctors when the page is ready
 */
document.addEventListener("DOMContentLoaded", () => {
    const addDoctorBtn = document.getElementById("addDocBtn");

    if (addDoctorBtn) {
        addDoctorBtn.addEventListener("click", () => {
            openModal("addDoctor");
        });
    }

    loadDoctorCards();

    const searchBar = document.getElementById("searchBar");
    const filterTime = document.getElementById("filterTime");
    const filterSpecialty = document.getElementById("filterSpecialty");

    if (searchBar) {
        searchBar.addEventListener("input", filterDoctorsOnChange);
    }

    if (filterTime) {
        filterTime.addEventListener("change", filterDoctorsOnChange);
    }

    if (filterSpecialty) {
        filterSpecialty.addEventListener("change", filterDoctorsOnChange);
    }
});


/*
 * Function: loadDoctorCards
 * Purpose: Fetch and display all doctors
 */
async function loadDoctorCards() {
    try {
        const doctors = await getDoctors();

        renderDoctorCards(doctors);

    } catch (error) {
        console.error("Error loading doctors:", error);
    }
}


/*
 * Function: filterDoctorsOnChange
 * Purpose: Filter doctors by name, time, and specialty
 */
async function filterDoctorsOnChange() {
    try {
        const searchBar = document.getElementById("searchBar");
        const filterTime = document.getElementById("filterTime");
        const filterSpecialty = document.getElementById("filterSpecialty");

        // Read filter values
        let name = searchBar ? searchBar.value.trim() : "";
        let time = filterTime ? filterTime.value : "";
        let specialty = filterSpecialty ? filterSpecialty.value : "";

        // Normalize empty values
        name = name || "null";
        time = time || "null";
        specialty = specialty || "null";

        // Fetch filtered doctors
        const response = await filterDoctors(name, time, specialty);

        const doctors = response.doctors || [];

        if (doctors.length > 0) {
            renderDoctorCards(doctors);
        } else {
            const contentDiv = document.getElementById("content");

            if (contentDiv) {
                contentDiv.innerHTML =
                    "<p>No doctors found with the given filters.</p>";
            }
        }

    } catch (error) {
        console.error("Error filtering doctors:", error);
        alert("Something went wrong while filtering doctors.");
    }
}


/*
 * Function: renderDoctorCards
 * Purpose: Render a list of doctors
 */
function renderDoctorCards(doctors) {
    const contentDiv = document.getElementById("content");

    if (!contentDiv) {
        return;
    }

    // Clear current doctor cards
    contentDiv.innerHTML = "";

    doctors.forEach((doctor) => {
        const doctorCard = createDoctorCard(doctor);
        contentDiv.appendChild(doctorCard);
    });
}


/*
 * Function: adminAddDoctor
 * Purpose: Add a new doctor
 */
window.adminAddDoctor = async function () {
    try {
        // Get values from modal form
        const name = document.getElementById("doctorName").value.trim();
        const email = document.getElementById("doctorEmail").value.trim();
        const phone = document.getElementById("doctorPhone").value.trim();
        const password = document.getElementById("doctorPassword").value;
        const specialty =
            document.getElementById("doctorSpecialty").value.trim();

        /*
         * Collect selected availability times.
         *
         * Assumes availability checkboxes use:
         * name="availability"
         */
        const availableTimes = Array.from(
            document.querySelectorAll(
                'input[name="availability"]:checked'
            )
        ).map((checkbox) => checkbox.value);

        // Retrieve admin authentication token
        const token = localStorage.getItem("token");

        if (!token) {
            alert("Authentication token not found. Please login again.");
            return;
        }

        // Build doctor object
        const doctor = {
            name,
            email,
            phone,
            password,
            specialty,
            availableTimes
        };

        // Send doctor data to backend
        const result = await saveDoctor(doctor, token);

        if (result.success) {
            alert(result.message || "Doctor added successfully.");

            // Refresh the page so modal closes and list is updated
            window.location.reload();

        } else {
            alert(result.message || "Failed to add doctor.");
        }

    } catch (error) {
        console.error("Error adding doctor:", error);
        alert("Something went wrong while adding the doctor.");
    }
};
