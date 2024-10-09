document.getElementById('logout').addEventListener('click', function() {
	localStorage.removeItem('token');
	const token = localStorage.getItem('token');
	if (token == null) {
		window.location.href = '/login';
	}
});

document.getElementById('search').addEventListener('click', function(event) {
	event.preventDefault();

	const searchButton = document.getElementById('search');
	// Disable the button and show the spinner
	searchButton.disabled = true;

	const message = document.getElementById('query').value;
	const userData = {
		query: message,
	};
	fetch('http://localhost:8082/query', {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
			'Authorization': 'Bearer ' + localStorage.getItem('token'),
		},
		body: JSON.stringify(userData),
	})
		.then(response => {
			// Check if the response status is OK (status code 200)
			if (!response.ok) {
				// If not OK, throw an error to be caught in the catch block
				return response.text().then(errMessage => {
					throw new Error(errMessage);
				});
			}
			// If the response is OK, parse the JSON
			return response.json();
		})
		.then(data => {
			let resultText = '';
			let fileName = '';

			// Log the parsed data to check the structure
			console.log('Response data:', data);

			// Check if 'data' is an object
			if (typeof data === 'object' && data !== null) {
				// Iterate over the map (assuming it's a JSON object where each key is a string and value is a list)
				for (const [key, value] of Object.entries(data)) {
					resultText += `${value.join('\n')}`;  // Use backticks for template literals
					fileName = `${key}`;  // Store the key as the fileName
				}

				// If there was something in the loop, display it in the textarea
				document.getElementById('responseTextArea').value = resultText;
				document.getElementById('fileName').textContent = 'For more information read\n'
					+ fileName + ' file.';  // Use textContent for <p> tag
			}
		})
		.catch(error => {
			// Display the error message in the textarea
			console.error('Error:', error);
			document.getElementById('responseTextArea').value = `${error.message}`;
		}).finally(() => {
			// Re-enable the button and hide the spinner after response
			searchButton.disabled = false;
		});

});