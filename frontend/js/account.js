$(document).ready(function () {
    // Login functionality
    $('#btn-login').click(function () {
        var email = $('#email-login').val();
        var password = $('#password-login').val();

        // Validate input fields
        if (!email || !password) {
            alert('Please fill in all fields');
            return;
        }

        $.ajax({
            method: "POST",
            url: "http://localhost:8080/auth/signin",
            contentType: "application/json",
            data: JSON.stringify({ email: email, password: password })
        })
        .done(function (result) {
            console.log(result.code);
            if (result.code === 200) {
                localStorage.setItem("token", result.data);
                alert('Login successful!');
                // Redirect to homepage
                window.location.href = '/homepage.html';
            } else {
                alert('Login failed: ' + result.message);
            }
        })
        .fail(function (xhr, status, error) {
            alert('Login error: ' + error);
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
            url: "http://localhost:8080/auth/signup",
            contentType: "application/json",
            data: JSON.stringify({
                email: email,
                password: password1,
                fullName: fullname,
                phone: phone
            })
        })
        .done(function (result) {
            console.log(result);
            if (result.code === 200) {
                showMessage('Registration successful! Please login.', 'success');
                // Clear form fields
                clearForm();
                // Redirect to login page after 2 seconds
                setTimeout(function() {
                    window.location.href = 'page-login.html';
                }, 2000);
            } else {
                showMessage('Registration failed: ' + (result.message || 'Unknown error'), 'error');
            }
            resetButton();
        })
        .fail(function (xhr, status, error) {
            showMessage('Registration error: ' + error, 'error');
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
        
        $('form.mb-4').prepend(alertHtml);
        
        // Auto remove after 5 seconds
        setTimeout(function() {
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