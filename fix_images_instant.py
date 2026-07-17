import mysql.connector

# Map of book name to Open Library ISBN cover URL (no hotlink blocks!)
COVER_URLS = {
    # ========== Productivity ==========
    "Atomic Habits": "https://covers.openlibrary.org/b/isbn/9780735211292-L.jpg",
    "Deep Work": "https://covers.openlibrary.org/b/isbn/9781455586691-L.jpg",
    "The 7 Habits of Highly Effective People": "https://covers.openlibrary.org/b/isbn/9781982137274-L.jpg",
    "The 7 Habits": "https://covers.openlibrary.org/b/isbn/9781982137274-L.jpg",
    "Eat That Frog": "https://covers.openlibrary.org/b/isbn/9781626569416-L.jpg",
    "The 5 AM Club": "https://covers.openlibrary.org/b/isbn/9781443456623-L.jpg",
    "Getting Things Done": "https://covers.openlibrary.org/b/isbn/9780143126560-L.jpg",
    "Think and Grow Rich": "https://covers.openlibrary.org/b/isbn/9781585424337-L.jpg",
    "The One Thing": "https://covers.openlibrary.org/b/isbn/9781885167774-L.jpg",
    "Mindset": "https://covers.openlibrary.org/b/isbn/9780345472328-L.jpg",
    "The Power of Habit": "https://covers.openlibrary.org/b/isbn/9781400069286-L.jpg",
    "Rich Dad Poor Dad": "https://covers.openlibrary.org/b/isbn/9781612680194-L.jpg",
    "The Magic of Thinking Big": "https://covers.openlibrary.org/b/isbn/9780671646783-L.jpg",
    "The Compound Effect": "https://covers.openlibrary.org/b/isbn/9781593157135-L.jpg",
    "Essentialism": "https://covers.openlibrary.org/b/isbn/9780804137386-L.jpg",
    "The Miracle Morning": "https://covers.openlibrary.org/b/isbn/9780979019715-L.jpg",
    "The Success Principles": "https://covers.openlibrary.org/b/isbn/9780060594893-L.jpg",
    "Start With Why": "https://covers.openlibrary.org/b/isbn/9781591846444-L.jpg",
    "Grit": "https://covers.openlibrary.org/b/isbn/9781501111105-L.jpg",
    "Outliers": "https://covers.openlibrary.org/b/isbn/9780316017930-L.jpg",
    "Drive": "https://covers.openlibrary.org/b/isbn/9781594484803-L.jpg",
    "Ikigai": "https://covers.openlibrary.org/b/isbn/9780143130727-L.jpg",
    "Unlimited Power": "https://covers.openlibrary.org/b/isbn/9780684845777-L.jpg",
    "Can't Hurt Me": "https://covers.openlibrary.org/b/isbn/9781544512273-L.jpg",
    "Peak": "https://covers.openlibrary.org/b/isbn/9780544456235-L.jpg",
    "Make Time": "https://covers.openlibrary.org/b/isbn/9780525572428-L.jpg",

    # ========== Science Fiction ==========
    "Dune": "https://covers.openlibrary.org/b/isbn/9780441172719-L.jpg",
    "Foundation": "https://covers.openlibrary.org/b/isbn/9780553293357-L.jpg",
    "The Martian": "https://covers.openlibrary.org/b/isbn/9780553418026-L.jpg",
    "The Time Machine": "https://covers.openlibrary.org/b/isbn/9780486284729-L.jpg",
    "Ender's Game": "https://covers.openlibrary.org/b/isbn/9780812550702-L.jpg",
    "Ready Player One": "https://covers.openlibrary.org/b/isbn/9780307887443-L.jpg",
    "Neuromancer": "https://covers.openlibrary.org/b/isbn/9780441569595-L.jpg",
    "I Robot": "https://covers.openlibrary.org/b/isbn/9780553294385-L.jpg",
    "Fahrenheit 451": "https://covers.openlibrary.org/b/isbn/9781451673319-L.jpg",
    "Brave New World": "https://covers.openlibrary.org/b/isbn/9780060850524-L.jpg",
    "Snow Crash": "https://covers.openlibrary.org/b/isbn/9780553380958-L.jpg",
    "Hyperion": "https://covers.openlibrary.org/b/isbn/9780553283686-L.jpg",
    "Childhood's End": "https://covers.openlibrary.org/b/isbn/9780345347954-L.jpg",
    "The Forever War": "https://covers.openlibrary.org/b/isbn/9780312536633-L.jpg",
    "Solaris": "https://covers.openlibrary.org/b/isbn/9780156027601-L.jpg",
    "The Expanse": "https://covers.openlibrary.org/b/isbn/9780316129084-L.jpg",
    "Red Rising": "https://covers.openlibrary.org/b/isbn/9780345539786-L.jpg",
    "Project Hail Mary": "https://covers.openlibrary.org/b/isbn/9780593135204-L.jpg",
    "Ringworld": "https://covers.openlibrary.org/b/isbn/9780345333926-L.jpg",
    "The Left Hand of Darkness": "https://covers.openlibrary.org/b/isbn/9780441478125-L.jpg",
    "Do Androids Dream": "https://covers.openlibrary.org/b/isbn/9780345404473-L.jpg",
    "2001 A Space Odyssey": "https://covers.openlibrary.org/b/isbn/9780451457998-L.jpg",
    "The Moon is a Harsh Mistress": "https://covers.openlibrary.org/b/isbn/9780312863555-L.jpg",
    "The Three Body Problem": "https://covers.openlibrary.org/b/isbn/9780765382030-L.jpg",
    "The Invisible Man": "https://covers.openlibrary.org/b/isbn/9780486270715-L.jpg",

    # ========== Crime Thrillers ==========
    "Sherlock Holmes": "https://covers.openlibrary.org/b/isbn/9780140439083-L.jpg",
    "Gone Girl": "https://covers.openlibrary.org/b/isbn/9780307588371-L.jpg",
    "The Da Vinci Code": "https://covers.openlibrary.org/b/isbn/9780307474278-L.jpg",
    "The Silent Patient": "https://covers.openlibrary.org/b/isbn/9781250301697-L.jpg",
    "Murder on the Orient Express": "https://covers.openlibrary.org/b/isbn/9780062693662-L.jpg",
    "The Firm": "https://covers.openlibrary.org/b/isbn/9780440245926-L.jpg",
    "The Client": "https://covers.openlibrary.org/b/isbn/9780440213529-L.jpg",
    "Killing Floor": "https://covers.openlibrary.org/b/isbn/9780515153651-L.jpg",
    "The Girl with the Dragon Tattoo": "https://covers.openlibrary.org/b/isbn/9780307454546-L.jpg",
    "Angels and Demons": "https://covers.openlibrary.org/b/isbn/9781416524793-L.jpg",
    "Inferno": "https://covers.openlibrary.org/b/isbn/9781400079155-L.jpg",
    "Origin": "https://covers.openlibrary.org/b/isbn/9780385514231-L.jpg",
    "Digital Fortress": "https://covers.openlibrary.org/b/isbn/9780312944926-L.jpg",
    "Presumed Innocent": "https://covers.openlibrary.org/b/isbn/9780446350983-L.jpg",
    "The Appeal": "https://covers.openlibrary.org/b/isbn/9780385342926-L.jpg",
    "The Broker": "https://covers.openlibrary.org/b/isbn/9780440241584-L.jpg",
    "The Partner": "https://covers.openlibrary.org/b/isbn/9780440224761-L.jpg",
    "The Pelican Brief": "https://covers.openlibrary.org/b/isbn/9780440214052-L.jpg",
    "A Time To Kill": "https://covers.openlibrary.org/b/isbn/9780440211723-L.jpg",
    "The Runaway Jury": "https://covers.openlibrary.org/b/isbn/9780440221470-L.jpg",
    "The Last Juror": "https://covers.openlibrary.org/b/isbn/9780440241577-L.jpg",
    "The Testament": "https://covers.openlibrary.org/b/isbn/9780440234746-L.jpg",
    "The Reversal": "https://covers.openlibrary.org/b/isbn/9780316069489-L.jpg",
    "The Racketeer": "https://covers.openlibrary.org/b/isbn/9780345530561-L.jpg",
    "The Whistler": "https://covers.openlibrary.org/b/isbn/9781101967683-L.jpg",

    # ========== Novels ==========
    "The Alchemist": "https://covers.openlibrary.org/b/isbn/9780062315007-L.jpg",
    "1984": "https://covers.openlibrary.org/b/isbn/9780451524935-L.jpg",
    "Pride and Prejudice": "https://covers.openlibrary.org/b/isbn/9780141439518-L.jpg",
    "The Hobbit": "https://covers.openlibrary.org/b/isbn/9780547928227-L.jpg",
    "The Great Gatsby": "https://covers.openlibrary.org/b/isbn/9780743273565-L.jpg",
    "To Kill a Mockingbird": "https://covers.openlibrary.org/b/isbn/9780060935467-L.jpg",
    "The Kite Runner": "https://covers.openlibrary.org/b/isbn/9781594631931-L.jpg",
    "The Book Thief": "https://covers.openlibrary.org/b/isbn/9780375842207-L.jpg",
    "Little Women": "https://covers.openlibrary.org/b/isbn/9780147514011-L.jpg",
    "The Fault in Our Stars": "https://covers.openlibrary.org/b/isbn/9780525478812-L.jpg",
    "The Catcher in the Rye": "https://covers.openlibrary.org/b/isbn/9780316769488-L.jpg",
    "The Hunger Games": "https://covers.openlibrary.org/b/isbn/9780439023481-L.jpg",
    "Life of Pi": "https://covers.openlibrary.org/b/isbn/9780156027328-L.jpg",
    "Animal Farm": "https://covers.openlibrary.org/b/isbn/9780451526342-L.jpg",
    "Jane Eyre": "https://covers.openlibrary.org/b/isbn/9780141441146-L.jpg",
    "Dracula": "https://covers.openlibrary.org/b/isbn/9780141439846-L.jpg",
    "Frankenstein": "https://covers.openlibrary.org/b/isbn/9780141439471-L.jpg",
    "The Giver": "https://covers.openlibrary.org/b/isbn/9780544336261-L.jpg",
    "The Outsiders": "https://covers.openlibrary.org/b/isbn/9780142407332-L.jpg",
    "The Road": "https://covers.openlibrary.org/b/isbn/9780307387899-L.jpg",
    "The Shack": "https://covers.openlibrary.org/b/isbn/9780964729230-L.jpg",
    "A Walk to Remember": "https://covers.openlibrary.org/b/isbn/9780446693806-L.jpg",
    "The Help": "https://covers.openlibrary.org/b/isbn/9780399155345-L.jpg",
    "The Old Man and the Sea": "https://covers.openlibrary.org/b/isbn/9780684801223-L.jpg",
    "The Little Prince": "https://covers.openlibrary.org/b/isbn/9780156012195-L.jpg",
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

updated = 0

for book_id, book_name in books:
    url = COVER_URLS.get(book_name)
    if not url:
        # Fallback to generated image
        import urllib.parse
        url = f"https://ui-avatars.com/api/?name={urllib.parse.quote(book_name)}&size=500&background=random&color=fff&font-size=0.33"

    cursor.execute(
        "UPDATE book SET image_link = %s WHERE book_id = %s",
        (url, book_id)
    )
    updated += 1
    print(f"[OK] Updated '{book_name}'")

db.commit()
cursor.close()
db.close()

print(f"\n[DONE] Successfully updated {updated} books with direct online URLs.")
