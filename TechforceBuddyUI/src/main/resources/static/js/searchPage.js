document.getElementById('logout').addEventListener('click', function() {
	localStorage.removeItem('token');
	const token = localStorage.getItem('token');
	if (token == null) {
		window.location.href = '/login';
	}
});



document.getElementById('searchQuery').addEventListener('click', function(event) {
	event.preventDefault();
	let api = '';
	const version = document.getElementById('version').value;
	const loader = document.getElementById('load');
	const searchButton = document.getElementById('searchQuery');
	searchButton.style.visibility = 'hidden';
	const message = document.getElementById('query').value;
	const userData = {
		query: message,
	};
	loader.style.visibility = 'visible';

	if (version == 'v1') {
		api = 'http://localhost:8082/v1/generate-summary';
	} else if (version == 'v2') {
		api = 'http://localhost:8082/v2/generate-summary';
	}
	fetch(api, { // Changed endpoint to match your Spring Boot API
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
			'Authorization': 'Bearer ' + localStorage.getItem('token'),
		},
		body: JSON.stringify(userData),
	})
		.then(response => {
			if (!response.ok) {
				return response.text().then(errMessage => {
					throw new Error(errMessage);
				});
			}
			return response.text(); // Changed from json() to text() since the response is a plain string
		})
		.then(summaryText => {
			const mainDiv = document.getElementById('responseDiv');
			mainDiv.innerHTML = ''; // Clear the main div if needed

			const buttonDiv = document.getElementById('button');
			buttonDiv.innerHTML = ''; // Clear the main div if needed

			// Create a heading for the summary
			const headingElement = document.createElement('h4'); // You can use h2 or h1 based on your styling preference
			headingElement.textContent = 'Summary:'; // Set the heading text

			// Create a paragraph for the summary text
			const pElement = document.createElement('p');
			pElement.className = 'card-text'; // Set the class name
			pElement.textContent = summaryText; // Directly set the summary text

			const buttonElement = document.createElement('button')
			buttonElement.textContent = 'Read more deails';
			buttonElement.className = 'btn btn-primary';

			buttonElement.addEventListener('click', function(event) {
				event.preventDefault();

				getDetails(version);

			});

			// Append the heading and paragraph to the main div
			mainDiv.appendChild(headingElement);
			mainDiv.appendChild(pElement);
			buttonDiv.appendChild(buttonElement);
			// Show the response card
			document.getElementById('responseCard').style.visibility = 'visible';
		})
		.catch(error => {
			const errorElement = document.createElement('p');
			errorElement.textContent = `${error.message}`;
			mainDiv.appendChild(errorElement);
			console.error('Error:', error);
		}).finally(() => {
			loader.style.visibility = 'hidden';
			searchButton.style.visibility = 'visible';
		});


});



// Function to open PDF in a new tab with authorization
function openPdf(fileName) {
	// Redirect to pdfViewer.html with the file name as a query parameter
	window.open(`/pdfViewer?file=${encodeURIComponent(fileName)}`, '_blank');
}

// Function to get more details of summary
function getDetails(v) {
	const version = document.getElementById('version').value;
	const loader = document.getElementById('load');
	const searchButton = document.getElementById('searchQuery');

	const message = document.getElementById('query').value;
	const userData = {
		query: message,
	};
	loader.style.visibility = 'visible';

	searchButton.style.visibility = 'hidden';
	if (v == 'v1') {

		fetch('http://localhost:8082/v1/query', {
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
				// Main div reference
				const mainDiv = document.getElementById('responseDiv');

				// Clear the main div if needed
				mainDiv.innerHTML = '';
				// Log the parsed data to check the structure

				// Check if 'data' is an object
				if (typeof data === 'object' && data !== null) {

					// Iterate over the map (assuming it's a JSON object where each key is a string and value is a list)
					for (const [key, value] of Object.entries(data)) {

						const pElement = document.createElement('p');
						pElement.className = 'card-text'; // Set the class name
						pElement.textContent = `${key}`;

						const smallTag = document.createElement('small'); // Create a <small> tag
						smallTag.textContent = `${value}`;


						// Create a new anchor element
						const aElement = document.createElement('a');
						aElement.className = 'card-link';
						aElement.style.cursor = 'pointer';

						aElement.addEventListener('click', function(event) {
							event.preventDefault();
							openPdf(`${value}`);
						}) // Set the class name
						aElement.appendChild(smallTag); // Set the link text


						// Append the paragraph and anchor to the main div
						mainDiv.appendChild(pElement);
						mainDiv.appendChild(aElement);

						// Create and append <br> tag
						const brElement = document.createElement('br');
						mainDiv.appendChild(brElement);

						// Create and append <hr> tag
						const hrElement = document.createElement('hr');
						mainDiv.appendChild(hrElement);
					}

				}
				document.getElementById('responseCard').style.visibility = 'visible';
			})
			.catch(error => {
				const errorElement = document.createElement('p');
				errorElement.textContent = `${error.message}`;
				mainDiv.appendChild(errorElement);
				// Display the error message in the textarea
				console.error('Error:', error);

			}).finally(() => {
				loader.style.visibility = 'hidden';
				searchButton.style.visibility = 'visible';
			});
	} else if (v == 'v2') {
		fetch('http://localhost:8082/v2/query', {
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
				// Main div reference
				const mainDiv = document.getElementById('responseDiv');

				// Clear the main div if needed
				mainDiv.innerHTML = '';
				// Log the parsed data to check the structure

				// Check if 'data' is an object
				if (typeof data === 'object' && data !== null) {

					for (const [fileName, sections] of Object.entries(data)) {
						// Create a new anchor for the file name
						const aElement = document.createElement('a');
						aElement.className = 'card-link';
						aElement.style.cursor = 'pointer';
						aElement.textContent = 'Reference : ' + fileName;
						aElement.addEventListener('click', function(event) {
							event.preventDefault();
							openPdf(fileName);
						})


						// Iterate over the sections list and add each section in a <p> tag
						sections.forEach(section => {
							// Create a paragraph tag for each section
							const pElement = document.createElement('p');
							pElement.className = 'card-text';
							pElement.innerHTML = section.replace(/\n/g, '<br>');

							// Append the paragraph to the main div
							mainDiv.appendChild(pElement);
							mainDiv.appendChild(aElement);


						});

						// Create and append <hr> tag
						const hrElement = document.createElement('hr');
						mainDiv.appendChild(hrElement);

					}

				}
				document.getElementById('responseCard').style.visibility = 'visible';
			})
			.catch(error => {
				const errorElement = document.createElement('p');
				errorElement.textContent = `${error.message}`;
				mainDiv.appendChild(errorElement);
				// Display the error message in the textarea
				console.error('Error:', error);
			}).finally(() => {
				loader.style.visibility = 'hidden';
				searchButton.style.visibility = 'visible';
			});
	}
}
