    document.getElementById('SignUpForm').addEventListener('submit', async function(event) {
    event.preventDefault(); // prevent default form action
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const email = document.getElementById('email').value;
    const name = document.getElementById('name').value;

    try {
      const response = await fetch('/users', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          userName: username,
          userCategory: "FREE_USER",
          password: password,
          emailId: email,
          name: name
        })
      });

      if (response.ok) {
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
          const data = await response.json();
          const userId = data.userId;
          localStorage.setItem('userId', userId);
          window.location.href = `home1.html?userId=${userId}`;
        } else {
          const text = await response.text();
          console.error('Unexpected non-JSON response:', text);
          alert('SignUp Failed: Unexpected response format from server. (Check if the server is running on the correct port)');
        }
      } else {
        const errorMsg = await response.text(); // get backend message
        alert('SignUp Failed: ' + errorMsg);
      }
    } catch (error) {
      console.error('Detailed Error:', error);
      alert('An error occurred: ' + error.message + '\n\nPlease ensure your Spring Boot backend is running and accessible.');
    }
  });
