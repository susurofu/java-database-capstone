/*
 * Handles role-based login for Admin and Doctor.
 * Opens the appropriate login modal and communicates
 * with the backend authentication endpoints.
 */

import { openModal } from '../components/modals.js';
import { API_BASE_URL } from '../config/config.js';

// API endpoints
const ADMIN_API = API_BASE_URL + '/admin';
const DOCTOR_API = API_BASE_URL + '/doctor/login';


/*
 * Wait until the page has fully loaded before
 * attaching event listeners to the login buttons.
 */
window.onload = function () {

    const adminBtn = document.getElementById('admin');
    const doctorBtn = document.getElementById('doctor');

    // Open Admin login modal
    if (adminBtn) {
        adminBtn.addEventListener('click', () => {
            openModal('adminLogin');
        });
    }

    // Open Doctor login modal
    if (doctorBtn) {
        doctorBtn.addEventListener('click', () => {
            openModal('doctorLogin');
        });
    }
};


/*
 * ADMIN LOGIN HANDLER
 *
 * Defined on window so that it can be called directly
 * from the HTML/modal form if necessary.
 */
window.adminLoginHandler = async function () {

    // Get credentials from input fields
    const username = document.getElementById('adminUsername').value;
    const password = document.getElementById('adminPassword').value;

    // Create object to send to backend
    const admin = {
        username,
        password
    };

    try {

        // Send login request
        const response = await fetch(ADMIN_API, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(admin)
        });

        // Login successful
        if (response.ok) {

            const data = await response.json();

            // Store token
            localStorage.setItem('token', data.token);

            // Store selected role and render appropriate page
            selectRole('admin');

        } else {

            alert('Invalid credentials!');

        }

    } catch (error) {

        console.error('Admin login error:', error);
        alert('An error occurred while logging in.');

    }
};


/*
 * DOCTOR LOGIN HANDLER
 *
 * Defined on window so that it can be called directly
 * from the HTML/modal form if necessary.
 */
window.doctorLoginHandler = async function () {

    // Get credentials from input fields
    const email = document.getElementById('doctorEmail').value;
    const password = document.getElementById('doctorPassword').value;

    // Create object to send to backend
    const doctor = {
        email,
        password
    };

    try {

        // Send login request
        const response = await fetch(DOCTOR_API, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(doctor)
        });

        // Login successful
        if (response.ok) {

            const data = await response.json();

            // Store token
            localStorage.setItem('token', data.token);

            // Store selected role and render appropriate page
            selectRole('doctor');

        } else {

            alert('Invalid credentials!');

        }

    } catch (error) {

        console.error('Doctor login error:', error);
        alert('An error occurred while logging in.');

    }
};
