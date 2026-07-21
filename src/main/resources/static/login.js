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
        `http://localhost:8080/users/login?name=${encodeURIComponent(userName)}&password=${encodeURIComponent(password)}&deviceName=${encodeURIComponent(deviceName)}`,
        { method: 'POST' }
      );
      if(response.ok){
        const data = await response.json();
        const userId = data.userId;
        window.location.href = `home1.html?userId=${userId}`;
      }
      else {
        const errorData = await response.text();
        console.error('Error:', errorData);
        alert('Login failed. Incorrect password.');
      }
     }
     catch(error){
        console.error('Error:', error);
        alert('Invalid User Name');
     }
  });