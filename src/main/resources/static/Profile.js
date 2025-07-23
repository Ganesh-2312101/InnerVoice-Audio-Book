async function display(){
            const params = new URLSearchParams(window.location.search);
            const userId = params.get('userId');

            const response = await fetch(`http://localhost:8080/users/${userId}`);

            if(response.ok){
                const user = await response.json();

                let premiumSection = '';

            if(user.userCategory === 'PREMIUM_USER' && user.premiumPlan){
                // call backend to get days left
                const daysLeftResponse = await fetch(`http://localhost:8080/users/premium-days-left/${userId}`);
                let daysLeftText = '';
                if(daysLeftResponse.ok){
                    daysLeftText = await daysLeftResponse.text();
                } else {
                    daysLeftText = 'Could not fetch premium validity.';
                }

                premiumSection = `
                    <p class="text-lg"><span class="font-medium text-gray-300">Premium Plan:</span> ${user.premiumPlan}</p>
                    <p class="text-lg"><span class="font-medium text-gray-300">Status:</span> ${daysLeftText}</p>
                `;
            }

                document.getElementById('ProfileInfo').innerHTML = ` <center>
                    <div class="bg-gray-800/70 p-8 rounded-2xl shadow-lg flex flex-col items-center space-y-6 w-[40%] mt-[5%]">
                        <img src="https://api.dicebear.com/6.x/initials/svg?seed=${user.userName}" alt="avatar" class="w-28 h-28 rounded-full border-4 border-[#0CF574] shadow-md">
                        <h2 class="text-3xl font-semibold text-[#0CF574]">${user.userName}</h2>
                        <div class="space-y-2 text-center">
                            <p class="text-lg"><span class="font-medium text-gray-300">Full Name:</span> ${user.name}</p>
                            <p class="text-lg"><span class="font-medium text-gray-300">Email:</span> ${user.emailId}</p>
                            <p class="text-lg"><span class="font-medium text-gray-300">User Category:</span> ${user.userCategory}</p>
                            ${premiumSection}
                        </div>
                    </div> </center>`;
            } else {
                document.getElementById('ProfileInfo').innerHTML = `
                    <div class="bg-red-700/30 p-6 rounded-xl text-center text-white">
                        <p>Failed to load profile. Please try again later.</p>
                    </div>`;
            }
        }
        display();