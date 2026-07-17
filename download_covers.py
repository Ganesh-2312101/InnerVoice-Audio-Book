import re, os, requests

# Project root directory
PROJECT_ROOT = r"c:/Users/mgane/Projects/InnerVoiceUpgrade"
SQL_FILE = os.path.join(PROJECT_ROOT, "books.sql")
# Directory where images will be stored (served as static resources)
IMAGE_DIR = os.path.join(PROJECT_ROOT, "src/main/resources/static/images")

os.makedirs(IMAGE_DIR, exist_ok=True)

# Regex to parse INSERT statements and extract author, title, and image filename
insert_regex = re.compile(r"INSERT INTO book\([^)]*\) VALUES\('([^']+)','([^']+)','[^']*','[^']*','([^']+)','[^']*'\);")

with open(SQL_FILE, "r", encoding="utf-8") as f:
    for line in f:
        match = insert_regex.search(line)
        if not match:
            continue
        author, title, image_name = match.group(1), match.group(2), match.group(3)
        image_path = os.path.join(IMAGE_DIR, image_name)
        if os.path.exists(image_path):
            # Skip if already downloaded
            continue
        # Build query for Google Books API
        query = f"intitle:{title}+inauthor:{author}".replace(' ', '+')
        api_url = f"https://www.googleapis.com/books/v1/volumes?q={query}&maxResults=1"
        try:
            resp = requests.get(api_url, timeout=10)
            data = resp.json()
            items = data.get('items')
            if not items:
                continue
            volume_info = items[0].get('volumeInfo', {})
            thumbnail = volume_info.get('imageLinks', {}).get('thumbnail')
            if not thumbnail:
                continue
            # Ensure HTTPS and remove extra params
            thumbnail = thumbnail.replace('http://', 'https://').split('&')[0]
            img_resp = requests.get(thumbnail, timeout=10)
            if img_resp.status_code == 200:
                with open(image_path, 'wb') as img_file:
                    img_file.write(img_resp.content)
                print(f"✅ Downloaded {image_name} for '{title}'")
        except Exception as e:
            print(f"⚠️ Error fetching cover for '{title}': {e}")
