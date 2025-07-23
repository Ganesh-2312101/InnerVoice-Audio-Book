
    function menuBring(){
      document.getElementById("SideMenu").classList.remove("-translate-x-full");
    }
    function sendMenu(){
      document.getElementById("SideMenu").classList.add("-translate-x-full");
    }

    async function loadUser(){
        try{
            const params = new URLSearchParams(window.location.search);
            const userId = params.get('userId');
            const response = await fetch(`http://localhost:8080/users/${userId}`)
            if(response.ok){
                const data = await response.json();
                const name = data.name;
                document.getElementById("Welcome").textContent=`Welcome ${name}`;

                if(data.userCategory === 'PREMIUM_USER'){
                document.getElementById("Premium").style.display = 'none';
            }
            }
            else{
                document.getElementById("Welcome").textContent=`Error Loading Name`;
            }
        }
        catch (error) {
            console.error("Error:", error);
            document.getElementById("Welcome").textContent = "Welcome!";
        }
    }
    loadUser();

    // set profile link
    const params1 = new URLSearchParams(window.location.search);
    const id = params1.get('userId');
    const link = document.getElementById("link");
    link.href = `Profile.html?userId=${id}`;
    const premium=document.getElementById("Premium");
    premium.href=`Premium.html?userId=${id}`;

    async function printRecommendations() {
    try {
        const p=new URLSearchParams(window.location.search);
        const userId=p.get('userId');
        const response = await fetch(`http://localhost:8080/books/recommendations/${userId}`);
        if (response.ok) {
            const books = await response.json();

            const container = document.getElementById("Recommendations");
            if (!container) {
                console.error("Recommendations container not found");
                return;
            }

            container.innerHTML = ""; // Clear old content

            books.forEach(book => {
                const card = document.createElement('div');
                const type = document.createElement('p');

                type.innerHTML = book.bookCategory === 'Premium' ? 'Premium' : 'Free';
                type.className = book.bookCategory === 'Premium'
                    ? "flex text-[#FFC107] justify-left text-sm mb-1"
                    : "flex text-white justify-left text-sm mb-1";

                card.className = "w-40 flex-shrink-0 bg-gray-800 rounded px-2 pt-1 text-center";
                card.onclick = () => goToBook(book.bookId);

                const img = document.createElement('img');
                img.src = book.imageLink;
                img.alt = book.bookName;
                img.className = "w-full h-48 object-cover rounded";

                const title = document.createElement('p');
                title.textContent = book.bookName;
                title.className = "mt-2 text-red-500 text-sm font-bold";

                card.appendChild(type);
                card.appendChild(img);
                card.appendChild(title);
                container.appendChild(card);
            });

        } else {
            console.error('Failed to fetch books:', response.status);
        }
    } catch (error) {
        console.error('Error fetching books:', error);
    }
}


    // load books
    async function loadBooks(bookType){
        try{
            const response = await fetch(`http://localhost:8080/books/${bookType}`);
            if(response.ok){
                const books = await response.json();
                const container = document.getElementById(bookType);
                container.innerHTML = ''; // clear previous

                books.forEach(book=>{
                    const card = document.createElement('div');
                    const type= document.createElement('p');
                    if(book.bookCategory==='Premium')
                    {
                         type.innerHTML=`Premium`;
                         type.className= "flex text-[#FFC107] justify-left text-sm mb-1";
                    }
                    else
                    {
                        type.innerHTML=`Free`;
                        type.className= "flex text-white justify-left text-sm mb-1";
                    }

                    card.className = "w-40 flex-shrink-0 bg-gray-800 rounded px-2 pt-1 text-center";
                    card.onclick=() => goToBook(book.bookId);
                    const img = document.createElement('img');
                    img.src = book.imageLink;
                    img.alt = book.bookName;
                    img.className = "w-full h-48 object-cover rounded";

                    const title = document.createElement('p');
                    title.textContent = book.bookName;
                    title.className = "mt-2 text-red-500 text-sm font-bold";
                    card.appendChild(type);
                    card.appendChild(img);
                    card.appendChild(title);


                    container.appendChild(card);
                });
            } else {
                console.error('Failed to fetch books:', response.status);
            }
        } catch(error){
            console.error('Error fetching books:', error);
        }
    }

    // call when page loads
    window.addEventListener('DOMContentLoaded', () => {
        loadBooks('Productivity'); // this must match your div id
        loadBooks('Novels');
        loadBooks('Science Fiction');
        loadBooks('Crime Thrillers');
        printRecommendations();
setTimeout(() => {
        const containers = document.querySelectorAll(".scroll-container");
        containers.forEach((container) => observer.observe(container));
    }, 1000);
    });

    async function searchBooks(){
    const input = document.getElementById('searchInput').value.trim();
    const resultsDiv = document.getElementById('searchResults');

    if(input === ''){
        resultsDiv.classList.add('hidden');
        resultsDiv.innerHTML = '';
        return;
    }

    try{
        const response = await fetch(`http://localhost:8080/books/search/${input}`);
        if(response.ok){
            const books = await response.json();

            if(books.length === 0){
                resultsDiv.innerHTML = `<p class="p-2 text-gray-400">No results found.</p>`;
            } else {
                resultsDiv.innerHTML = books.map(book => `
                    <div onclick="goToBook(${book.bookId})" class="p-2 cursor-pointer hover:bg-gray-800">
                        ${book.bookName}
                    </div>
                `).join('');
            }

            resultsDiv.classList.remove('hidden');
        } else {
            console.error('Search failed:', response.status);
            resultsDiv.classList.add('hidden');
        }
    } catch(error){
        console.error('Error fetching search:', error);
        resultsDiv.classList.add('hidden');
    }
}

//handle what happens when a book is clicked
async function goToBook(bookId){
    const response=await fetch(`http://localhost:8080/users/${id}`);
    const user= await response.json();
        await fetch(`http://localhost:8080/activity/track?userId=${id}&bookId=${bookId}`, {
        method: 'POST'
    });
    if(user.userCategory === 'FREE_USER')
    {
        const bookResponse=await fetch(`http://localhost:8080/books/getting/${bookId}`);
        const book=await bookResponse.json();
        if(book.bookCategory === 'Premium')
        {
            window.location.href = `Premium.html?userId=${id}`;
        }
        else
        {
            window.location.href = `BookDetails.html?bookId=${bookId}`;
        }
    }
    else
    {
        window.location.href = `BookDetails.html?bookId=${bookId}`;
    }
}


    function autoScroll(container) {
    const scrollAmount = container.firstElementChild?.offsetWidth * 3 || 0;
    if (scrollAmount > 0) {
        setInterval(() => {
            container.scrollBy({ left: scrollAmount, behavior: 'smooth' });
        }, 3000); // every 3 seconds
    }
}

// Observe only one container in view
const observer = new IntersectionObserver(
    (entries) => {
        entries.forEach((entry) => {
            if (entry.isIntersecting) {
                autoScroll(entry.target); // only scroll the visible one
            }
        });
    },
    {
        threshold: 0.7, // 70% of container should be visible
    }
);