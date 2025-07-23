    document.getElementById('SignUpForm').addEventListener('submit', async function(event) {
    event.preventDefault(); // prevent default form action
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const email = document.getElementById('email').value;
    const name = document.getElementById('name').value;

    try {
      const response = await fetch('http://localhost:8080/users', {
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
        const data = await response.json();
        const userId = data.userId;
        window.location.href = `home1.html?userId=${userId}`;
      } else {
        const errorMsg = await response.text(); // get backend message
        alert('SignUp Failed: ' + errorMsg);
      }
    } catch (error) {
      console.error('Error:', error);
      alert('An error occurred.');
    }
  });