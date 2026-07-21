package com.InnerVoice.InnerVoiceProject.Services;

import com.InnerVoice.InnerVoiceProject.Model.Book;
import com.InnerVoice.InnerVoiceProject.Repositories.BookRepository;
import com.InnerVoice.InnerVoiceProject.Util.CoverUrlConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Duration;
import java.util.*;
import java.util.logging.Logger;

/**
 * AdminService — Java Spring Boot replacement for all Python utility scripts:
 *
 *  Python Script                   → Replaced by method
 *  ─────────────────────────────────────────────────────
 *  update_covers.py                → updateCoversToAmazonUrls()
 *  fix_images_instant.py           → updateCoversToOpenLibraryUrls()
 *  download_and_update_local.py    → downloadAndSaveCoversLocally()
 *  download_missing.py             → downloadMissingCoversFromOpenLibrary()
 *  download_covers.py              → downloadCoversFromGoogleBooks()
 *  generate_audio.py               → generateAudio()
 *  generate_stories.py             → generateStoriesAudio()
 */
@Service
public class AdminService {

    private static final Logger log = Logger.getLogger(AdminService.class.getName());

    @Autowired
    private BookRepository bookRepository;

    /** Path to static images folder — e.g. src/main/resources/static/images */
    @Value("${admin.static.images-dir:src/main/resources/static/images}")
    private String imagesDirPath;

    /** Path to static audio folder — e.g. src/main/resources/static/audio */
    @Value("${admin.static.audio-dir:src/main/resources/static/audio}")
    private String audioDirPath;

    /** VoiceRSS API key — register free at voicerss.org */
    @Value("${admin.voicerss.api-key:}")
    private String voiceRssApiKey;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .followRedirects(java.net.http.HttpClient.Redirect.ALWAYS)
            .build();

    // ─────────────────────────────────────────────────────────────────────────
    // COVER IMAGE — DB-only URL update methods (no file download)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Replaces: update_covers.py
     * Updates every book's image_link in the DB to the mapped Amazon URL.
     * Books not in the map are skipped.
     */
    public Map<String, Object> updateCoversToAmazonUrls() {
        return updateCoversFromMap(CoverUrlConstants.AMAZON_COVER_URLS, "Amazon");
    }

    /**
     * Replaces: fix_images_instant.py
     * Updates every book's image_link in the DB to the mapped Open Library ISBN URL.
     * Books not in the map get a ui-avatars.com fallback.
     */
    public Map<String, Object> updateCoversToOpenLibraryUrls() {
        return updateCoversFromMap(CoverUrlConstants.OPEN_LIBRARY_COVER_URLS, "Open Library");
    }

    /** Shared helper: update DB image_link from a name→url map. */
    private Map<String, Object> updateCoversFromMap(Map<String, String> urlMap, String source) {
        List<Book> books = bookRepository.findAll();
        int updated = 0;
        List<String> missed = new ArrayList<>();

        for (Book book : books) {
            String url = urlMap.get(book.getBookName());
            if (url != null) {
                book.setImageLink(url);
                updated++;
                log.info("[OK] Updated '" + book.getBookName() + "' → " + url);
            } else {
                // Fallback: generated avatar image
                String fallback = "https://ui-avatars.com/api/?name="
                        + URLEncoder.encode(book.getBookName(), StandardCharsets.UTF_8)
                        + "&size=500&background=random&color=fff&font-size=0.33";
                book.setImageLink(fallback);
                missed.add(book.getBookName());
                log.warning("[FALLBACK] No " + source + " URL for '" + book.getBookName() + "'");
            }
        }
        bookRepository.saveAll(books);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("source", source);
        result.put("totalBooks", books.size());
        result.put("updated", updated);
        result.put("fallback", missed.size());
        result.put("missedBooks", missed);
        return result;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // COVER IMAGE — Download to local static/images folder
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Replaces: download_and_update_local.py
     * Downloads cover images from Amazon URLs to local static/images folder.
     * Updates DB image_link to /images/cover_<safe_name>.jpg
     */
    public Map<String, Object> downloadAndSaveCoversLocally() throws IOException {
        Path imagesDir = ensureDir(imagesDirPath);
        List<Book> books = bookRepository.findAll();
        int downloaded = 0, skipped = 0, failed = 0;
        List<String> failedBooks = new ArrayList<>();

        for (Book book : books) {
            String url = CoverUrlConstants.AMAZON_COVER_URLS.get(book.getBookName());
            if (url == null) {
                log.warning("[MISS] No mapped Amazon URL for '" + book.getBookName() + "'");
                skipped++;
                continue;
            }

            String safeName = toSafeFilename(book.getBookName());
            String filename = "cover_" + safeName + ".jpg";
            Path filepath = imagesDir.resolve(filename);

            try {
                if (!Files.exists(filepath)) {
                    downloadFile(url, filepath);
                    log.info("[DOWNLOADED] '" + book.getBookName() + "' → " + filename);
                } else {
                    log.info("[EXISTS] Skipping download for '" + book.getBookName() + "'");
                }
                book.setImageLink("/images/" + filename);
                downloaded++;
            } catch (Exception e) {
                failedBooks.add(book.getBookName());
                failed++;
                log.severe("[ERR] Download failed for '" + book.getBookName() + "': " + e.getMessage());
            }
        }
        bookRepository.saveAll(books);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("downloaded", downloaded);
        result.put("skipped", skipped);
        result.put("failed", failed);
        result.put("failedBooks", failedBooks);
        return result;
    }

    /**
     * Replaces: download_missing.py
     * Finds books whose image_link does NOT start with /images/, fetches cover
     * from Open Library search API, downloads image, updates DB.
     */
    public Map<String, Object> downloadMissingCoversFromOpenLibrary() throws IOException {
        Path imagesDir = ensureDir(imagesDirPath);

        // Fetch books that still have non-local image links
        List<Book> books = bookRepository.findAll().stream()
                .filter(b -> b.getImageLink() == null || !b.getImageLink().startsWith("/images/"))
                .toList();

        log.info("Found " + books.size() + " books needing cover images.");
        int updated = 0, failed = 0;
        List<String> failedBooks = new ArrayList<>();

        for (Book book : books) {
            String safeName = toSafeFilename(book.getBookName());
            String filename = "cover_" + safeName + ".jpg";
            Path filepath = imagesDir.resolve(filename);

            try {
                String imgUrl = null;

                // 1. Search Open Library
                String searchUrl = "https://openlibrary.org/search.json?title="
                        + URLEncoder.encode(book.getBookName(), StandardCharsets.UTF_8)
                        + "&author=" + URLEncoder.encode(book.getBookAuthor() != null ? book.getBookAuthor() : "", StandardCharsets.UTF_8)
                        + "&limit=1";
                String searchBody = httpGet(searchUrl);
                Long coverId = extractLongField(searchBody, "cover_i");

                if (coverId != null) {
                    imgUrl = "https://covers.openlibrary.org/b/id/" + coverId + "-L.jpg";
                } else {
                    // Fallback: generated avatar
                    imgUrl = "https://ui-avatars.com/api/?name="
                            + URLEncoder.encode(book.getBookName(), StandardCharsets.UTF_8)
                            + "&size=500&background=random&color=fff&font-size=0.33";
                    log.warning("[WARN] No Open Library cover for '" + book.getBookName() + "', using avatar.");
                }

                // 2. Download if not already present
                if (!Files.exists(filepath)) {
                    downloadFile(imgUrl, filepath);
                }

                book.setImageLink("/images/" + filename);
                updated++;
                log.info("[OK] Updated '" + book.getBookName() + "' → /images/" + filename);

                Thread.sleep(500); // Be polite to Open Library API

            } catch (Exception e) {
                failedBooks.add(book.getBookName());
                failed++;
                log.severe("[ERR] Failed for '" + book.getBookName() + "': " + e.getMessage());
            }
        }
        bookRepository.saveAll(books);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("processed", books.size());
        result.put("updated", updated);
        result.put("failed", failed);
        result.put("failedBooks", failedBooks);
        return result;
    }

    /**
     * Replaces: download_covers.py
     * Fetches cover thumbnails from Google Books API by title+author, downloads them locally.
     */
    public Map<String, Object> downloadCoversFromGoogleBooks() throws IOException {
        Path imagesDir = ensureDir(imagesDirPath);
        List<Book> books = bookRepository.findAll();
        int downloaded = 0, skipped = 0, failed = 0;
        List<String> failedBooks = new ArrayList<>();

        for (Book book : books) {
            String author = book.getBookAuthor() != null ? book.getBookAuthor() : "";
            String safeName = toSafeFilename(book.getBookName());
            String filename = "cover_" + safeName + ".jpg";
            Path filepath = imagesDir.resolve(filename);

            if (Files.exists(filepath)) {
                skipped++;
                continue;
            }

            try {
                // Google Books API search
                String query = "intitle:" + URLEncoder.encode(book.getBookName(), StandardCharsets.UTF_8)
                        + "+inauthor:" + URLEncoder.encode(author, StandardCharsets.UTF_8);
                String apiUrl = "https://www.googleapis.com/books/v1/volumes?q=" + query + "&maxResults=1";
                String body = httpGet(apiUrl);

                String thumbnail = extractGoogleBooksThumbnail(body);
                if (thumbnail == null) {
                    log.warning("[SKIP] No thumbnail from Google Books for '" + book.getBookName() + "'");
                    skipped++;
                    continue;
                }

                // Ensure HTTPS
                thumbnail = thumbnail.replace("http://", "https://");
                // Remove extra params
                if (thumbnail.contains("&")) {
                    thumbnail = thumbnail.substring(0, thumbnail.indexOf("&"));
                }

                downloadFile(thumbnail, filepath);
                book.setImageLink("/images/" + filename);
                downloaded++;
                log.info("[OK] Downloaded Google Books cover for '" + book.getBookName() + "'");

            } catch (Exception e) {
                failedBooks.add(book.getBookName());
                failed++;
                log.severe("[ERR] Google Books failed for '" + book.getBookName() + "': " + e.getMessage());
            }
        }
        bookRepository.saveAll(books);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("downloaded", downloaded);
        result.put("skipped", skipped);
        result.put("failed", failed);
        result.put("failedBooks", failedBooks);
        return result;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // AUDIO GENERATION
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Replaces: generate_audio.py
     * Generates a welcome-message TTS audio MP3 for each book using VoiceRSS API.
     * Updates the audio_file_link column in DB.
     */
    public Map<String, Object> generateAudio() throws IOException {
        Path audioDir = ensureDir(audioDirPath);
        List<Book> books = bookRepository.findAll();
        int generated = 0, failed = 0;
        List<String> failedBooks = new ArrayList<>();

        for (Book book : books) {
            String safeAlpha = book.getBookName().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
            String filename = "audio_" + book.getBookId() + "_" + safeAlpha + ".mp3";
            Path filepath = audioDir.resolve(filename);

            String scriptText = "Welcome to Inner Voice! You are now listening to "
                    + book.getBookName() + " by " + (book.getBookAuthor() != null ? book.getBookAuthor() : "Unknown Author")
                    + ". Relax and enjoy the book!";

            try {
                if (!Files.exists(filepath)) {
                    generateMp3WithVoiceRss(scriptText, filepath);
                }
                book.setAudioFileLink("/audio/" + filename);
                generated++;
                log.info("[OK] Generated audio for '" + book.getBookName() + "'");
            } catch (Exception e) {
                failedBooks.add(book.getBookName());
                failed++;
                log.severe("[ERR] Audio generation failed for '" + book.getBookName() + "': " + e.getMessage());
            }
        }
        bookRepository.saveAll(books);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("generated", generated);
        result.put("failed", failed);
        result.put("failedBooks", failedBooks);
        result.put("note", "Restart Spring Boot to serve newly created audio files if they were cached.");
        return result;
    }

    /**
     * Replaces: generate_stories.py
     * Fetches a real Wikipedia summary for each book, builds a richer TTS script,
     * generates MP3 via VoiceRSS, updates DB audio_file_link.
     */
    public Map<String, Object> generateStoriesAudio() throws IOException {
        Path audioDir = ensureDir(audioDirPath);
        List<Book> books = bookRepository.findAll();
        int generated = 0, failed = 0;
        List<String> failedBooks = new ArrayList<>();

        for (Book book : books) {
            String safeAlpha = book.getBookName().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
            String filename = "audio_" + book.getBookId() + "_" + safeAlpha + ".mp3";
            Path filepath = audioDir.resolve(filename);

            try {
                // 1. Fetch Wikipedia summary
                String summary = fetchWikipediaSummary(book.getBookName(), book.getBookAuthor());

                // 2. Build script
                String author = book.getBookAuthor() != null ? book.getBookAuthor() : "Unknown Author";
                String scriptText;
                if (summary != null && summary.length() > 20) {
                    scriptText = "Welcome to Inner Voice. You are listening to " + book.getBookName()
                            + " by " + author + ". Here is a summary of the story. " + summary
                            + ". We hope you enjoy this audiobook.";
                } else {
                    scriptText = "Welcome to Inner Voice. You are listening to " + book.getBookName()
                            + " by " + author + ". This is a fascinating story that explores deep themes "
                            + "and captivating narratives. Dive into the world of " + book.getBookName()
                            + " and discover the brilliant ideas written by " + author
                            + ". Relax, get comfortable, and enjoy the book.";
                }

                // 3. Generate audio (overwrite old one)
                log.info("Generating story audio for '" + book.getBookName() + "'...");
                generateMp3WithVoiceRss(scriptText, filepath);

                book.setAudioFileLink("/audio/" + filename);
                generated++;

                Thread.sleep(500); // Polite rate limit

            } catch (Exception e) {
                failedBooks.add(book.getBookName());
                failed++;
                log.severe("[ERR] Story audio failed for '" + book.getBookName() + "': " + e.getMessage());
            }
        }
        bookRepository.saveAll(books);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("generated", generated);
        result.put("failed", failed);
        result.put("failedBooks", failedBooks);
        result.put("note", "Restart Spring Boot to serve updated audio files if they were cached.");
        return result;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PRIVATE HELPERS
    // ─────────────────────────────────────────────────────────────────────────

    /** Ensure the directory exists, creating it if necessary. */
    private Path ensureDir(String dirPath) throws IOException {
        Path path = Paths.get(dirPath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            log.info("[INIT] Created directory: " + path.toAbsolutePath());
        }
        return path;
    }

    /** Convert a book name to a safe lowercase filename (spaces → underscores, alphanum only). */
    private String toSafeFilename(String bookName) {
        return bookName
                .replaceAll("[^a-zA-Z0-9 ]", "")
                .trim()
                .toLowerCase()
                .replace(" ", "_");
    }

    /** HTTP GET → response body as String. */
    private String httpGet(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "Mozilla/5.0 InnerVoice-SpringBoot")
                .timeout(Duration.ofSeconds(15))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    /** Download a URL's binary content and save it to the given path. */
    private void downloadFile(String url, Path dest) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "Mozilla/5.0 InnerVoice-SpringBoot")
                .timeout(Duration.ofSeconds(20))
                .GET()
                .build();
        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
        if (response.statusCode() != 200) {
            throw new IOException("HTTP " + response.statusCode() + " downloading " + url);
        }
        Files.write(dest, response.body());
    }

    /**
     * Generate an MP3 file using the VoiceRSS REST API.
     * If no API key is configured, writes a .txt script fallback instead.
     */
    private void generateMp3WithVoiceRss(String text, Path dest) throws Exception {
        if (voiceRssApiKey == null || voiceRssApiKey.isBlank()) {
            // No API key — write script as text file as fallback
            Path txtPath = dest.resolveSibling(dest.getFileName().toString().replace(".mp3", ".txt"));
            Files.writeString(txtPath, text, StandardCharsets.UTF_8);
            log.warning("[FALLBACK] VoiceRSS key not set. Saved script text to: " + txtPath);
            return;
        }

        // Truncate to VoiceRSS limit (100 chars free / longer with key)
        String safeText = text.length() > 900 ? text.substring(0, 900) + "." : text;

        String apiUrl = "https://api.voicerss.org/"
                + "?key=" + voiceRssApiKey
                + "&hl=en-us"
                + "&c=MP3"
                + "&f=22khz_16bit_stereo"
                + "&src=" + URLEncoder.encode(safeText, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();
        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() != 200) {
            throw new IOException("VoiceRSS API returned HTTP " + response.statusCode());
        }

        // Check for error response (VoiceRSS returns plain text errors)
        String contentType = response.headers().firstValue("Content-Type").orElse("");
        if (contentType.contains("text/plain")) {
            String errorMsg = new String(response.body(), StandardCharsets.UTF_8);
            throw new IOException("VoiceRSS API error: " + errorMsg);
        }

        Files.write(dest, response.body());
    }

    /**
     * Fetch the first ~3 sentences from Wikipedia for a book title.
     * Uses the Wikipedia REST summary API — no API key required.
     */
    private String fetchWikipediaSummary(String bookName, String author) {
        try {
            // Try title search: "BookName book"
            String encodedTitle = URLEncoder.encode(bookName + " novel", StandardCharsets.UTF_8)
                    .replace("+", "_");
            String wikiUrl = "https://en.wikipedia.org/api/rest_v1/page/summary/" + encodedTitle;
            String body = httpGet(wikiUrl);

            // Extract "extract" field from JSON
            return extractJsonStringField(body, "extract");
        } catch (Exception e) {
            log.warning("[WIKI] Could not fetch summary for '" + bookName + "': " + e.getMessage());
            return null;
        }
    }

    /** Simple JSON string field extractor (avoids adding Jackson dependency for admin util). */
    private String extractJsonStringField(String json, String field) {
        String key = "\"" + field + "\":\"";
        int start = json.indexOf(key);
        if (start < 0) return null;
        start += key.length();
        StringBuilder sb = new StringBuilder();
        boolean escaped = false;
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (escaped) {
                sb.append(c);
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
            } else if (c == '"') {
                break;
            } else {
                sb.append(c);
            }
        }
        String result = sb.toString().trim();
        return result.isEmpty() ? null : result;
    }

    /** Extract a numeric (Long) field from a JSON response body. */
    private Long extractLongField(String json, String field) {
        String key = "\"" + field + "\":";
        int start = json.indexOf(key);
        if (start < 0) return null;
        start += key.length();
        int end = start;
        while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-')) {
            end++;
        }
        String numStr = json.substring(start, end).trim();
        try {
            return Long.parseLong(numStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Extract the thumbnail URL from a Google Books API JSON response. */
    private String extractGoogleBooksThumbnail(String json) {
        String key = "\"thumbnail\":\"";
        int start = json.indexOf(key);
        if (start < 0) return null;
        start += key.length();
        int end = json.indexOf("\"", start);
        return end > start ? json.substring(start, end) : null;
    }
}
