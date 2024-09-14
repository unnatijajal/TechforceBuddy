document.getElementById('registerForm')
	.addEventListener('submit', function(event) {
		event.preventDefault();// prevent form submitting the default way

		
		// Collect form data
		const firstName = document.getElementById('firstName').value;
		const lastName = document.getElementById('lastName').value;
		const password = document.getElementById('password').value;
		const email = document.getElementById('email').value;

		// Prepare data to send
		const userData = {
			firstName: firstName,
			lastName: lastName,
			email: email,
			password: password,
			role:'ROLE_USER'
		};

		//send the post request
		fetch('http://localhost:8082/signin', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
			},
			body: JSON.stringify(userData),     // convert form data into json format
		}) //URL for register API
		.then(response => response.json)
		.then(data => {
		        // Redirect to another page if needed
		        window.location.href = "/login";
			  // console.log('saved');
		 })
		.catch(error => {
			console.error(error);
		});

	});