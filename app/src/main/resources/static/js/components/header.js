// header.js

function renderHeader() {
    const headerDiv = document.getElementById("header");

    // If this page does not contain a header placeholder,
    // there is nothing to render.
    if (!headerDiv) {
        return;
    }

    /*
     * When the user returns to the main homepage,
     * clear the previous login/session information.
     */
    if (
        window.location.pathname === "/" ||
        window.location.pathname.endsWith("/index.html")
    ) {
        localStorage.removeItem("userRole");
        localStorage.removeItem("token");
        return;
    }

    // Get current role and login token.
    const role = localStorage.getItem("userRole");
    const token = localStorage.getItem("token");

    /*
     * Admin, doctor, and logged-in patient users
     * must have a valid token.
     */
    if (
        (role === "loggedPatient" ||
            role === "admin" ||
            role === "doctor") &&
        !token
    ) {
        localStorage.removeItem("userRole");

        alert(
            "Session expired or invalid login. Please log in again."
        );

        window.location.href = "/";
        return;
    }

    let headerContent = "";

    /*
     * Common header structure.
     */
    headerContent += `
        <header class="header">
            <div class="header-container">

                <div class="header-logo">
                    <a href="/">
                        Clinic Management System
                    </a>
                </div>

                <nav class="header-nav">
    `;


    /* =========================
       Admin Header
       ========================= */

    if (role === "admin") {
        headerContent += `
            <button
                id="addDocBtn"
                class="adminBtn"
                type="button"
            >
                Add Doctor
            </button>

            <a href="#" id="logoutBtn">
                Logout
            </a>
        `;
    }


    /* =========================
       Doctor Header
       ========================= */

    else if (role === "doctor") {
        headerContent += `
            <a href="#" id="doctorHomeBtn">
                Home
            </a>

            <a href="#" id="logoutBtn">
                Logout
            </a>
        `;
    }


    /* =========================
       Patient Header
       Not Logged In
       ========================= */

    else if (role === "patient") {
        headerContent += `
            <button
                id="patientLoginBtn"
                type="button"
            >
                Login
            </button>

            <button
                id="patientSignupBtn"
                type="button"
            >
                Sign Up
            </button>
        `;
    }


    /* =========================
       Logged-In Patient Header
       ========================= */

    else if (role === "loggedPatient") {
        headerContent += `
            <a href="#" id="patientHomeBtn">
                Home
            </a>

            <a href="#" id="appointmentsBtn">
                Appointments
            </a>

            <a href="#" id="logoutPatientBtn">
                Logout
            </a>
        `;
    }


    /*
     * Close navigation/header containers.
     */
    headerContent += `
                </nav>
            </div>
        </header>
    `;


    /*
     * Insert generated header into the page.
     */
    headerDiv.innerHTML = headerContent;


    /*
     * Header elements now exist in the DOM,
     * so listeners can be attached.
     */
    attachHeaderButtonListeners();
}


/* =====================================================
   Attach Header Event Listeners
   ===================================================== */

function attachHeaderButtonListeners() {

    /* ---------- Admin: Add Doctor ---------- */

    const addDocBtn = document.getElementById("addDocBtn");

    if (addDocBtn) {
        addDocBtn.addEventListener("click", () => {
            if (typeof openModal === "function") {
                openModal("addDoctor");
            } else {
                console.error(
                    "openModal() function is not available."
                );
            }
        });
    }


    /* ---------- Logout ---------- */

    const logoutBtn = document.getElementById("logoutBtn");

    if (logoutBtn) {
        logoutBtn.addEventListener("click", (event) => {
            event.preventDefault();
            logout();
        });
    }


    /* ---------- Patient Logout ---------- */

    const logoutPatientBtn =
        document.getElementById("logoutPatientBtn");

    if (logoutPatientBtn) {
        logoutPatientBtn.addEventListener("click", (event) => {
            event.preventDefault();
            logoutPatient();
        });
    }


    /* ---------- Patient Login ---------- */

    const patientLoginBtn =
        document.getElementById("patientLoginBtn");

    if (patientLoginBtn) {
        patientLoginBtn.addEventListener("click", () => {
            if (typeof openModal === "function") {
                openModal("patientLogin");
            } else {
                console.error(
                    "openModal() function is not available."
                );
            }
        });
    }


    /* ---------- Patient Sign Up ---------- */

    const patientSignupBtn =
        document.getElementById("patientSignupBtn");

    if (patientSignupBtn) {
        patientSignupBtn.addEventListener("click", () => {
            if (typeof openModal === "function") {
                openModal("patientSignup");
            } else {
                console.error(
                    "openModal() function is not available."
                );
            }
        });
    }


    /* ---------- Doctor Home ---------- */

    const doctorHomeBtn =
        document.getElementById("doctorHomeBtn");

    if (doctorHomeBtn) {
        doctorHomeBtn.addEventListener("click", (event) => {
            event.preventDefault();

            window.location.href = "/doctor/dashboard";
        });
    }


    /* ---------- Patient Home ---------- */

    const patientHomeBtn =
        document.getElementById("patientHomeBtn");

    if (patientHomeBtn) {
        patientHomeBtn.addEventListener("click", (event) => {
            event.preventDefault();

            window.location.href =
                "/pages/patientDashboard.html";
        });
    }


    /* ---------- Patient Appointments ---------- */

    const appointmentsBtn =
        document.getElementById("appointmentsBtn");

    if (appointmentsBtn) {
        appointmentsBtn.addEventListener("click", (event) => {
            event.preventDefault();

            window.location.href =
                "/pages/appointments.html";
        });
    }
}


/* =====================================================
   Logout
   ===================================================== */

function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("userRole");

    window.location.href = "/";
}


/* =====================================================
   Patient Logout
   ===================================================== */

function logoutPatient() {
    localStorage.removeItem("token");

    /*
     * Keep the user identified as a patient so that
     * Login and Sign Up buttons are displayed.
     */
    localStorage.setItem("userRole", "patient");

    window.location.href =
        "/pages/patientDashboard.html";
}


/* =====================================================
   Initial Render
   ===================================================== */

renderHeader();