
// Example: Replace with actual addresses from backend or localStorage
var shippingAddresses = [
	{
		id: 1,
		name: "John Doe",
		address: "123 Main St, Springfield",
		city: "Springfield",
		zip: "12345",
		country: "USA",
		phone: "+1 555-1234"
	},
	{
		id: 2,
		name: "Jane Smith",
		address: "456 Elm St, Shelbyville",
		city: "Shelbyville",
		zip: "67890",
		country: "USA",
		phone: "+1 555-5678"
	}
];

function renderShippingAddresses() {
	var container = document.getElementById('shipping-address-section');
	if (!shippingAddresses || shippingAddresses.length === 0) {
		container.innerHTML = '';
		return;
	}
	var html = '<div class="card shadow-sm mb-5 p-4">';
	html += '<h5 class="mb-3">Shipping Address</h5>';
	html += '<form id="address-radio-form" class="d-grid gap-3">';
	shippingAddresses.forEach(function(addr, idx) {
		html += '<label class="card-check d-flex p-3 rounded">';
		html += '<input class="form-check-input" name="shipping-address" type="radio" value="' + addr.id + '"' + (idx === 0 ? ' checked' : '') + '>';
		html += '<div class="ml-1">';
		html += '<span>' + addr.name + '</span>';
		html += '<p class="d-block mb-0 text-secondary">' + addr.address + ', ' + addr.city + ', ' + addr.zip + ', ' + addr.country + '<br>Phone: ' + addr.phone + '</p>';
		html += '</div>';
		html += '</label>';
	});
	// Add new address radio option
	html += '<label class="card-check d-flex p-3 rounded">';
	html += '<input class="form-check-input" name="shipping-address" type="radio" value="new-address" id="new-address-radio">';
	html += '<div class="ml-1">';
	html += '<span><strong>Add new address</strong></span>';
	html += '</div>';
	html += '</label>';
	html += '</form>';
	// Add new address form (hidden by default)
	html += '<form id="new-address-form" class="d-grid gap-3 mt-3" style="display:none;">';
	html += '<div><label for="new-name" class="form-label">Full Name</label><input type="text" id="new-name" name="new-name" class="form-control" placeholder="Enter your full name" required></div>';
	html += '<div><label for="new-address" class="form-label">Address</label><input type="text" id="new-address" name="new-address" class="form-control" placeholder="Street address" required></div>';
	html += '<div class="d-flex gap-2">';
	html += '<div class="flex-grow"><label for="new-city" class="form-label">City</label><input type="text" id="new-city" name="new-city" class="form-control" placeholder="City" required></div>';
	html += '<div class="flex-grow"><label for="new-zip" class="form-label">ZIP Code</label><input type="text" id="new-zip" name="new-zip" class="form-control" placeholder="ZIP Code" required></div>';
	html += '</div>';
	html += '<div><label for="new-country" class="form-label">Country</label><input type="text" id="new-country" name="new-country" class="form-control" placeholder="Country" required></div>';
	html += '<div><label for="new-phone" class="form-label">Phone Number</label><input type="tel" id="new-phone" name="new-phone" class="form-control" placeholder="Phone number" required></div>';
	html += '<button type="submit" class="btn btn-primary mt-2">Save Address</button>';
	html += '</form>';
	html += '</div>';
	container.innerHTML = html;
	// Add event listeners for radio and form
	var radioForm = document.getElementById('address-radio-form');
	var newAddressRadio = document.getElementById('new-address-radio');
	var newAddressForm = document.getElementById('new-address-form');
	if (radioForm && newAddressRadio && newAddressForm) {
		radioForm.addEventListener('change', function(e) {
			if (newAddressRadio.checked) {
				newAddressForm.style.display = '';
			} else {
				newAddressForm.style.display = 'none';
			}
		});
		newAddressForm.addEventListener('submit', function(e) {
			e.preventDefault();
			// Collect new address data
			var newAddr = {
				id: Date.now(),
				name: document.getElementById('new-name').value,
				address: document.getElementById('new-address').value,
				city: document.getElementById('new-city').value,
				zip: document.getElementById('new-zip').value,
				country: document.getElementById('new-country').value,
				phone: document.getElementById('new-phone').value
			};
			shippingAddresses.push(newAddr);
			renderShippingAddresses();
		});
	}
}

document.addEventListener('DOMContentLoaded', function() {
	renderShippingAddresses();
});