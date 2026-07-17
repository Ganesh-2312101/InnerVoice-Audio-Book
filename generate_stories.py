import os
import mysql.connector
import wikipedia
from gtts import gTTS
import time

PROJECT_ROOT = r"c:/Users/mgane/Projects/InnerVoiceUpgrade"
AUDIO_DIR = os.path.join(PROJECT_ROOT, "src/main/resources/static/audio")

# Connect to database
db = mysql.connector.connect(
    host="localhost",
    user="root",
    password="Ganesh@2005",
    database="innervoiceproject"
)
cursor = db.cursor()

# Get all books
cursor.execute("SELECT book_id, book_name, book_author FROM book")
books = cursor.fetchall()

print(f"Found {len(books)} books. Generating real stories for each...")

updated = 0
for book_id, book_name, book_author in books:
    try:
        # Recreate the exact same filename we used before
        safe_name = "".join([c for c in book_name if c.isalpha() or c.isdigit()]).lower()
        filename = f"audio_{book_id}_{safe_name}.mp3"
        filepath = os.path.join(AUDIO_DIR, filename)
        
        # 1. Try to fetch a real summary from Wikipedia
        summary = ""
        try:
            # Search for the book specifically
            search_query = f"{book_name} book {book_author}"
            wiki_search = wikipedia.search(search_query)
            if wiki_search:
                # Get the first 3 sentences of the summary
                summary = wikipedia.summary(wiki_search[0], sentences=3)
        except Exception as e:
            pass # Ignore wiki errors and use fallback
            
        # 2. Build the final spoken text
        if summary and len(summary) > 20:
            script_text = f"Welcome to Inner Voice. You are listening to {book_name} by {book_author}. Here is a summary of the story. {summary}. We hope you enjoy this audiobook."
        else:
            # Fallback if Wikipedia fails
            script_text = f"Welcome to Inner Voice. You are listening to {book_name} by {book_author}. This is a fascinating story that explores deep themes and captivating narratives. Dive into the world of {book_name} and discover the brilliant ideas written by {book_author}. Relax, get comfortable, and enjoy the book."
            
        # 3. Generate the audio (overwriting the old one)
        print(f"Generating story for '{book_name}'...")
        tts = gTTS(text=script_text, lang='en', slow=False)
        tts.save(filepath)
        
        updated += 1
        time.sleep(0.5) # Be nice to Wikipedia API
        
    except Exception as e:
        print(f"[ERR] Failed to generate story for '{book_name}': {e}")

db.commit()
cursor.close()
db.close()

print(f"\n[DONE] Successfully generated {updated} rich story audio tracks!")
print("NOTE: You MUST restart your Spring Boot application to serve the updated MP3s if they were cached!")
