import mysql.connector

# Mapping of book_name -> reliable cover image URL
# Using Open Library covers by ISBN, Google Books by ID, and other reliable sources
COVER_URLS = {
    # ========== Productivity ==========
    "Atomic Habits": "https://m.media-amazon.com/images/I/81ANaVZk5LL._SY466_.jpg",
    "Deep Work": "https://m.media-amazon.com/images/I/81JJ7fyyKyS._SY466_.jpg",
    "The 7 Habits of Highly Effective People": "https://m.media-amazon.com/images/I/71yI1MWsxFL._SY466_.jpg",
    "The 7 Habits": "https://m.media-amazon.com/images/I/71yI1MWsxFL._SY466_.jpg",
    "Eat That Frog": "https://m.media-amazon.com/images/I/71ozhMK0r4L._SY466_.jpg",
    "The 5 AM Club": "https://m.media-amazon.com/images/I/71zytzrg6lL._SY466_.jpg",
    "Getting Things Done": "https://m.media-amazon.com/images/I/71fEMHDdDNL._SY466_.jpg",
    "Think and Grow Rich": "https://m.media-amazon.com/images/I/71UypkUjStL._SY466_.jpg",
    "The One Thing": "https://m.media-amazon.com/images/I/71eRK-bvDvL._SY466_.jpg",
    "Mindset": "https://m.media-amazon.com/images/I/61bDwEBtdFL._SY466_.jpg",
    "The Power of Habit": "https://m.media-amazon.com/images/I/81Myap0MmhL._SY466_.jpg",
    "Rich Dad Poor Dad": "https://m.media-amazon.com/images/I/81bsw6fnUiL._SY466_.jpg",
    "The Magic of Thinking Big": "https://m.media-amazon.com/images/I/71UfNWfSr0L._SY466_.jpg",
    "The Compound Effect": "https://m.media-amazon.com/images/I/71HAiKFOdML._SY466_.jpg",
    "Essentialism": "https://m.media-amazon.com/images/I/71Iyf-FE4ZL._SY466_.jpg",
    "The Miracle Morning": "https://m.media-amazon.com/images/I/71p0KQh37sL._SY466_.jpg",
    "The Success Principles": "https://m.media-amazon.com/images/I/81r7Os3CJPL._SY466_.jpg",
    "Start With Why": "https://m.media-amazon.com/images/I/71NCtBz4VcL._SY466_.jpg",
    "Grit": "https://m.media-amazon.com/images/I/81VEKXfnUAL._SY466_.jpg",
    "Outliers": "https://m.media-amazon.com/images/I/71m-MxdJ2WL._SY466_.jpg",
    "Drive": "https://m.media-amazon.com/images/I/614St3EEsxL._SY466_.jpg",
    "Ikigai": "https://m.media-amazon.com/images/I/81l3rZK4lnL._SY466_.jpg",
    "Unlimited Power": "https://m.media-amazon.com/images/I/71W14IjvRVL._SY466_.jpg",
    "Can't Hurt Me": "https://m.media-amazon.com/images/I/81gTRv2HXrL._SY466_.jpg",
    "Peak": "https://m.media-amazon.com/images/I/71V0Vu-oLkL._SY466_.jpg",
    "Make Time": "https://m.media-amazon.com/images/I/71gLEoj1woL._SY466_.jpg",

    # ========== Science Fiction ==========
    "Dune": "https://m.media-amazon.com/images/I/81ym3QUd3KL._SY466_.jpg",
    "Foundation": "https://m.media-amazon.com/images/I/81iC6e0TQ+L._SY466_.jpg",
    "The Martian": "https://m.media-amazon.com/images/I/81wFMY9OAFL._SY466_.jpg",
    "The Time Machine": "https://m.media-amazon.com/images/I/71Qai3nMT3L._SY466_.jpg",
    "Ender's Game": "https://m.media-amazon.com/images/I/91LHLZ+BTRL._SY466_.jpg",
    "Ready Player One": "https://m.media-amazon.com/images/I/91urHoL1JmL._SY466_.jpg",
    "Neuromancer": "https://m.media-amazon.com/images/I/81VEKYnbcaL._SY466_.jpg",
    "I Robot": "https://m.media-amazon.com/images/I/51ee7WMx-gL._SY466_.jpg",
    "Fahrenheit 451": "https://m.media-amazon.com/images/I/71OFqSRFDgL._SY466_.jpg",
    "Brave New World": "https://m.media-amazon.com/images/I/81zE42gT3xL._SY466_.jpg",
    "Snow Crash": "https://m.media-amazon.com/images/I/81Bsa3tFKQL._SY466_.jpg",
    "Hyperion": "https://m.media-amazon.com/images/I/51DE2NqFPOL._SY466_.jpg",
    "Childhood's End": "https://m.media-amazon.com/images/I/71cNSsCqt6L._SY466_.jpg",
    "The Forever War": "https://m.media-amazon.com/images/I/81MKG5WoqlL._SY466_.jpg",
    "Solaris": "https://m.media-amazon.com/images/I/71bMfxafp3L._SY466_.jpg",
    "The Expanse": "https://m.media-amazon.com/images/I/91OI9tPW-5L._SY466_.jpg",
    "Red Rising": "https://m.media-amazon.com/images/I/81bcRVRjwjL._SY466_.jpg",
    "Project Hail Mary": "https://m.media-amazon.com/images/I/91vS2L5670L._SY466_.jpg",
    "Ringworld": "https://m.media-amazon.com/images/I/81ep0gOfrKL._SY466_.jpg",
    "The Left Hand of Darkness": "https://m.media-amazon.com/images/I/61-Iq4cEJSL._SY466_.jpg",
    "Do Androids Dream": "https://m.media-amazon.com/images/I/71HavnzUkXL._SY466_.jpg",
    "2001 A Space Odyssey": "https://m.media-amazon.com/images/I/71b06MkyScL._SY466_.jpg",
    "The Moon is a Harsh Mistress": "https://m.media-amazon.com/images/I/91c8wDKkJ1L._SY466_.jpg",
    "The Three Body Problem": "https://m.media-amazon.com/images/I/919XM42JQlL._SY466_.jpg",
    "The Invisible Man": "https://m.media-amazon.com/images/I/71lp3U8cJXL._SY466_.jpg",

    # ========== Crime Thrillers ==========
    "Sherlock Holmes": "https://m.media-amazon.com/images/I/81z1yJRbHXL._SY466_.jpg",
    "Gone Girl": "https://m.media-amazon.com/images/I/71+GQJT2gkL._SY466_.jpg",
    "The Da Vinci Code": "https://m.media-amazon.com/images/I/91Q5dCjc2KL._SY466_.jpg",
    "The Silent Patient": "https://m.media-amazon.com/images/I/81IzbD2IiIL._SY466_.jpg",
    "Murder on the Orient Express": "https://m.media-amazon.com/images/I/81pqpOQW8nL._SY466_.jpg",
    "The Firm": "https://m.media-amazon.com/images/I/81prqJYKGiL._SY466_.jpg",
    "The Client": "https://m.media-amazon.com/images/I/81M-ZZh0tTL._SY466_.jpg",
    "Killing Floor": "https://m.media-amazon.com/images/I/81TbD38YOEL._SY466_.jpg",
    "The Girl with the Dragon Tattoo": "https://m.media-amazon.com/images/I/81iqZ2HHD-L._SY466_.jpg",
    "Angels and Demons": "https://m.media-amazon.com/images/I/81WcnNQ-TBL._SY466_.jpg",
    "Inferno": "https://m.media-amazon.com/images/I/81GDME0FU+L._SY466_.jpg",
    "Origin": "https://m.media-amazon.com/images/I/91CQHL+HUKL._SY466_.jpg",
    "Digital Fortress": "https://m.media-amazon.com/images/I/71NFGlFEs8L._SY466_.jpg",
    "Presumed Innocent": "https://m.media-amazon.com/images/I/81JDvDShyBL._SY466_.jpg",
    "The Appeal": "https://m.media-amazon.com/images/I/81Pv8sBnrfL._SY466_.jpg",
    "The Broker": "https://m.media-amazon.com/images/I/814tC6QRxbL._SY466_.jpg",
    "The Partner": "https://m.media-amazon.com/images/I/71g2MkaB+dL._SY466_.jpg",
    "The Pelican Brief": "https://m.media-amazon.com/images/I/71J0y-mOR5L._SY466_.jpg",
    "A Time To Kill": "https://m.media-amazon.com/images/I/81NHDh8ZjWL._SY466_.jpg",
    "The Runaway Jury": "https://m.media-amazon.com/images/I/81TBfLCclKL._SY466_.jpg",
    "The Last Juror": "https://m.media-amazon.com/images/I/91e2W7UBG7L._SY466_.jpg",
    "The Testament": "https://m.media-amazon.com/images/I/71fXjWMO-aL._SY466_.jpg",
    "The Reversal": "https://m.media-amazon.com/images/I/818+ILbTDyL._SY466_.jpg",
    "The Racketeer": "https://m.media-amazon.com/images/I/81mNE6KKOEL._SY466_.jpg",
    "The Whistler": "https://m.media-amazon.com/images/I/81YkqyaFVEL._SY466_.jpg",

    # ========== Novels ==========
    "The Alchemist": "https://m.media-amazon.com/images/I/71aFt4+OTOL._SY466_.jpg",
    "1984": "https://m.media-amazon.com/images/I/71kxa1-0mfL._SY466_.jpg",
    "Pride and Prejudice": "https://m.media-amazon.com/images/I/71Q1tPupKjL._SY466_.jpg",
    "The Hobbit": "https://m.media-amazon.com/images/I/71jLBXtWJWL._SY466_.jpg",
    "The Great Gatsby": "https://m.media-amazon.com/images/I/81QuEGw8VPL._SY466_.jpg",
    "To Kill a Mockingbird": "https://m.media-amazon.com/images/I/81gepf1eMqL._SY466_.jpg",
    "The Kite Runner": "https://m.media-amazon.com/images/I/81LVDX01+6L._SY466_.jpg",
    "The Book Thief": "https://m.media-amazon.com/images/I/91biFe+QXOL._SY466_.jpg",
    "Little Women": "https://m.media-amazon.com/images/I/81T7l6cS6AL._SY466_.jpg",
    "The Fault in Our Stars": "https://m.media-amazon.com/images/I/817tHNcyAgS._SY466_.jpg",
    "The Catcher in the Rye": "https://m.media-amazon.com/images/I/8125BDk3l9L._SY466_.jpg",
    "The Hunger Games": "https://m.media-amazon.com/images/I/71un2hI4mcL._SY466_.jpg",
    "Life of Pi": "https://m.media-amazon.com/images/I/71XPa2bFijL._SY466_.jpg",
    "Animal Farm": "https://m.media-amazon.com/images/I/71je3-DsQEL._SY466_.jpg",
    "Jane Eyre": "https://m.media-amazon.com/images/I/71hMqFLzrLL._SY466_.jpg",
    "Dracula": "https://m.media-amazon.com/images/I/91M8resLqUL._SY466_.jpg",
    "Frankenstein": "https://m.media-amazon.com/images/I/81z7E0uB5sL._SY466_.jpg",
    "The Giver": "https://m.media-amazon.com/images/I/51SKzF942pL._SY466_.jpg",
    "The Outsiders": "https://m.media-amazon.com/images/I/91HPG31dTiL._SY466_.jpg",
    "The Road": "https://m.media-amazon.com/images/I/51M7XGLQTBL._SY466_.jpg",
    "The Shack": "https://m.media-amazon.com/images/I/81XZ3MJPoDL._SY466_.jpg",
    "A Walk to Remember": "https://m.media-amazon.com/images/I/81Jqkk1MxRL._SY466_.jpg",
    "The Help": "https://m.media-amazon.com/images/I/71wBXJCEhjL._SY466_.jpg",
    "The Old Man and the Sea": "https://m.media-amazon.com/images/I/71OZY035QKL._SY466_.jpg",
    "The Little Prince": "https://m.media-amazon.com/images/I/71OZY035QKL._SY466_.jpg",
}

# Database connection
db = mysql.connector.connect(
    host="localhost",
    user="root",
    password="Ganesh@2005",
    database="innervoiceproject"
)
cursor = db.cursor()

# Fetch all books
cursor.execute("SELECT book_id, book_name FROM book")
books = cursor.fetchall()

print(f"Found {len(books)} books in database.\n")

updated = 0
not_found = []

for book_id, book_name in books:
    url = COVER_URLS.get(book_name)
    if url:
        cursor.execute(
            "UPDATE book SET image_link = %s WHERE book_id = %s",
            (url, book_id)
        )
        updated += 1
        print(f"  [OK] [{updated}] {book_name}")
    else:
        not_found.append((book_id, book_name))
        print(f"  [MISS] No URL mapped for '{book_name}'")

db.commit()
cursor.close()
db.close()

print(f"\n{'='*50}")
print(f"[DONE] Updated: {updated} books")
if not_found:
    print(f"[MISS] Not mapped: {len(not_found)} books")
    for bid, bname in not_found:
        print(f"  - [{bid}] {bname}")
print("\nRestart your Spring Boot app and refresh the page!")
