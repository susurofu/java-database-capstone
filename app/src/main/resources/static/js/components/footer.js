// footer.js

/*
 * Function to render the footer content into the page.
 * The footer is reusable across all pages.
 */
function renderFooter() {

    // Select the footer element from the DOM.
    const footer = document.getElementById("footer");

    // Prevent an error if the page does not contain #footer.
    if (!footer) {
        return;
    }

    // Set the footer HTML content.
    footer.innerHTML = `
        <footer class="footer">

            <div class="footer-container">

                <!-- Hospital Logo and Copyright -->
                <div class="footer-logo">

                    <img
                        src="../assets/images/logo/logo.png"
                        alt="Hospital CMS Logo"
                    >

                    <p>
                        © Copyright 2025.
                        All Rights Reserved by Hospital CMS.
                    </p>

                </div>


                <!-- Footer Links -->
                <div class="footer-links">

                    <!-- Company Links -->
                    <div class="footer-column">

                        <h4>Company</h4>

                        <a href="#">About</a>
                        <a href="#">Careers</a>
                        <a href="#">Press</a>

                    </div>


                    <!-- Support Links -->
                    <div class="footer-column">

                        <h4>Support</h4>

                        <a href="#">Account</a>
                        <a href="#">Help Center</a>
                        <a href="#">Contact Us</a>

                    </div>


                    <!-- Legal Links -->
                    <div class="footer-column">

                        <h4>Legals</h4>

                        <a href="#">Terms & Conditions</a>
                        <a href="#">Privacy Policy</a>
                        <a href="#">Licensing</a>

                    </div>

                </div>

            </div>

        </footer>
    `;
}


/*
 * Call the function when footer.js loads.
 */
renderFooter();