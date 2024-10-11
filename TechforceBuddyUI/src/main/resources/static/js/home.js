window.onload = function() {
	const token = localStorage.getItem('token');
	if (token == null) {
		window.location.href = '/login';
	}
}

document.getElementById('logout').addEventListener('click', function() {
	localStorage.removeItem('token');
	const token = localStorage.getItem('token');
	if (token == null) {
		window.location.href = '/login';
	}
});

