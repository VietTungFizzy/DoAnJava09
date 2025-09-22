
$(document).ready(function () {
    // Login functionality
    $('#btn-login').click(function () {
        var email = $('#email-login').val();
        var password = $('#password-login').val();

        // Validate input fields
        if (!email || !password) {
            showMessage('Please fill in all fields', 'error');
            return;
        }

        // Validate email format
        var emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            showMessage('Please enter a valid email address', 'error');
            return;
        }

        $.ajax({
            method: "POST",
            url: "http://localhost:8080/auth/login",
            contentType: "application/json",
            data: JSON.stringify({ email: email, password: password })
        })
            .done(function (result) {
                console.log(result);
                // Backend returns LoginResponse object directly
                if (result.token) {
                    localStorage.setItem("token", result.token);
                    localStorage.setItem("email", result.email);
                    localStorage.setItem("role", result.role);
                    showMessage('Login successful!', 'success');
                    // Redirect to homepage after short delay
                    setTimeout(function() {
                        window.location.href = 'homepage.html';
                    }, 1500);
                } else {
                    showMessage('Login failed: ' + (result.message || 'Unknown error'), 'error');
                }
            })
            .fail(function (xhr, status, error) {
                console.log('Login error:', xhr.responseText);
                var errorMessage = 'Login error';
                try {
                    var response = JSON.parse(xhr.responseText);
                    errorMessage = response.message || errorMessage;
                } catch (e) {
                    errorMessage = xhr.status === 401 ? 'Invalid credentials' : 'Connection error';
                }
                showMessage(errorMessage, 'error');
            });
    });

    // Register functionality
    $('#btn-signup').click(function () {
        // Disable button during processing
        $('#btn-signup').prop('disabled', true).text('Creating account...');

        var firstname = $('#firstname-signup').val().trim();
        var lastname = $('#lastname-signup').val().trim();
        var fullname = (firstname + ' ' + lastname).trim(); // Combine and clean
        var email = $('#email-signup').val().trim();
        var password1 = $('#password1-signup').val();
        var rePassword = $('#password2-signup').val();
        var phone = $('#phone-signup').val().trim();

        // Validate input fields
        if (!firstname || !lastname || !email || !password1 || !rePassword || !phone) {
            showMessage('Please fill in all fields', 'error');
            resetButton();
            return;
        }

        // Validate phone number format (more flexible)
        var phoneRegex = /^[\+]?[0-9\s\-\(\)]{8,15}$/;
        if (!phoneRegex.test(phone)) {
            showMessage('Please enter a valid phone number', 'error');
            resetButton();
            return;
        }

        // Validate email format
        var emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            showMessage('Please enter a valid email address', 'error');
            resetButton();
            return;
        }

        // Validate password match
        if (rePassword !== password1) {
            showMessage('Passwords do not match!', 'error');
            resetButton();
            return;
        }

        // Validate password strength
        if (password1.length < 6) {
            showMessage('Password must be at least 6 characters long', 'error');
            resetButton();
            return;
        }

        $.ajax({
            method: "POST",
            url: "http://localhost:8080/auth/register",
            contentType: "application/json",
            data: JSON.stringify({
                email: email,
                password: password1,
                name: fullname,  // Backend expects 'name' not 'fullName'
                phone: phone
            })
        })
            .done(function (result) {
                console.log(result);
                // Backend returns string message directly on success
                if (result && typeof result === 'string' && result.includes('thành công')) {
                    showMessage('Registration successful! Please login.', 'success');
                    // Clear form fields
                    // clearForm();
                    // Redirect to login page after 2 seconds
                    setTimeout(function () {
                        window.location.href = 'page-login.html';
                    }, 2000);
                } else {
                    showMessage('Registration failed: ' + (result || 'Unknown error'), 'error');
                }
                resetButton();
            })
            .fail(function (xhr, status, error) {
                console.log('Registration error:', xhr.responseText);
                var errorMessage = 'Registration error';
                try {
                    // Backend returns string error message directly
                    errorMessage = xhr.responseText || errorMessage;
                } catch (e) {
                    errorMessage = xhr.status === 400 ? 'Invalid registration data' : 'Connection error';
                }
                showMessage(errorMessage, 'error');
                resetButton();
            });
    });

    // Helper functions
    function showMessage(message, type) {
        // Remove existing messages
        $('.alert').remove();

        var alertClass = type === 'success' ? 'alert-success' : 'alert-danger';
        var alertHtml = '<div class="alert ' + alertClass + ' alert-dismissible fade show mt-3" role="alert">' +
            message +
            '<button type="button" class="btn-close" data-bs-dismiss="alert"></button>' +
            '</div>';

        // Try to find form or fieldset to prepend the message
        var target = $('form').first();
        if (target.length === 0) {
            target = $('fieldset').first();
        }
        if (target.length === 0) {
            target = $('.card').first();
        }
        target.prepend(alertHtml);

        // Auto remove after 5 seconds
        setTimeout(function () {
            $('.alert').fadeOut();
        }, 5000);
    }

    function resetButton() {
        $('#btn-signup').prop('disabled', false).text('Create my account');
    }

    function clearForm() {
        $('#firstname-signup').val('');
        $('#lastname-signup').val('');
        $('#email-signup').val('');
        $('#password1-signup').val('');
        $('#password2-signup').val('');
        $('#phone-signup').val('');
    }
});