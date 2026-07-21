async function display() {
    const params = new URLSearchParams(window.location.search);
    const userId = params.get('userId');

    const response = await fetch(`http://localhost:8080/users/${userId}`);

    if (response.ok) {
        const user = await response.json();

        let premiumSection = '';

        if (user.userCategory === 'PREMIUM_USER' && user.premiumPlan) {
            const daysLeftResponse = await fetch(`http://localhost:8080/users/premium-days-left/${userId}`);
            let daysLeftText = '';
            if (daysLeftResponse.ok) {
                daysLeftText = await daysLeftResponse.text();
            } else {
                daysLeftText = 'Could not fetch premium validity.';
            }
            premiumSection = `
                <p class="text-lg"><span class="font-medium text-gray-300">Premium Plan:</span> ${user.premiumPlan}</p>
                <p class="text-lg"><span class="font-medium text-gray-300">Status:</span> ${daysLeftText}</p>
            `;
        }

        // Resolve avatar: use uploaded pic or DiceBear fallback
        const avatarSrc = user.profilePicture
            ? `/profile-pics/${user.profilePicture}`
            : `https://api.dicebear.com/6.x/initials/svg?seed=${user.userName}`;

        document.getElementById('ProfileInfo').innerHTML = `
        <center>
            <div class="bg-gray-800/70 p-8 rounded-2xl shadow-lg flex flex-col items-center space-y-6 w-[90%] lg:w-[40%] mt-[5%]">

                <!-- Avatar with camera-upload overlay -->
                <div class="relative group cursor-pointer" id="avatarWrapper" title="Change profile picture">
                    <img id="profileAvatar"
                         src="${avatarSrc}"
                         alt="avatar"
                         class="w-28 h-28 rounded-full border-4 border-[#0CF574] shadow-md object-cover">

                    <!-- Camera icon overlay (visible on hover) -->
                    <div class="absolute inset-0 flex flex-col items-center justify-center
                                rounded-full bg-black/60 opacity-0 group-hover:opacity-100 transition-opacity">
                        <svg xmlns="http://www.w3.org/2000/svg" class="w-8 h-8 text-white mb-1" fill="none"
                             viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                            <path stroke-linecap="round" stroke-linejoin="round"
                                  d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2
                                  2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0
                                  01-2 2H5a2 2 0 01-2-2V9z"/>
                            <path stroke-linecap="round" stroke-linejoin="round" d="M15 13a3 3 0 11-6 0 3 3 0 016 0z"/>
                        </svg>
                        <span class="text-white text-xs font-semibold">Change Photo</span>
                    </div>

                    <!-- Hidden file input -->
                    <input type="file" id="picInput" accept="image/*"
                           class="absolute inset-0 opacity-0 cursor-pointer rounded-full w-full h-full">
                </div>

                <!-- Upload action bar (hidden until file is selected) -->
                <div id="uploadBar" class="hidden flex-col items-center gap-2 w-full">
                    <p id="uploadFileName" class="text-gray-400 text-sm text-center truncate w-full"></p>
                    <div class="flex gap-3">
                        <button id="uploadBtn"
                                class="bg-[#0CF574] text-black font-bold px-5 py-2 rounded-xl hover:bg-green-400 transition text-sm">
                            ✅ Save Photo
                        </button>
                        <button id="cancelUploadBtn"
                                class="bg-gray-700 text-white font-semibold px-5 py-2 rounded-xl hover:bg-gray-600 transition text-sm">
                            ✖ Cancel
                        </button>
                    </div>
                </div>

                <h2 class="text-3xl font-semibold text-[#0CF574]">${user.userName}</h2>
                <div class="space-y-2 text-center">
                    <p class="text-lg"><span class="font-medium text-gray-300">Full Name:</span> ${user.name}</p>
                    <p class="text-lg"><span class="font-medium text-gray-300">Email:</span> ${user.emailId}</p>
                    <p class="text-lg"><span class="font-medium text-gray-300">User Category:</span> ${user.userCategory}</p>
                    ${premiumSection}
                </div>
            </div>
        </center>`;

        // ── Wire up upload logic ──────────────────────────────────────
        const picInput      = document.getElementById('picInput');
        const uploadBar     = document.getElementById('uploadBar');
        const uploadBtn     = document.getElementById('uploadBtn');
        const cancelBtn     = document.getElementById('cancelUploadBtn');
        const avatar        = document.getElementById('profileAvatar');
        const uploadFileName= document.getElementById('uploadFileName');

        let selectedFile = null;

        // When user picks a file → show live preview + action bar
        picInput.addEventListener('change', function () {
            if (!this.files || !this.files[0]) return;
            selectedFile = this.files[0];
            uploadFileName.textContent = selectedFile.name;

            // Live preview before saving
            const reader = new FileReader();
            reader.onload = e => { avatar.src = e.target.result; };
            reader.readAsDataURL(selectedFile);

            uploadBar.classList.remove('hidden');
            uploadBar.classList.add('flex');
        });

        // Save button → POST multipart to backend
        uploadBtn.addEventListener('click', async function () {
            if (!selectedFile) return;

            uploadBtn.disabled = true;
            uploadBtn.textContent = '⏳ Uploading…';

            const formData = new FormData();
            formData.append('file', selectedFile);

            try {
                const res = await fetch(`http://localhost:8080/users/${userId}/profile-picture`, {
                    method: 'POST',
                    body: formData
                });

                if (res.ok) {
                    const newPath = await res.text();
                    // Remove surrounding quotes if JSON string
                    avatar.src = newPath.replace(/"/g, '') + '?t=' + Date.now();
                    uploadBar.classList.add('hidden');
                    uploadBar.classList.remove('flex');
                    selectedFile = null;
                    showToast('✅ Profile picture updated!', '#0CF574');
                } else {
                    showToast('❌ Upload failed. Try again.', '#ef4444');
                    uploadBtn.disabled = false;
                    uploadBtn.textContent = '✅ Save Photo';
                }
            } catch (e) {
                showToast('❌ Network error.', '#ef4444');
                uploadBtn.disabled = false;
                uploadBtn.textContent = '✅ Save Photo';
            }
        });

        // Cancel → revert preview to original
        cancelBtn.addEventListener('click', function () {
            avatar.src = avatarSrc + '?t=' + Date.now();
            uploadBar.classList.add('hidden');
            uploadBar.classList.remove('flex');
            selectedFile = null;
            picInput.value = '';
        });

    } else {
        document.getElementById('ProfileInfo').innerHTML = `
            <div class="bg-red-700/30 p-6 rounded-xl text-center text-white">
                <p>Failed to load profile. Please try again later.</p>
            </div>`;
    }
}

// ── Toast notification helper ───────────────────────────────────────────────
function showToast(message, color = '#0CF574') {
    const toast = document.createElement('div');
    toast.textContent = message;
    toast.style.cssText = `
        position:fixed; bottom:30px; left:50%; transform:translateX(-50%);
        background:${color}; color:#000; font-weight:700;
        padding:12px 28px; border-radius:999px; font-size:15px;
        box-shadow:0 4px 20px rgba(0,0,0,0.4); z-index:9999;
        animation: fadeInUp 0.3s ease;
    `;
    document.body.appendChild(toast);
    setTimeout(() => toast.remove(), 3000);
}

// ── Login History ───────────────────────────────────────────────────────────
async function fetchLoginHistory() {
    const params = new URLSearchParams(window.location.search);
    const userId = params.get('userId');
    const container = document.getElementById('LoginHistorySection');

    try {
        const response = await fetch(`http://localhost:8080/login-history/user/${userId}`);
        if (!response.ok) {
            container.innerHTML = '<p class="text-center text-gray-400">Could not load login history.</p>';
            return;
        }

        const history = await response.json();

        if (history.length === 0) {
            container.innerHTML = '<p class="text-center text-gray-400 mt-4">No login history found.</p>';
            return;
        }

        const rows = history.map(entry => {
            const dt = new Date(entry.loginTime);
            const formatted = dt.toLocaleString('en-IN', {
                day: '2-digit', month: 'short', year: 'numeric',
                hour: '2-digit', minute: '2-digit', hour12: true
            });
            return `
            <tr class="border-b border-gray-700 hover:bg-gray-700/40 transition">
                <td class="px-5 py-3 text-[#0CF574] font-semibold">${entry.userName}</td>
                <td class="px-5 py-3 text-gray-300">${formatted}</td>
                <td class="px-5 py-3 text-gray-300">${entry.ipAddress ?? '—'}</td>
                <td class="px-5 py-3">
                    <span class="bg-gray-700 text-white text-xs px-3 py-1 rounded-full">${entry.deviceName ?? 'Unknown'}</span>
                </td>
            </tr>`;
        }).join('');

        container.innerHTML = `
        <div class="mt-10 px-4 lg:px-[10%] pb-12">
            <h2 class="text-2xl font-bold text-white mb-5">🕐 Login History</h2>
            <div class="overflow-x-auto rounded-2xl shadow-lg">
                <table class="w-full text-sm text-left bg-gray-900 rounded-2xl overflow-hidden">
                    <thead class="bg-gray-800 text-gray-400 uppercase text-xs tracking-wider">
                        <tr>
                            <th class="px-5 py-3">User</th>
                            <th class="px-5 py-3">Time</th>
                            <th class="px-5 py-3">IP Address</th>
                            <th class="px-5 py-3">Device</th>
                        </tr>
                    </thead>
                    <tbody>${rows}</tbody>
                </table>
            </div>
        </div>`;
    } catch (e) {
        container.innerHTML = '<p class="text-center text-red-400 mt-4">Error loading login history.</p>';
    }
}

display();
fetchLoginHistory();