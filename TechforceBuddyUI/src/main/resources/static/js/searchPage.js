document.getElementById('logout').addEventListener('click', function() {
	localStorage.removeItem('token');
	const token = localStorage.getItem('token');
	if (token == null) {
		window.location.href = '/auth/login';
	}
});





document.getElementById('searchQuery').addEventListener('click', function(event) {
	event.preventDefault();
	const version = document.getElementById('version').value;
	const loader = document.getElementById('load');
	const searchButton = document.getElementById('searchQuery');
	searchButton.style.visibility = 'hidden';
	const message = document.getElementById('query').value;
	const userData = {
		query: message,
	};
	loader.style.visibility = 'visible';

	if (version === 'v1') {
		fetch('http://localhost:8083/v1/query-and-summary', {
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
				return response.json(); // Change to json() to parse the JSON response
			})
			.then(data => {
				// Accessing summary and structuredContent
				const summary = data.summary;
				const unstructuredContent = data.unstructuredContent;

				// Create summary and structured content sections
				const summaryDiv = document.getElementById('summaryContent');
				const detailsDiv = document.getElementById('detailsContent');
				summaryDiv.innerHTML = ''; // Clear the main div if needed
				detailsDiv.innerHTML = '';

				// Create a heading for the summary
				const headingElement = document.createElement('h4');
				headingElement.textContent = 'Summary:'; // Set the heading text

				// Create a paragraph for the summary text
				const pElement = document.createElement('p');
				pElement.innerHTML = summary; // Set the summary text

				// Append the heading and paragraph to the summary div
				summaryDiv.appendChild(headingElement);
				summaryDiv.appendChild(pElement);

				// Process unstructured content and display it
				
				for (const [key, value] of Object.entries(unstructuredContent)) {
					const pElement = document.createElement('p');
					pElement.className = 'card-text'; // Set the class name
					pElement.textContent = `${key}`;

					const smallTag = document.createElement('small'); // Create a <small> tag
					smallTag.textContent = 'Reference : '+`${value}`;


					// Create a new anchor element
					const aElement = document.createElement('a');
					aElement.className = 'card-link';
					aElement.className = 'a';

					aElement.addEventListener('click', function(event) {
						event.preventDefault();
						openPdf(`${value}`);
					}) // Set the class name
					aElement.appendChild(smallTag); // Set the link text


					// Append the paragraph and anchor to the main div
					detailsDiv.appendChild(pElement);
					detailsDiv.appendChild(aElement);

					// Create and append <br> tag
					const brElement = document.createElement('br');
					detailsDiv.appendChild(brElement);

					// Create and append <hr> tag
					const hrElement = document.createElement('hr');
					detailsDiv.appendChild(hrElement);
				}

				// Show the response card
				document.getElementById('accordion').style.visibility = 'visible';
			})
			.catch(error => {
				console.error('Error:', error);
			})
			.finally(() => {
				loader.style.visibility = 'hidden';
				searchButton.style.visibility = 'visible';
			});

	} else if (version === 'v2') {
		fetch('http://localhost:8083/v2/query-and-summary', {
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
				return response.json(); // Change to json() to parse the JSON response
			})
			.then(data => {
				// Accessing summary and structuredContent
				const summary = data.summary;
				const structuredContent = data.structuredContent;

				// Create summary and structured content sections
				const summaryDiv = document.getElementById('summaryContent');
				const detailsDiv = document.getElementById('detailsContent');
				summaryDiv.innerHTML = ''; // Clear the main div if needed
				detailsDiv.innerHTML = '';

				// Create a heading for the summary
				const headingElement = document.createElement('h4');
				headingElement.textContent = 'Summary:'; // Set the heading text

				// Create a paragraph for the summary text
				const pElement = document.createElement('p');
				pElement.innerHTML = summary; // Set the summary text

				// Append the heading and paragraph to the summary div
				summaryDiv.appendChild(headingElement);
				summaryDiv.appendChild(pElement);


				for (const [fileName, sections] of Object.entries(structuredContent)) {
					// Create a new anchor for the file name
					const aElement = document.createElement('a');
					aElement.className = 'card-link';
					aElement.className = 'a';
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
						detailsDiv.appendChild(pElement);
						detailsDiv.appendChild(aElement);


					});

					// Create and append <hr> tag
					const hrElement = document.createElement('hr');
					detailsDiv.appendChild(hrElement);

				}

				// Show the response card
				document.getElementById('accordion').style.visibility = 'visible';
			})
			.catch(error => {
				console.error('Error:', error);
			})
			.finally(() => {
				loader.style.visibility = 'hidden';
				searchButton.style.visibility = 'visible';
			});

	}
});



// Function to open PDF in a new tab with authorization
function openPdf(fileName) {
	// Redirect to pdfViewer.html with the file name as a query parameter
	window.open(`/pdfViewer?file=${encodeURIComponent(fileName)}`, '_blank');
}

