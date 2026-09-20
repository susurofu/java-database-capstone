// doctorDashboard.js

import { getAllAppointments } from "./services/appointmentRecordService.js";
import { createPatientRow } from "./components/patientRows.js";


// Table body where appointment rows will be rendered
const patientTableBody = document.getElementById("patientTableBody");

// Today's date in YYYY-MM-DD format
let selectedDate = new Date().toISOString().split("T")[0];

// Authentication token
const token = localStorage.getItem("token");

// Patient name filter
let patientName = "null";


/*
 * Search patient by name
 */
const searchBar = document.getElementById("searchBar");

if (searchBar) {
    searchBar.addEventListener("input", () => {
        const value = searchBar.value.trim();

        patientName = value !== "" ? value : "null";

        loadAppointments();
    });
}


/*
 * Show today's appointments
 */
const todayButton = document.getElementById("todayButton");

if (todayButton) {
    todayButton.addEventListener("click", () => {
        selectedDate = new Date().toISOString().split("T")[0];

        const datePicker = document.getElementById("datePicker");

        if (datePicker) {
            datePicker.value = selectedDate;
        }

        loadAppointments();
    });
}


/*
 * Filter appointments by selected date
 */
const datePicker = document.getElementById("datePicker");

if (datePicker) {
    datePicker.value = selectedDate;

    datePicker.addEventListener("change", () => {
        selectedDate = datePicker.value;

        loadAppointments();
    });
}


/*
 * Function: loadAppointments
 * Purpose: Fetch and render appointments for the selected date
 * and optional patient-name filter
 */
async function loadAppointments() {
    try {
        // Fetch appointments from backend
        const appointments = await getAllAppointments(
            selectedDate,
            patientName,
            token
        );

        // Clear existing rows
        patientTableBody.innerHTML = "";

        // No appointments found
        if (!appointments || appointments.length === 0) {
            patientTableBody.innerHTML = `
                <tr>
                    <td colspan="5">
                        No Appointments found for today.
                    </td>
                </tr>
            `;

            return;
        }

        // Render each appointment
        appointments.forEach((appointment) => {
            const patient = {
                id: appointment.patient.id,
                name: appointment.patient.name,
                phone: appointment.patient.phone,
                email: appointment.patient.email
            };

            const row = createPatientRow(
                patient,
                appointment
            );

            patientTableBody.appendChild(row);
        });

    } catch (error) {
        console.error("Error loading appointments:", error);

        patientTableBody.innerHTML = `
            <tr>
                <td colspan="5">
                    Error loading appointments. Try again later.
                </td>
            </tr>
        `;
    }
}


/*
 * Load page content and today's appointments
 */
document.addEventListener("DOMContentLoaded", () => {

    if (typeof renderContent === "function") {
        renderContent();
    }

    loadAppointments();
});