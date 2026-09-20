// patientDashboard.js

import { createDoctorCard } from "./components/doctorCard.js";
import { openModal } from "./components/modals.js";

import {
    getDoctors,
    filterDoctors
} from "./services/doctorServices.js";

import {
    patientLogin,
    patientSignup
} from "./services/patientServices.js";


/*
 * Load doctors when the page loads
 */
document.addEventListener("DOMContentLoaded", () => {
    loadDoctorCards();

    const searchBar = document.getElementById("searchBar");
    const filterTime = document.getElementById("filterTime");
    const filterSpecialty = document.getElementById("filterSpecialty");

    if (searchBar) {
        searchBar.addEventListener(
            "input",
            filterDoctorsOnChange
        );
    }

    if (filterTime) {
        filterTime.addEventListener(
            "change",
            filterDoctorsOnChange
        );
    }

    if (filterSpecialty) {
        filterSpecialty.addEventListener(
            "change",
            filterDoctorsOnChange
        );
    }
});


/*
 * Open Patient Signup modal
 */
document.addEventListener("DOMContentLoaded", () => {
    const signupBtn =
        document.getElementById("patientSignup");

    if (signupBtn) {
        signupBtn.addEventListener("click", () => {
            openModal("patientSignup");
        });
    }
});


/*
 * Open Patient Login modal
 */
document.addEventListener("DOMContentLoaded", () => {
    const loginBtn =
        document.getElementById("patientLogin");

    if (loginBtn) {
        loginBtn.addEventListener("click", () => {
            openModal("patientLogin");
        });
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
        console.error(
            "Error loading doctors:",
            error
        );
    }
}


/*
 * Function: filterDoctorsOnChange
 * Purpose: Filter doctors based on search text,
 * available time, and specialty
 */
async function filterDoctorsOnChange() {
    try {
        const searchBar =
            document.getElementById("searchBar");

        const filterTime =
            document.getElementById("filterTime");

        const filterSpecialty =
            document.getElementById(
                "filterSpecialty"
            );

        let name =
            searchBar.value.trim();

        let time =
            filterTime.value;

        let specialty =
            filterSpecialty.value;

        // Backend expects null-like path values
        name = name || "null";
        time = time || "null";
        specialty = specialty || "null";

        const response = await filterDoctors(
            name,
            time,
            specialty
        );

        const doctors =
            response.doctors || [];

        if (doctors.length > 0) {
            renderDoctorCards(doctors);
        } else {
            const contentDiv =
                document.getElementById("content");

            contentDiv.innerHTML =
                "<p>No doctors found with the given filters.</p>";
        }

    } catch (error) {
        console.error(
            "Error filtering doctors:",
            error
        );

        alert(
            "Something went wrong while filtering doctors."
        );
    }
}


/*
 * Function: renderDoctorCards
 * Purpose: Render a list of doctors
 */
function renderDoctorCards(doctors) {
    const contentDiv =
        document.getElementById("content");

    contentDiv.innerHTML = "";

    doctors.forEach((doctor) => {
        const doctorCard =
            createDoctorCard(doctor);

        contentDiv.appendChild(
            doctorCard
        );
    });
}


/*
 * Function: signupPatient
 * Purpose: Register a new patient
 *
 * Made global because the modal form can call it
 * using onclick / onsubmit.
 */
window.signupPatient = async function () {
    try {
        const name =
            document.getElementById(
                "patientName"
            ).value.trim();

        const email =
            document.getElementById(
                "patientEmail"
            ).value.trim();

        const password =
            document.getElementById(
                "patientPassword"
            ).value;

        const phone =
            document.getElementById(
                "patientPhone"
            ).value.trim();

        const address =
            document.getElementById(
                "patientAddress"
            ).value.trim();

        const patient = {
            name,
            email,
            password,
            phone,
            address
        };

        const result =
            await patientSignup(patient);

        if (result.success) {
            alert(
                result.message ||
                "Patient registered successfully."
            );

            window.location.reload();

        } else {
            alert(
                result.message ||
                "Patient registration failed."
            );
        }

    } catch (error) {
        console.error(
            "Patient signup error:",
            error
        );

        alert(
            "Something went wrong during signup."
        );
    }
};


/*
 * Function: loginPatient
 * Purpose: Authenticate patient and store token
 */
window.loginPatient = async function () {
    try {
        const email =
            document.getElementById(
                "loginPatientEmail"
            ).value.trim();

        const password =
            document.getElementById(
                "loginPatientPassword"
            ).value;

        const credentials = {
            email,
            password
        };

        const response =
            await patientLogin(credentials);

        if (response.ok) {
            const data =
                await response.json();

            // Save authentication token
            localStorage.setItem(
                "token",
                data.token
            );

            // Save patient role
            localStorage.setItem(
                "role",
                "patient"
            );

            // Redirect to logged-in dashboard
            window.location.href =
                "loggedPatientDashboard.html";

        } else {
            alert(
                "Invalid credentials!"
            );
        }

    } catch (error) {
        console.error(
            "Patient login error:",
            error
        );

        alert(
            "Something went wrong during login."
        );
    }
};