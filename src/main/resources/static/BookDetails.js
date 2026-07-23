document.addEventListener("DOMContentLoaded", async () => {
    const params = new URLSearchParams(window.location.search);
    const bookId = params.get('bookId');

    try {
      const response = await fetch(`/books/getting/${bookId}`);

      if (response.ok) {
        const book = await response.json();
        const name = document.getElementById('bookTitle');
        const image = document.getElementById('image');
        const audioElement = document.getElementById('audio');

        name.textContent = book.bookName;

        // Image with fallback
        if (book.imageLink) {
          image.src = book.imageLink;
          image.onerror = () => { image.src = 'default.webp'; };
        } else {
          image.src = 'default.webp';
        }

        // Audio: set src and load
        if (book.audioFileLink) {
          audioElement.src = book.audioFileLink;
          audioElement.load();
        }
      } else {
        console.error("Failed to fetch book data:", response.status);
      }
    } catch (error) {
      console.error("Error fetching book details:", error);
    }
  });

  const audio = document.getElementById('audio');
  const progressBar = document.getElementById('progressBar');
  const currentTimeSpan = document.getElementById('currentTime');
  const durationSpan = document.getElementById('duration');
  const seekBar = document.getElementById('seekBar');

  // Seek bar click — registered once at load time
  if (seekBar) {
    seekBar.addEventListener('click', function (e) {
      if (!audio.duration) return;
      const rect = seekBar.getBoundingClientRect();
      const percent = (e.clientX - rect.left) / rect.width;
      audio.currentTime = percent * audio.duration;
    });
  }

function toggleAudio() {
  const iconPath = document.getElementById('iconPath');
  if (audio.paused) {
    audio.play().catch(err => console.error("Playback error:", err));
    iconPath.setAttribute("d", "M10 9v6m4-6v6"); // Pause icon
  } else {
    audio.pause();
    iconPath.setAttribute("d", "M5 3v18l15-9L5 3z"); // Play icon
  }
}

  audio.addEventListener('loadedmetadata', () => {
    durationSpan.textContent = formatTime(audio.duration);
  });

  audio.addEventListener('timeupdate', () => {
    if (!audio.duration) return;
    const progress = (audio.currentTime / audio.duration) * 100;
    progressBar.style.width = progress + '%';
    currentTimeSpan.textContent = formatTime(audio.currentTime);
  });

  audio.addEventListener('ended', () => {
    const iconPath = document.getElementById('iconPath');
    iconPath.setAttribute("d", "M5 3v18l15-9L5 3z"); // Reset to play icon
    progressBar.style.width = '0%';
    currentTimeSpan.textContent = '0:00';
  });

  function formatTime(seconds) {
    if (isNaN(seconds)) return '0:00';
    const mins = Math.floor(seconds / 60);
    const secs = Math.floor(seconds % 60);
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  }

  async function generateSummary(type) {
    const params = new URLSearchParams(window.location.search);
    const bookId = params.get('bookId');
    if (!bookId) {
      alert("Book ID not found");
      return;
    }
    
    const summaryContainer = document.getElementById('summaryContainer');
    const summaryContent = document.getElementById('summaryContent');
    
    summaryContainer.classList.remove('hidden');
    summaryContent.innerHTML = `<span class="animate-pulse text-indigo-400">🤖 AI is generating summary...</span>`;

    try {
      const response = await fetch(`/books/summary/${bookId}?type=${type}`);
      if (response.ok) {
        const data = await response.text();
        summaryContent.innerHTML = data;
      } else {
        summaryContent.innerHTML = `<span class="text-red-500">Failed to generate summary. Please try again later.</span>`;
      }
    } catch (error) {
      console.error("Error generating summary:", error);
      summaryContent.innerHTML = `<span class="text-red-500">An error occurred while communicating with the AI.</span>`;
    }
  }
