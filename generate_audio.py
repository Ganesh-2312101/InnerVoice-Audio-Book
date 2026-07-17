import os
import mysql.connector
from gtts import gTTS
import urllib.parse

PROJECT_ROOT = r"c:/Users/mgane/Projects/InnerVoiceUpgrade"
AUDIO_DIR = os.path.join(PROJECT_ROOT, "src/main/resources/static/audio")

# Ensure audio directory exists
os.makedirs(AUDIO_DIR, exist_ok=True)

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

print(f"Found {len(books)} books. Generating unique audio for each...")

updated = 0
for book_id, book_name, book_author in books:
    try:
        # Create a safe filename
        safe_name = "".join([c for c in book_name if c.isalpha() or c.isdigit()]).lower()
        filename = f"audio_{book_id}_{safe_name}.mp3"
        filepath = os.path.join(AUDIO_DIR, filename)
        
        # Generate the voice script
        script_text = f"Welcome to Inner Voice! You are now listening to {book_name} by {book_author}. Relax and enjoy the book!"
        
        # Only generate if it doesn't already exist to save time
        if not os.path.exists(filepath):
            tts = gTTS(text=script_text, lang='en', slow=False)
            tts.save(filepath)
        
        # Update the database
        db_audio_path = f"/audio/{filename}"
        cursor.execute(
            "UPDATE book SET audio_file_link = %s WHERE book_id = %s",
            (db_audio_path, book_id)
        )
        updated += 1
        print(f"[OK] Generated audio for '{book_name}'")
        
    except Exception as e:
        print(f"[ERR] Failed to generate audio for '{book_name}': {e}")

db.commit()
cursor.close()
db.close()

print(f"\n[DONE] Successfully generated {updated} unique audio tracks!")
print("NOTE: You MUST restart your Spring Boot application to be able to listen to these newly created audio files!")
