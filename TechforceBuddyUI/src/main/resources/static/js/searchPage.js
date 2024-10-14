document.getElementById('logout').addEventListener('click', function() {
	localStorage.removeItem('token');
	const token = localStorage.getItem('token');
	if (token == null) {
		window.location.href = '/login';
	}
});

document.getElementById('searchVersionOne').addEventListener('click', function(event) {
	event.preventDefault();

	const searchVersionOneButton = document.getElementById('searchVersionOne');
	const searchVersionTwoButton = document.getElementById('searchVersionTwo');
	// Disable the button 
	searchVersionOneButton.disabled = true;
	searchVersionTwoButton.disabled = true;

	const message = document.getElementById('query').value;
	const userData = {
		query: message,
	};
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
					/*resultText += `${key.join('\n')}`;  // Use backticks for template literals
					fileName = `${value}`;  // Store the key as the fileName*/

					const pElement = document.createElement('p');
					pElement.className = 'card-text'; // Set the class name
					pElement.textContent = `${key}`;

					const smallTag = document.createElement('small'); // Create a <small> tag
					smallTag.textContent = `${value}`;


					// Create a new anchor element
					const aElement = document.createElement('a');
					aElement.className = 'card-link'; // Set the class name
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
		})
		.catch(error => {
			const errorElement = document.createElement('p');
			errorElement.textContent = `${error.message}`;
			mainDiv.appendChild(errorElement);
			// Display the error message in the textarea
			console.error('Error:', error);

		}).finally(() => {
			// Re-enable the button and hide the spinner after response
			searchVersionOneButton.disabled = false;
			searchVersionTwoButton.disabled = false;
		});

});


document.getElementById('searchVersionTwo').addEventListener('click', function(event) {
	event.preventDefault();

	const searchVersionOneButton = document.getElementById('searchVersionOne');
	const searchVersionTwoButton = document.getElementById('searchVersionTwo');
	// Disable the button 
	searchVersionOneButton.disabled = true;
	searchVersionTwoButton.disabled = true;

	const message = document.getElementById('query').value;
	const userData = {
		query: message,
	};
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
					aElement.textContent = 'Reference : ' + fileName;
					aElement.addEventListener('click',function(event){
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

					// Append a horizontal rule after each file entry for better separation
					const hrElement = document.createElement('hr');
					mainDiv.appendChild(hrElement);
				}

			}
		})
		.catch(error => {
			const errorElement = document.createElement('p');
			errorElement.textContent = `${error.message}`;
			mainDiv.appendChild(errorElement);
			// Display the error message in the textarea
			console.error('Error:', error);
		}).finally(() => {
			// Re-enable the button and hide the spinner after response
			searchVersionOneButton.disabled = false;
			searchVersionTwoButton.disabled = false;
		});

});


// Function to open PDF in a new tab with authorization
function openPdf(fileName) {
	const token = localStorage.getItem('token');
	const url = `http://localhost:8082/openPdf?fileName=${encodeURIComponent(fileName)}`;

	// Create a new GET request with the Authorization header
	fetch(url, {
		method: 'GET',
		headers: {
			'Authorization': 'Bearer ' + token,
			'Content-Type': 'application/json' // Adjust if necessary
		}
	})
		.then(response => {
			if (response.ok) {
				// If the response is OK, open the PDF in a new tab
				return response.blob();
			} else {
				throw new Error('Failed to fetch PDF: ' + response.statusText);
			}
		})
		.then(blob => {
			const pdfUrl = URL.createObjectURL(blob);
			window.open(pdfUrl, '_blank'); // Open the PDF in a new tab
		})
		.catch(error => {
			console.error('Error:', error);
			alert('Access denied or PDF not found.');
		});
}