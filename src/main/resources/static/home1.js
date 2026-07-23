function menuBring() {
  document.getElementById("SideMenu").classList.remove("-translate-x-full");
}

function sendMenu() {
  document.getElementById("SideMenu").classList.add("-translate-x-full");
}

async function loadUser() {
  try {
    const params = new URLSearchParams(window.location.search);
    const userId = params.get('userId') || localStorage.getItem('userId');
    const response = await fetch(`/users/${userId}`);
    if (response.ok) {
      const data = await response.json();
      const name = data.name;
      document.getElementById("Welcome").textContent = `Welcome back, ${name}! 👋`;

      if (data.userCategory === 'PREMIUM_USER') {
        const premiumBtn = document.getElementById("Premium");
        if (premiumBtn) premiumBtn.style.display = 'none';
      }
    } else {
      document.getElementById("Welcome").textContent = `Welcome to InnerVoice! 👋`;
    }
  } catch (error) {
    console.error("Error loading user details:", error);
    document.getElementById("Welcome").textContent = "Welcome to InnerVoice! 👋";
  }
}
loadUser();

// Set profile & premium link parameters
const params1 = new URLSearchParams(window.location.search);
const id = params1.get('userId') || localStorage.getItem('userId');

const link = document.getElementById("link");
if (link) link.href = `Profile.html?userId=${id}`;

const premium = document.getElementById("Premium");
if (premium) premium.href = `Premium.html?userId=${id}`;

// Helper to construct modern book cards
function createBookCard(book) {
  const card = document.createElement('div');
  card.className = "w-44 sm:w-48 flex-shrink-0 group relative cursor-pointer glass-card rounded-2xl p-3 flex flex-col justify-between transition-all duration-300";
  card.onclick = () => goToBook(book.bookId);

  const isPremium = book.bookCategory === 'Premium';
  const badgeClass = isPremium ? "badge-premium" : "badge-free";
  const badgeText = isPremium ? '<i class="fa-solid fa-crown text-amber-400 mr-1 text-[10px]"></i> Premium' : 'Free';

  card.innerHTML = `
    <div class="relative w-full aspect-[3/4] rounded-xl overflow-hidden mb-3 bg-slate-800">
      <img src="${book.imageLink}" alt="${book.bookName}" class="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500" onerror="this.src='default.webp'">
      <div class="absolute top-2 left-2 px-2.5 py-0.5 text-[10px] font-bold rounded-full ${badgeClass} shadow-md">
        ${badgeText}
      </div>
      <div class="play-overlay absolute inset-0 bg-slate-950/60 backdrop-blur-[2px] flex items-center justify-center">
        <div class="w-12 h-12 rounded-full bg-red-500 text-white flex items-center justify-center shadow-lg transform group-hover:scale-110 transition-transform">
          <i class="fa-solid fa-play text-lg ml-0.5"></i>
        </div>
      </div>
    </div>
    <div class="flex-1 flex flex-col justify-between">
      <div>
        <h3 class="text-sm font-bold text-gray-100 group-hover:text-red-400 transition-colors line-clamp-1" title="${book.bookName}">${book.bookName}</h3>
        <p class="text-xs text-gray-400 mt-0.5 font-medium">Audiobook</p>
      </div>
      <div class="flex items-center justify-between mt-2 pt-2 border-t border-slate-700/50 text-[11px] text-gray-400">
        <span class="flex items-center space-x-1 text-amber-400 font-semibold">
          <i class="fa-solid fa-star text-[10px]"></i>
          <span>4.8</span>
        </span>
        <span class="text-red-400 group-hover:translate-x-1 transition-transform font-bold">Listen <i class="fa-solid fa-chevron-right text-[9px] ml-0.5"></i></span>
      </div>
    </div>
  `;

  return card;
}

// Fetch and render recommendations
async function printRecommendations() {
  try {
    const p = new URLSearchParams(window.location.search);
    const userId = p.get('userId') || localStorage.getItem('userId');
    const response = await fetch(`/books/recommendations/${userId}`);
    if (response.ok) {
      const books = await response.json();
      const container = document.getElementById("Recommendations");
      if (!container) return;

      container.innerHTML = "";
      books.forEach(book => {
        container.appendChild(createBookCard(book));
      });
    }
  } catch (error) {
    console.error('Error fetching recommendations:', error);
  }
}

// Fetch and render category books
async function loadBooks(bookType) {
  try {
    const response = await fetch(`/books/${bookType}`);
    if (response.ok) {
      const books = await response.json();
      const container = document.getElementById(bookType);
      if (!container) return;
      container.innerHTML = '';

      books.forEach(book => {
        container.appendChild(createBookCard(book));
      });
    }
  } catch (error) {
    console.error(`Error fetching books for ${bookType}:`, error);
  }
}

// Global page load event listener
window.addEventListener('DOMContentLoaded', () => {
  loadBooks('Productivity');
  loadBooks('Novels');
  loadBooks('Science Fiction');
  loadBooks('Crime Thrillers');
  printRecommendations();
});

// Live Search handler
async function searchBooks() {
  const input = document.getElementById('searchInput').value.trim();
  const resultsDiv = document.getElementById('searchResults');
  const searchSection = document.getElementById('SearchResultsSection');
  const searchContainer = document.getElementById('SearchResultsContainer');
  const searchCount = document.getElementById('SearchResultCount');

  if (input === '') {
    clearSearch();
    return;
  }

  try {
    const response = await fetch(`/books/search/${input}`);
    if (response.ok) {
      const books = await response.json();

      // Update dropdown results
      if (books.length === 0) {
        resultsDiv.innerHTML = `<p class="p-3 text-sm text-gray-400">No audiobooks found matching "${input}".</p>`;
      } else {
        resultsDiv.innerHTML = books.map(book => `
          <div onclick="goToBook(${book.bookId})" class="flex items-center space-x-3 p-2.5 rounded-xl cursor-pointer hover:bg-slate-800 transition">
            <img src="${book.imageLink}" class="w-10 h-12 object-cover rounded-lg" onerror="this.src='default.webp'">
            <div>
              <div class="text-sm font-semibold text-white">${book.bookName}</div>
              <div class="text-xs text-red-400 font-medium">${book.bookCategory}</div>
            </div>
          </div>
        `).join('');
      }
      resultsDiv.classList.remove('hidden');

      // Update Top Page Search Shelf ("First" position)
      if (searchSection && searchContainer) {
        searchContainer.innerHTML = '';
        if (books.length > 0) {
          books.forEach(book => {
            searchContainer.appendChild(createBookCard(book));
          });
          if (searchCount) searchCount.textContent = `(${books.length} found)`;
          searchSection.classList.remove('hidden');
          // Smooth scroll top search section into view
          searchSection.scrollIntoView({ behavior: 'smooth', block: 'start' });
        } else {
          searchSection.classList.add('hidden');
        }
      }

    } else {
      resultsDiv.classList.add('hidden');
    }
  } catch (error) {
    console.error('Error in search:', error);
    resultsDiv.classList.add('hidden');
  }
}

function clearSearch() {
  const searchInput = document.getElementById('searchInput');
  const resultsDiv = document.getElementById('searchResults');
  const searchSection = document.getElementById('SearchResultsSection');
  const searchContainer = document.getElementById('SearchResultsContainer');

  if (searchInput) searchInput.value = '';
  if (resultsDiv) {
    resultsDiv.classList.add('hidden');
    resultsDiv.innerHTML = '';
  }
  if (searchSection) searchSection.classList.add('hidden');
  if (searchContainer) searchContainer.innerHTML = '';
}

// Navigation & Access tracking on book click
async function goToBook(bookId) {
  try {
    const response = await fetch(`/users/${id}`);
    const user = await response.json();

    await fetch(`/activity/track?userId=${id}&bookId=${bookId}`, {
      method: 'POST'
    });

    if (user.userCategory === 'FREE_USER') {
      const bookResponse = await fetch(`/books/getting/${bookId}`);
      const book = await bookResponse.json();
      if (book.bookCategory === 'Premium') {
        window.location.href = `Premium.html?userId=${id}`;
      } else {
        window.location.href = `BookDetails.html?bookId=${bookId}`;
      }
    } else {
      window.location.href = `BookDetails.html?bookId=${bookId}`;
    }
  } catch (err) {
    console.error('Error redirecting to book:', err);
    window.location.href = `BookDetails.html?bookId=${bookId}`;
  }
}

function logout() {
  localStorage.removeItem('userId');
  window.location.href = 'login.html';
}
