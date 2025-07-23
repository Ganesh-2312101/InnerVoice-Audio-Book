document.getElementById('LoginForm').addEventListener('submit', async function(event) {
    event.preventDefault();
    const userName= document.getElementById('username').value;
    const password = document.getElementById('password').value;
     try{
      const response= await fetch(`http://localhost:8080/users/login?name=${userName}&password=${password}`, {
        method: 'POST',
      })
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