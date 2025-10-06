
$(document).ready(function () {
    // Gọi API lấy danh sách user
    $.ajax({
        method: "GET",
        url: "http://localhost:8080/admin/listUsers",
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem('token')
        },
        contentType: "application/json"
    })
        .done(function (users) {
            // Lưu trữ data users để sử dụng lại
            usersData = users;

            var html = "";
            var roleName = "";
            for (var i = 0; i < users.length; i++) {
                var user = users[i];

                if (user.roleId === 1) {
                    roleName = "Admin";
                } else if (user.roleId === 2) {
                    roleName = "Seller";
                } else if (user.roleId === 3) {
                    roleName = "Buyer";
                }
                html += `<tr>
                <td>#${user.id}</td>
                <td><img src="images/no-avatar.png" alt="${user.name}" class="rounded-circle" width="40" height="40"></td>
                <td><div class="fw-medium">${user.name}</div></td>
                <td>${user.email}</td>
                <td>${user.phone || '-'}</td>
                <td><span class="badge bg-success"> ${roleName}</span></td>
                <td>${user.createdAt ? (new Date(user.createdAt)).toLocaleDateString('en-GB') : '-'}</td>
                <td class="text-center">
                    <div class="btn-group btn-group-sm">
                        <button id="btn-edit-${user.id}" class="btn btn-outline-primary" title="Edit">Sửa</button>
                        <button id="btn-delete-${user.id}" class="btn btn-outline-danger" title="Delete">Xóa</button>
                    </div>
                </td>
            </tr>`;
            }
            // Render vào tbody của bảng user
            $("#users-table tbody").html(html);
        })
        .fail(function (xhr, status, error) {
            $("#users-table tbody").html('<tr><td colspan="8" class="text-center text-danger">Không thể tải danh sách user</td></tr>');
        });

    // Lưu trữ data users để sử dụng lại
    var usersData = [];

    // Xử lý sự kiện cho nút Edit
    $(document).on('click', '[id^="btn-edit-"]', function () {
        var userId = parseInt($(this).attr('id').split('-')[2]);

        // Tìm user data từ mảng đã lưu
        var currentUser = usersData.find(user => user.id === userId);

        if (!currentUser) {
            showMessage('Không tìm thấy thông tin user!', 'error');
            return;
        }

        // Điền thông tin vào form trực tiếp từ data gốc
        $('#user_id').val(currentUser.id);
        $('#user_name').val(currentUser.name);
        $('#user_email').val(currentUser.email);
        $('#user_phone').val(currentUser.phone || '');

        // Chuyển đổi roleId về role value cho select
        var roleValue = '';
        if (currentUser.roleId === 1) roleValue = 'ADMIN';
        else if (currentUser.roleId === 2) roleValue = 'SELLER';
        else if (currentUser.roleId === 3) roleValue = 'BUYER';
        $('#user_role').val(roleValue);

        // Thay đổi title modal và ẩn password field khi edit (chỉ update thông tin)
        $('#modal_title').text('Edit User Information');
        $('#password_section').hide(); // Ẩn hoàn toàn password field
        $('#user_password').removeAttr('required');

        // Mở modal
        document.getElementById('dialog_user_form').showModal();
    });

    // Xử lý sự kiện cho nút Delete
    $(document).on('click', '[id^="btn-delete-"]', function () {
        var userId = $(this).attr('id').split('-')[2];
        var userName = $(this).closest('tr').find('td:nth-child(3) div').text();

        // Hiển thị dialog xác nhận
        if (confirm(`Bạn có chắc chắn muốn xóa user "${userName}" (ID: ${userId}) không?`)) {
            // Gọi API xóa user theo ID
            $.ajax({
                method: "DELETE",
                url: `http://localhost:8080/admin/deleteUser/${userId}`,
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem('token')
                },
                contentType: "application/json"
            })
                .done(function (response) {
                    // Xóa thành công
                    showMessage('Xóa user thành công!', 'success');
                    // Xóa dòng khỏi bảng mà không cần reload
                    $(`#btn-delete-${userId}`).closest('tr').fadeOut(500, function () {
                        $(this).remove();
                    });
                })
                .fail(function (xhr, status, error) {
                    // Xử lý lỗi
                    var errorMessage = 'Có lỗi xảy ra khi xóa user!';
                    try {
                        if (xhr.responseJSON && xhr.responseJSON.message) {
                            errorMessage = xhr.responseJSON.message;
                        } else if (xhr.responseText) {
                            errorMessage = xhr.responseText;
                        }
                    } catch (e) {
                        // Giữ nguyên errorMessage mặc định
                    }
                    showMessage(errorMessage, 'error');
                });
        }
    });

    // Xử lý submit form user (cho cả add và edit)
    $('#userForm').on('submit', function(e) {
        e.preventDefault();
        
        var userId = $('#user_id').val();
        var isEdit = userId && userId !== '';
        
        // Validate các trường bắt buộc
        var name = $('#user_name').val().trim();
        var email = $('#user_email').val().trim();
        var phone = $('#user_phone').val().trim();
        var roleValue = $('#user_role').val();
        var password = $('#user_password').val().trim();
        
        if (!name || !email || !roleValue) {
            showMessage('Vui lòng điền đầy đủ thông tin bắt buộc!', 'error');
            return;
        }
        
        // Validate password cho add user
        if (!isEdit && (!password || password === '')) {
            showMessage('Vui lòng nhập password khi thêm user mới!', 'error');
            return;
        }
        
        if (isEdit) {
            // Xử lý Edit User
            var roleId;
            if (roleValue === 'ADMIN') roleId = 1;
            else if (roleValue === 'SELLER') roleId = 2;
            else if (roleValue === 'BUYER') roleId = 3;
            else roleId = null;
            
            var updateData = {
                id: parseInt(userId),
                name: name,
                email: email,
                phone: phone,
                roleId: roleId
            };
            
            $.ajax({
                method: "PUT",
                url: "http://localhost:8080/admin/updateUser",
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem('token')
                },
                contentType: "application/json",
                data: JSON.stringify(updateData)
            })
            .done(function(response) {
                showMessage('Cập nhật user thành công!', 'success');
                closeUserModal();
                setTimeout(function() {
                    location.reload();
                }, 1500);
            })
            .fail(function(xhr, status, error) {
                var errorMessage = 'Có lỗi xảy ra khi cập nhật user!';
                try {
                    if (xhr.responseText) {
                        errorMessage = xhr.responseText;
                    }
                } catch (e) {
                    // Giữ nguyên errorMessage mặc định
                }
                showMessage(errorMessage, 'error');
            });
            
        } else {
            // Xử lý Add User - chỉ tạo buyer với RegisterRequest format
            var addData = {
                name: name,
                email: email,
                password: password,
                phone: phone
            };
            
            $.ajax({
                method: "POST",
                url: "http://localhost:8080/admin/addUser",
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem('token')
                },
                contentType: "application/json",
                data: JSON.stringify(addData)
            })
            .done(function(response) {
                showMessage('Thêm buyer thành công!', 'success');
                closeUserModal();
                setTimeout(function() {
                    location.reload();
                }, 1500);
            })
            .fail(function(xhr, status, error) {
                var errorMessage = 'Có lỗi xảy ra khi thêm buyer!';
                try {
                    if (xhr.responseText) {
                        errorMessage = xhr.responseText;
                    }
                } catch (e) {
                    // Giữ nguyên errorMessage mặc định
                }
                showMessage(errorMessage, 'error');
            });
        }
    });

    // Helper function để hiển thị thông báo
    function showMessage(message, type) {
        // Xóa các thông báo cũ
        $('.alert').remove();

        var alertClass;
        if (type === 'success') {
            alertClass = 'alert-success';
        } else if (type === 'warning') {
            alertClass = 'alert-warning';
        } else {
            alertClass = 'alert-danger';
        }

        var alertHtml = '<div class="alert ' + alertClass + ' alert-dismissible fade show mt-3" role="alert">' +
            message +
            '<button type="button" class="btn-close" data-bs-dismiss="alert"></button>' +
            '</div>';

        // Tìm vị trí để hiển thị thông báo
        var target = $('#users-table').parent();
        if (target.length === 0) {
            target = $('body');
        }
        target.prepend(alertHtml);

        // Tự động ẩn sau 5 giây
        setTimeout(function () {
            $('.alert').fadeOut();
        }, 5000);
    }

});

// Các function global được gọi từ HTML
function openAddUserModal() {
    // Reset form
    $('#userForm')[0].reset();
    $('#user_id').val('');
    
    // Cấu hình modal cho add user (chỉ tạo buyer)
    $('#modal_title').text('Add New Buyer');
    $('#password_section').show();
    $('#user_password').attr('required', 'required');
    $('#user_password').attr('placeholder', 'Enter password');
    
    // Ẩn role selection vì chỉ tạo được buyer
    $('#user_role').closest('.mb-3').hide();
    $('#user_role').val('BUYER'); // Set mặc định là buyer
    
    // Mở modal
    document.getElementById('dialog_user_form').showModal();
}

function closeUserModal() {
    document.getElementById('dialog_user_form').close();
    $('#userForm')[0].reset();
    $('#password_section').show(); // Hiện lại password section để tránh lỗi UI
    $('#user_role').closest('.mb-3').show(); // Hiện lại role selection để tránh lỗi UI
}

// Function để mở dialog (được sử dụng trong HTML)
function open_dialog(dialogId) {
    document.getElementById(dialogId).showModal();
}