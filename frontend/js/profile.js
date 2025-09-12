// Avatar preview logic
$(function() {
    $("#avatarInput").on("change", function(e) {
        const file = this.files[0];
        if (file && file.type.startsWith('image/')) {
            const reader = new FileReader();
            reader.onload = function(e) {
                $("#avatarImg").attr("src", e.target.result);
            }
            reader.readAsDataURL(file);
        }
    });
    // Checkbox mutual exclusive logic for gender
    $('#nam').on('change', function() {
        if (this.checked) $('#nu').prop('checked', false);
    });
    $('#nu').on('change', function() {
        if (this.checked) $('#nam').prop('checked', false);
    });
    // No real form submit
    $('#profileForm').on('submit', function(e) {
        e.preventDefault();
        alert('Thông tin đã được lưu (demo)');
    });
});