// Function to get the query parameter value from the URL
function getQueryParam(param) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(param);
}

window.addEventListener("load", function() {
	// Get the file name from the query parameter
	const fileName = getQueryParam('file');

	// Retrieve the token from local storage (declare once)
	let token = localStorage.getItem('token'); // Using let here ensures no redeclaration error
	console.log(token);
	if (fileName) {
	    // Proceed with fetching the PDF if the file name is present
	    fetch(`http://localhost:8082/download/${encodeURIComponent(fileName)}`, {
	        method: 'GET',
	        headers: {
	            'Authorization': 'Bearer ' + token,
	            'Content-Type': 'application/json',
	        },
	    })
	    .then(response => {
	        if (response.ok) {
	            return response.blob(); // Get the PDF as a blob
	        } else {
	            throw new Error('Failed to download PDF');
	        }
	    })
	    .then(blob => {
	        // Create a blob URL and set it to the iframe
	        const blobUrl = URL.createObjectURL(blob);
	        document.getElementById('pdfIframe').src = blobUrl;
	    })
	    .catch(error => console.error('Error:', error));
	} else {
	    console.error('File name is missing in the URL');
	}
});



