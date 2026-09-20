// patientServices.js

import { API_BASE_URL } from "../config/config.js";

const PATIENT_API = API_BASE_URL + "/patient";


/*
 * Function: patientSignup
 * Purpose: Create/register a new patient
 */
export async function patientSignup(data) {
    try {
        // Send patient data to the backend
        const response = await fetch(PATIENT_API, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        });

        // Parse backend response
        const result = await response.json();

        // Handle unsuccessful signup
        if (!response.ok) {
            throw new Error(result.message);
        }

        return {
            success: true,
            message: result.message
        };

    } catch (error) {
        console.error("Error :: patientSignup ::", error);

        return {
            success: false,
            message: error.message
        };
    }
}


/*
 * Function: patientLogin
 * Purpose: Authenticate a patient
 */
export async function patientLogin(data) {
    try {
        console.log("patientLogin ::", data);

        // Send login credentials to backend
        const response = await fetch(`${PATIENT_API}/login`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        });

        // Return full response so frontend can check status/token
        return response;

    } catch (error) {
        console.error("Error :: patientLogin ::", error);
        throw error;
    }
}


/*
 * Function: getPatientData
 * Purpose: Get information about the logged-in patient
 */
export async function getPatientData(token) {
    try {
        // Request patient data using authentication token
        const response = await fetch(`${PATIENT_API}/${token}`, {
            method: "GET"
        });

        const data = await response.json();

        if (response.ok) {
            return data.patient;
        }

        return null;

    } catch (error) {
        console.error("Error fetching patient details:", error);
        return null;
    }
}


/*
 * Function: getPatientAppointments
 * Purpose:
 * Fetch patient records or appointments depending on
 * whether the requesting user is a patient or doctor
 */
export async function getPatientAppointments(id, token, user) {
    try {
        // Dynamic endpoint based on patient ID and user role
        const response = await fetch(
            `${PATIENT_API}/${id}/${user}/${token}`,
            {
                method: "GET"
            }
        );

        const data = await response.json();

        if (response.ok) {
            console.log(data.appointments);
            return data.appointments;
        }

        return null;

    } catch (error) {
        console.error("Error fetching patient appointments:", error);
        return null;
    }
}


/*
 * Function: filterAppointments
 * Purpose: Filter appointments by condition and patient name
 */
export async function filterAppointments(condition, name, token) {
    try {
        // Request filtered appointment data
        const response = await fetch(
            `${PATIENT_API}/filter/${condition}/${name}/${token}`,
            {
                method: "GET",
                headers: {
                    "Content-Type": "application/json"
                }
            }
        );

        if (response.ok) {
            const data = await response.json();
            return data;
        }

        console.error(
            "Failed to fetch appointments:",
            response.statusText
        );

        return {
            appointments: []
        };

    } catch (error) {
        console.error("Error filtering appointments:", error);
        alert("Something went wrong!");

        return {
            appointments: []
        };
    }
}