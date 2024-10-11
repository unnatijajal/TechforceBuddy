
// Get the token from the local storage
const token = localStorage.getItem('token');
if(token!=null){

	// Spilt the jwt token 
	const tokenParts = token.split('.');

	// Fetch the first part
	const payload = tokenParts[1];

	// atob is ASCII to binary.
	//Used Base64 encoding to convert binary data. 
	//And Base64 encoding transforms the binary data into a string of ASCII characters
	const decodedPayload = atob(payload);

	// JSON.parse will parse the payload into json object.
	const decoded = JSON.parse(decodedPayload);

	// Get the exiration time from the json object
	const expirationTime = decoded.exp ;
	
	// This is function check whether the jwt token is valid or not.
	function validateToken(){
		// Get current time in milliseconds
		const currentTime = Math.floor(Date.now() / 1000);
		
		 /* Condition is check if current time is greater than the 
		  *	Expired time then token is expired so it will redirect 
		  *	on login page.
		  */
		if (currentTime > expirationTime) {
			// Token is expired so remove it from local storage.
			localStorage.removeItem('token')
			// Set message local storage for session expiration 
			localStorage.setItem('tokenExpiredMessage','Your session has expired. Please log in again.');
			// Redirect on login page.
			window.location.href='/login';
		}
	}

	// setInterval() call the validateToken after every 5 sec to check whether token is valid or not.
	setInterval(validateToken,5000);	
}else{
	window.location.href='/login';
}


