// Detect browser / device name from user agent
function getDeviceName() {
    const ua = navigator.userAgent;
    if (/Edg\//.test(ua))    return 'Microsoft Edge';
    if (/OPR\//.test(ua))    return 'Opera';
    if (/Chrome\//.test(ua)) return 'Chrome';
    if (/Firefox\//.test(ua))return 'Firefox';
    if (/Safari\//.test(ua)) return 'Safari';
    return navigator.userAgent.substring(0, 60);
}

document.getElementById('LoginForm').addEventListener('submit', async function(event) {
    event.preventDefault();
    const userName = document.getElementById('username').value;
    const password  = document.getElementById('password').value;
    const deviceName = getDeviceName();
     try{
      const response = await fetch(
        `/users/login?name=${encodeURIComponent(userName)}&password=${encodeURIComponent(password)}&deviceName=${encodeURIComponent(deviceName)}`,
        { method: 'POST' }
      );
      if (response.ok) {
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
          const data = await response.json();
          const userId = data.userId;
          localStorage.setItem('userId', userId);
          localStorage.setItem('savedUsername', userName);
          window.location.href = `home1.html?userId=${userId}`;
        } else {
          const text = await response.text();
          console.error('Unexpected non-JSON response:', text);
          alert('Login Failed: Unexpected response format from server. (Check if the server is running on the correct port)');
        }
      } else {
        const errorData = await response.text();
        console.error('Error:', errorData);
        alert('Login failed: ' + errorData);
      }
     }
     catch(error){
        console.error('Detailed Error:', error);
        alert('An error occurred: ' + error.message + '\n\nPlease ensure your Spring Boot backend is running and accessible.');
     }
  });

window.addEventListener('DOMContentLoaded', () => {
    const savedUserId = localStorage.getItem('userId');
    if (savedUserId) {
        window.location.href = `home1.html?userId=${savedUserId}`;
    }
    const savedUsername = localStorage.getItem('savedUsername');
    if (savedUsername) {
        document.getElementById('username').value = savedUsername;
    }
});