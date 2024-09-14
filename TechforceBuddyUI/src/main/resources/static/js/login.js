window.onload = function(){
	const expiredMessage =localStorage.getItem('tokenExpiredMessage');
	if(expiredMessage != null){
		document.getElementById('error').innerHTML = expiredMessage;
		localStorage.removeItem('tokenExpiredMessage');
	}
}

document.getElementById('loginForm').addEventListener('submit', function(event) {
    event.preventDefault(); // Prevent the default form submission

    const username = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    fetch('http://localhost:8082/login', { // Replace with your backend login endpoint
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: new URLSearchParams({
            email: username,
            password: password
        })
    })
    .then(response => {
        if (response.ok) {
			return response.json();
        } else {
			document.getElementById('error').innerHTML ='Username or Password is invalid';
            
        }
    })
	.then(data=>{
		// Store the token in local storage or a variable
		localStorage.setItem('token', data.token);
		// Redirect upon successful login
		window.location.href = '/chatbot';
	})
    .catch(error => {
		localStorage.removeItem('token');
        console.error('Error:', error);
    });
});
