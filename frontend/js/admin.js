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
		var html = "";
         var roleName ="";
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
				<td>#${String(user.id).padStart(3, '0')}</td>
				<td><img src="images/no-avatar.png" alt="${user.name}" class="rounded-circle" width="40" height="40"></td>
				<td><div class="fw-medium">${user.name}</div></td>
				<td>${user.email}</td>
				<td>${user.phone || '-'}</td>
				<td><span class="badge bg-success"> ${roleName}</span></td>
				<td>${user.createdAt ? (new Date(user.createdAt)).toLocaleDateString('en-GB') : '-'}</td>
				<td class="text-center">
					<div class="btn-group btn-group-sm">
						<button class="btn btn-outline-primary" title="Edit">Sửa</button>
						<button class="btn btn-outline-danger" title="Delete">Xóa</button>
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
});
