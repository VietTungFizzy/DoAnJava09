
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

// Global variables for cart data
let currentOrderItems = [];
let currentProducts = [];
let currentOrderId = null;

// Stripe integration
const stripePublicKey = "pk_test_51S9SX3L9RJOfTfprzim07Jef7DOY7AS32iqxeLfFaiJQ5lOoFZBeXJTkSBY4EHtLVjSGVwj84puuYVTvsDKUq0Nu00PRp9BlRQ";
let stripe = null;

function renderCart(orderItems, products) {
    const container = document.getElementById('cart-items');
    if (!container) return;

    // Remove existing articles
    const articles = container.querySelectorAll('article');
    articles.forEach(a => a.remove());

    // Update header
    const headerP = document.querySelector('.py-8 p.text-secondary');
    if (headerP) {
        headerP.textContent = `${orderItems.length} Products in Your cart`;
    }

    // Generate new HTML
    let html = '';
    orderItems.forEach((item, index) => {
        const product = products[index] || {};
        const image = product.images && product.images[0] ? product.images[0] : 'images/placeholder.jpg';
        const name = product.name || 'Unknown Product';
        const itemTotal = (item.price * item.quantity).toFixed(2);
        const quantity = item.quantity;

        html += `
        <article class="d-flex max-sm:flex-wrap gap-2 mb-5 pb-5 border-bottom">
            <div class="mr-2 flex-shrink-0 w-25 h-25 bg-neutral-200 rounded overflow-hidden">
                <img src="${image}" class="ratio-1x1 h-full rounded">
            </div>
            <div class="flex flex-grow py-1">
                <h5 class="mb-1 font-medium text-lg">
                    <a href="#" class="text-dark">${name}</a>
                </h5>
                <p class="text-secondary">
                    Quantity: ${quantity} <br>
                    Price per item: $${item.price.toFixed(2)} <br>
                </p>
            </div>
            <div class="py-1 max-sm:w-full max-sm:d-flex max-sm:align-items-center">
                <p class="sm:mb-4 sm:text-right max-sm:mr-10 max-sm:flex-grow"> <strong>$${itemTotal}</strong> </p>
                <div class="d-flex gap-2">
                    <select class="form-select" name="" id="">
                        ${Array.from({length: 10}, (_, i) => `<option value="${i+1}" ${quantity == i+1 ? 'selected' : ''}>${i+1}</option>`).join('')}
                    </select>
                    <button class="btn btn-neutral btn-icon">
                        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M12 5v14M5 12h14"/>
                        </svg>
                    </button>
                    <button class="btn btn-neutral btn-icon">
                        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/>
                        </svg>
                    </button>
                </div>
            </div>
        </article>
        <hr>
        `;
    });

    // Insert before the form
    const form = container.querySelector('form');
    if (form) {
        form.insertAdjacentHTML('beforebegin', html);
    }

    // Update summary
    const itemsLi = container.querySelector('li label');
    if (itemsLi) {
        itemsLi.textContent = `${orderItems.length} items:`;
    }
    const subtotalVar = container.querySelector('li var');
    if (subtotalVar) {
        const subtotal = orderItems.reduce((sum, item) => sum + item.price * item.quantity, 0);
        subtotalVar.textContent = `$${subtotal.toFixed(2)}`;
    }
    const totalDd = container.querySelector('dl dd');
    if (totalDd) {
        const total = orderItems.reduce((sum, item) => sum + item.price * item.quantity, 0);
        totalDd.textContent = `$${total.toFixed(2)}`;
    }
}

function renderSimilarProducts(relatedProducts) {
    const container = document.getElementById('similar-products');
    if (!container) return;
    let html = '';
    relatedProducts.forEach(product => {
        const image = product.images && product.images[0] ? product.images[0] : 'images/placeholder.jpg';
        const name = product.name || 'Unknown';
        const price = product.price ? `$${product.price.toFixed(2)}` : '$0.00';
        const rating = product.rating || 4.5;
        const reviewCount = product.reviewCount || 12;
        const stars = '⭐'.repeat(Math.floor(rating)) + (rating % 1 ? '⭐' : '');
        html += `
        <div>
            <figure class="relative">
                <button class="absolute top-2 right-2 btn btn-icon btn-default">
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-heart-icon lucide-heart"><path d="M19 14c1.49-1.46 3-3.21 3-5.5A5.5 5.5 0 0 0 16.5 3c-1.76 0-3 .5-4.5 2-1.5-1.5-2.74-2-4.5-2A5.5 5.5 0 0 0 2 8.5c0 2.3 1.5 4.05 3 5.5l7 7Z"></path></svg>
                </button>
                <a href="product-detail.html?id=${product.id}" class="d-block overflow-hidden rounded bg-neutral-200 h-80 max-sm:h-60"> 
                    <img src="${image}" height="320" class="w-full h-full"> 
                </a>
                <figcaption class="py-3 d-grid gap-1">
                    <p class="mb-1 h-10 overflow-hidden"> 
                        <a href="product-detail.html?id=${product.id}" class="text-initial hover:text-primary text-decoration-none">
                        ${name}
                        </a> 
                    </p>
                    <div class="d-flex align-items-center gap-2 mb-1">
                        <span class="text-orange"> ${stars}  ${rating}  </span>
                        <span class="text-secondary">(${reviewCount} orders)</span>
                    </div>
                    <p class="mb-2">
                        <strong class="text-lg text-primary"> ${price} </strong> 
                    </p>
                    <button class="btn btn-neutral lg:w-full"> 
                        <svg width="22" height="22" viewBox="0 0 21 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M18.3769 9.58333L18.6089 8.19985C18.7605 7.29524 18.8363 6.84293 18.5935 6.54647C18.3507 6.25 17.9044 6.25 17.0119 6.25L4.65466 6.25C3.76214 6.25 3.31588 6.25 3.07307 6.54647C2.83026 6.84293 2.90609 7.29524 3.05773 8.19985L4.0607 14.1829C4.39314 16.1659 4.55935 17.1574 5.238 17.7454C5.91665 18.3333 6.89492 18.3333 8.85147 18.3333H10.8333" stroke="#14181F" stroke-width="1.5" stroke-linecap="round"/>
                            <path d="M12.4999 14.9998L19.1666 14.9998M15.8333 18.3332L15.8333 11.6665" stroke="#14181F" stroke-width="1.5" stroke-linecap="round"/>
                            <path d="M15.4166 6.24984C15.4166 3.71853 13.3646 1.6665 10.8333 1.6665C8.30197 1.6665 6.24994 3.71853 6.24994 6.24984" stroke="#14181F" stroke-width="1.5"/>
                        </svg>
                        Add to cart
                    </button>
                </figcaption>
            </figure>  
        </div>
        `;
    });
    container.innerHTML = html;
}

function getCartItems() {
    return currentOrderItems;
}

function getSelectedShippingAddress() {
    const selected = document.querySelector('input[name="shipping-address"]:checked');
    if (!selected || selected.value === 'new-address') return null;
    return shippingAddresses.find(addr => addr.id == selected.value);
}

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
	const token = localStorage.getItem('token');
	if (!token) {
		window.location.href = 'page-login.html';
		return;
	}
	fetch(window.AppConfig.getApiUrl('/orders/pending'), {
		headers: {
			'Authorization': `Bearer ${token}`
		}
	})
		.then(response => response.json())
		.then(data => {
			console.log('Fetched pending orders:', data);

			if(data.error) {
				alert("There is no pending order found");
				window.location.href = 'homepage.html';
			}

			currentOrderId = data.id;
			if (data && data.orderItems) {
				const orderItems = data.orderItems;
				const productPromises = orderItems.map(item => 
					fetch(window.AppConfig.getApiUrl('/products/' + item.productId), {
						headers: {
							'Authorization': `Bearer ${token}`
						}
					}).then(res => res.json())
				);
				Promise.all(productPromises).then(products => {
					console.log('Fetched products:', products);
					renderCart(orderItems, products);
					currentOrderItems = orderItems;
					currentProducts = products;
				}).catch(error => console.error('Error fetching products:', error));
				// Fetch related products for a random order item
				if (orderItems.length > 0) {
					const randomIndex = Math.floor(Math.random() * orderItems.length);
					const randomProductId = orderItems[randomIndex].productId;
					fetch(window.AppConfig.getApiUrl('/products/' + randomProductId + '/related?limit=4'), {
						headers: {
							'Authorization': `Bearer ${token}`
						}
					}).then(res => res.ok ? res.json() : []).then(relatedProducts => {
						renderSimilarProducts(relatedProducts);
					}).catch(error => console.error('Error fetching related products:', error));
				}
			}
		})
		.catch(error => {
			alert('Something is wrong. Please check again');
			window.location.href = 'homepage.html';
		});

	// renderShippingAddresses();
	// Set delivery time to 1 week from now
	const deliveryDate = new Date();
	deliveryDate.setDate(deliveryDate.getDate() + 7);
	const options = { weekday: 'short', year: 'numeric', month: 'short', day: 'numeric' };
	const formattedDate = deliveryDate.toLocaleDateString('en-US', options);
	document.getElementById('time-delivery').innerHTML = `Delivered by <br> ${formattedDate}`;
	
	// Payment method radio logic
	// const paymentRadios = document.getElementsByName('payment-type');
	// const stripeForm = document.getElementById('stripe-card-form');
	
	// function updateStripeFormVisibility() {
	// 	const selected = Array.from(paymentRadios).find(r => r.checked);
	// 	if (selected && selected.value === 'credit-card') {
	// 		stripeForm.style.display = '';
	// 	} else {
	// 		stripeForm.style.display = 'none';
	// 	}
	// }
	
	// paymentRadios.forEach(radio => {
	// 	radio.addEventListener('change', updateStripeFormVisibility);
	// });
	// updateStripeFormVisibility();
	// Initialize Stripe
	stripe = Stripe(stripePublicKey);
	// Handle Stripe Checkout button click
	const checkoutBtn = document.getElementById('checkout-button');
	const messageDiv = document.getElementById('payment-message');
	
	if (checkoutBtn) {
		checkoutBtn.addEventListener('click', async function(e) {
			e.preventDefault();
			
			const selectedPayment = document.querySelector('input[name="payment-type"]:checked');
			if (!selectedPayment || selectedPayment.value !== 'credit-card') {
				// For other payment methods, update order status to processing
				try {
					const response = await fetch(`http://localhost:8080/api/orders/${currentOrderId}/status`, {
						method: 'PUT',
						headers: {
							'Content-Type': 'application/json',
							'Authorization': `Bearer ${token}`
						},
						body: JSON.stringify({ status: 'processing' })
					});
					if (!response.ok) {
						throw new Error('Failed to update order status');
					}
					alert('Đơn hàng của bạn đang được xử lý');
					window.location.href = 'homepage.html';
				} catch (error) {
					console.error('Error updating order status:', error);
					alert('Có lỗi xảy ra khi xử lý đơn hàng. Vui lòng thử lại.');
				}
				return;
			}
			
			// Show loading state
			checkoutBtn.disabled = true;
			checkoutBtn.innerHTML = 'Creating checkout session...';
			messageDiv.classList.add('hidden');
			
			try {
				// Create checkout session on your backend
				const response = await fetch('http://localhost:8080/api/checkout/create-session', {
					method: 'POST',
					headers: {
						'Content-Type': 'application/json',
					},
					body: JSON.stringify({
						orderId: currentOrderId,
						successUrl: window.location.origin + '/payment-success.html?orderId='+currentOrderId,
						cancelUrl: window.location.origin + '/page-cart.html'
					}),
				});
				if (!response.ok) {
					throw new Error('Failed to create checkout session');
				}
				const session = await response.json();
				
				// Redirect to Stripe Checkout
				const result = await stripe.redirectToCheckout({
					sessionId: session.id
				});
				if (result.error) {
					throw new Error(result.error.message);
				}
			} catch (error) {
				console.error('Error:', error);
				messageDiv.textContent = error.message || 'An error occurred. Please try again.';
				messageDiv.classList.remove('hidden');
				messageDiv.className = 'mt-3 text-danger';
			} finally {
				// Reset button state
				checkoutBtn.disabled = false;
				checkoutBtn.innerHTML = `
					<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="mr-2">
						<rect x="1" y="4" width="22" height="16" rx="2" ry="2"/>
						<line x1="1" y1="10" x2="23" y2="10"/>
					</svg>
					Pay with Stripe Checkout
				`;
			}
		});
	}
});