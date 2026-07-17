import os
import requests
import mysql.connector
import time

PROJECT_ROOT = r"c:/Users/mgane/Projects/InnerVoiceUpgrade"
IMAGE_DIR = os.path.join(PROJECT_ROOT, "src/main/resources/static/images")

# Database connection
db = mysql.connector.connect(
    host="localhost",
    user="root",
    password="Ganesh@2005",
    database="innervoiceproject"
)
cursor = db.cursor()

# Fetch books that didn't get updated to local /images/ URL yet
cursor.execute("SELECT book_id, book_name, book_author FROM book WHERE image_link NOT LIKE '/images/%'")
books = cursor.fetchall()

print(f"Found {len(books)} books that need cover images.\n")

updated = 0
failed = []

for book_id, book_name, book_author in books:
    safe_name = "".join([c for c in book_name if c.isalpha() or c.isdigit() or c==' ']).rstrip().replace(" ", "_").lower()
    filename = f"cover_{safe_name}.jpg"
    filepath = os.path.join(IMAGE_DIR, filename)

    try:
        # 1. Search Open Library
        search_url = f"https://openlibrary.org/search.json?title={requests.utils.quote(book_name)}&author={requests.utils.quote(book_author)}&limit=1"
        search_resp = requests.get(search_url, timeout=10)
        docs = search_resp.json().get('docs', [])
        
        if not docs:
            search_url = f"https://openlibrary.org/search.json?title={requests.utils.quote(book_name)}&limit=1"
            search_resp = requests.get(search_url, timeout=10)
            docs = search_resp.json().get('docs', [])

        cover_id = None
        if docs:
            cover_id = docs[0].get('cover_i')

        if not cover_id:
            print(f"[WARN] No cover found on Open Library for '{book_name}'")
            # Create a placeholder image URL using placeholder.com or ui-avatars
            img_url = f"https://ui-avatars.com/api/?name={requests.utils.quote(book_name)}&size=500&background=random&color=fff&font-size=0.33"
        else:
            img_url = f"https://covers.openlibrary.org/b/id/{cover_id}-L.jpg"

        # 2. Download the image
        if not os.path.exists(filepath):
            headers = {'User-Agent': 'Mozilla/5.0'}
            response = requests.get(img_url, headers=headers, stream=True, timeout=10)
            if response.status_code == 200:
                with open(filepath, 'wb') as f:
                    for chunk in response.iter_content(1024):
                        f.write(chunk)
            else:
                print(f"[ERR] Download failed for '{book_name}': HTTP {response.status_code}")
                continue

        # 3. Update database
        local_db_path = f"/images/{filename}"
        cursor.execute(
            "UPDATE book SET image_link = %s WHERE book_id = %s",
            (local_db_path, book_id)
        )
        updated += 1
        print(f"[OK] Downloaded and updated '{book_name}' -> {local_db_path}")

        time.sleep(0.5)  # Be nice to Open Library API

    except Exception as e:
        failed.append(book_name)
        print(f"[ERR] Exception for '{book_name}': {e}")

db.commit()
cursor.close()
db.close()

print(f"\n==================================================")
print(f"[DONE] Successfully updated {updated} remaining books.")
if failed:
    print(f"Failed: {failed}")
