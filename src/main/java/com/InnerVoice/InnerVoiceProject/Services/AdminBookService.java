package com.InnerVoice.InnerVoiceProject.Services;

import com.InnerVoice.InnerVoiceProject.Model.Book;
import com.InnerVoice.InnerVoiceProject.Repositories.BookRepository;
import com.InnerVoice.InnerVoiceProject.Util.CoverUrlConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * AdminBookService — Java Spring Boot replacement for all 6 Python scripts:
 *
 *  1. download_and_update_local.py  → downloadAndUpdateCoversLocal()
 *  2. update_covers.py              → updateCoversToAmazonUrls()
 *  3. fix_images_instant.py         → updateCoversToOpenLibraryUrls()
 *  4. download_missing.py           → downloadMissingCovers()
 *  5. generate_audio.py             → generateAudioForAllBooks()
 *  6. generate_stories.py           → generateStoriesAudio()
 */
@Service
public class AdminBookService {

    @Autowired
    private BookRepository bookRepository;

    // Base path of static resources — reads from application.properties,
    // defaults to the standard Spring Boot location.
    @Value("${app.static-resource-path:src/main/resources/static}")
    private String staticResourcePath;

    // ─────────────────────────────────────────────────────────────────────────
    // 1. Replaces download_and_update_local.py
    //    Downloads cover images from Amazon URLs → saves locally → updates DB
    // ─────────────────────────────────────────────────────────────────────────
    public Map<String, Object> downloadAndUpdateCoversLocal() {
        Path imageDir = Paths.get(staticResourcePath, "images");
        try { Files.createDirectories(imageDir); } catch (IOException ignored) {}

        List<Book> books = bookRepository.findAll();
        int updated = 0;
        List<String> missed = new ArrayList<>();
        List<String> failed = new ArrayList<>();

        for (Book book : books) {
            String url = CoverUrlConstants.AMAZON_COVER_URLS.get(book.getBookName());
            if (url == null) { missed.add(book.getBookName()); continue; }

            String safeName = toSafeFilename(book.getBookName());
            String filename = "cover_" + safeName + ".jpg";
            Path filepath = imageDir.resolve(filename);

            try {
                if (!Files.exists(filepath)) {
                    downloadFile(url, filepath,
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
                }
                book.setImageLink("/images/" + filename);
                bookRepository.save(book);
                updated++;
            } catch (Exception e) {
                failed.add(book.getBookName() + ": " + e.getMessage());
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("updated", updated);
        result.put("missed", missed);
        result.put("failed", failed);
        return result;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 2. Replaces update_covers.py
    //    Updates DB image_link to hardcoded Amazon URLs (no download)
    // ─────────────────────────────────────────────────────────────────────────
    public Map<String, Object> updateCoversToAmazonUrls() {
        List<Book> books = bookRepository.findAll();
        int updated = 0;
        List<String> notFound = new ArrayList<>();

        for (Book book : books) {
            String url = CoverUrlConstants.AMAZON_COVER_URLS.get(book.getBookName());
            if (url != null) {
                book.setImageLink(url);
                bookRepository.save(book);
                updated++;
            } else {
                notFound.add(book.getBookName());
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("updated", updated);
        result.put("notMapped", notFound);
        return result;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 3. Replaces fix_images_instant.py
    //    Updates DB image_link to Open Library ISBN URLs.
    //    For books not in the map → uses ui-avatars.com as fallback.
    // ─────────────────────────────────────────────────────────────────────────
    public Map<String, Object> updateCoversToOpenLibraryUrls() {
        List<Book> books = bookRepository.findAll();
        int updated = 0;

        for (Book book : books) {
            String url = CoverUrlConstants.OPEN_LIBRARY_COVER_URLS.get(book.getBookName());
            if (url == null) {
                // Fallback: generated avatar image
                url = "https://ui-avatars.com/api/?name="
                        + URLEncoder.encode(book.getBookName(), StandardCharsets.UTF_8)
                        + "&size=500&background=random&color=fff&font-size=0.33";
            }
            book.setImageLink(url);
            bookRepository.save(book);
            updated++;
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("updated", updated);
        result.put("message", "All books updated with Open Library / fallback URLs.");
        return result;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 4. Replaces download_missing.py
    //    Fetches books whose image_link is NOT already a local /images/ path,
    //    searches Open Library, downloads the cover, updates DB.
    // ─────────────────────────────────────────────────────────────────────────
    public Map<String, Object> downloadMissingCovers() {
        Path imageDir = Paths.get(staticResourcePath, "images");
        try { Files.createDirectories(imageDir); } catch (IOException ignored) {}

        // Find books that don't have a local image yet
        List<Book> books = bookRepository.findAll().stream()
                .filter(b -> b.getImageLink() == null || !b.getImageLink().startsWith("/images/"))
                .toList();

        int updated = 0;
        List<String> failed = new ArrayList<>();

        for (Book book : books) {
            String safeName = toSafeFilename(book.getBookName());
            String filename  = "cover_" + safeName + ".jpg";
            Path   filepath  = imageDir.resolve(filename);

            try {
                String imgUrl = null;

                // 1. Try Open Library search API
                String searchUrl = "https://openlibrary.org/search.json?title="
                        + URLEncoder.encode(book.getBookName(), StandardCharsets.UTF_8)
                        + "&author="
                        + URLEncoder.encode(book.getBookAuthor() != null ? book.getBookAuthor() : "", StandardCharsets.UTF_8)
                        + "&limit=1";

                String searchJson = httpGet(searchUrl);
                String coverId = extractJsonValue(searchJson, "cover_i");

                if (coverId != null && !coverId.isBlank()) {
                    imgUrl = "https://covers.openlibrary.org/b/id/" + coverId + "-L.jpg";
                } else {
                    // Fallback: avatar
                    imgUrl = "https://ui-avatars.com/api/?name="
                            + URLEncoder.encode(book.getBookName(), StandardCharsets.UTF_8)
                            + "&size=500&background=random&color=fff&font-size=0.33";
                }

                // 2. Download the image
                if (!Files.exists(filepath)) {
                    downloadFile(imgUrl, filepath, "Mozilla/5.0");
                }

                // 3. Update DB
                book.setImageLink("/images/" + filename);
                bookRepository.save(book);
                updated++;

                Thread.sleep(500); // Be nice to Open Library API

            } catch (Exception e) {
                failed.add(book.getBookName() + ": " + e.getMessage());
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalMissing", books.size());
        result.put("updated", updated);
        result.put("failed", failed);
        return result;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 5. Replaces generate_audio.py
    //    Generates a greeting audio file per book using Google TTS,
    //    saves as /audio/audio_{id}_{safeName}.mp3, updates DB.
    // ─────────────────────────────────────────────────────────────────────────
    public Map<String, Object> generateAudioForAllBooks() {
        Path audioDir = Paths.get(staticResourcePath, "audio");
        try { Files.createDirectories(audioDir); } catch (IOException ignored) {}

        List<Book> books = bookRepository.findAll();
        int updated = 0;
        List<String> failed = new ArrayList<>();

        for (Book book : books) {
            try {
                String safeAlpha  = book.getBookName().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
                String filename   = "audio_" + book.getBookId() + "_" + safeAlpha + ".mp3";
                Path   filepath   = audioDir.resolve(filename);

                String script = "Welcome to Inner Voice! You are now listening to "
                        + book.getBookName() + " by " + book.getBookAuthor()
                        + ". Relax and enjoy the book!";

                if (!Files.exists(filepath)) {
                    downloadGoogleTts(script, filepath);
                }

                book.setAudioFileLink("/audio/" + filename);
                bookRepository.save(book);
                updated++;

            } catch (Exception e) {
                failed.add(book.getBookName() + ": " + e.getMessage());
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("updated", updated);
        result.put("failed", failed);
        result.put("note", "Restart Spring Boot to serve newly generated audio files.");
        return result;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 6. Replaces generate_stories.py
    //    Fetches a Wikipedia summary for each book, generates audio via
    //    Google TTS, overwrites existing mp3, does NOT update DB path
    //    (filenames are identical to generate_audio, so path stays valid).
    // ─────────────────────────────────────────────────────────────────────────
    public Map<String, Object> generateStoriesAudio() {
        Path audioDir = Paths.get(staticResourcePath, "audio");
        try { Files.createDirectories(audioDir); } catch (IOException ignored) {}

        List<Book> books = bookRepository.findAll();
        int updated = 0;
        List<String> failed = new ArrayList<>();

        for (Book book : books) {
            try {
                String safeAlpha = book.getBookName().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
                String filename  = "audio_" + book.getBookId() + "_" + safeAlpha + ".mp3";
                Path   filepath  = audioDir.resolve(filename);

                // 1. Try Wikipedia REST API summary
                String summary = fetchWikipediaSummary(book.getBookName(), book.getBookAuthor());

                // 2. Build script text
                String script;
                if (summary != null && summary.length() > 20) {
                    script = "Welcome to Inner Voice. You are listening to "
                            + book.getBookName() + " by " + book.getBookAuthor()
                            + ". Here is a summary of the story. " + summary
                            + ". We hope you enjoy this audiobook.";
                } else {
                    script = "Welcome to Inner Voice. You are listening to "
                            + book.getBookName() + " by " + book.getBookAuthor()
                            + ". This is a fascinating story that explores deep themes "
                            + "and captivating narratives. Dive into the world of "
                            + book.getBookName() + " and discover the brilliant ideas "
                            + "written by " + book.getBookAuthor()
                            + ". Relax, get comfortable, and enjoy the book.";
                }

                // 3. Generate audio (overwrite existing)
                downloadGoogleTts(script, filepath);

                // 4. Ensure DB path is up-to-date
                book.setAudioFileLink("/audio/" + filename);
                bookRepository.save(book);
                updated++;

                Thread.sleep(500); // Be nice to Wikipedia API

            } catch (Exception e) {
                failed.add(book.getBookName() + ": " + e.getMessage());
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("updated", updated);
        result.put("failed", failed);
        result.put("note", "Restart Spring Boot to serve updated MP3 files.");
        return result;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PRIVATE HELPERS
    // ─────────────────────────────────────────────────────────────────────────

    /** Convert a book name to a safe lowercase filename segment (spaces → _). */
    private String toSafeFilename(String name) {
        return name.chars()
                .filter(c -> Character.isLetterOrDigit(c) || c == ' ')
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString()
                .trim()
                .replace(' ', '_')
                .toLowerCase();
    }

    /** Download a file from a URL and save it to a local path. */
    private void downloadFile(String fileUrl, Path dest, String userAgent) throws IOException {
        URL url = URI.create(fileUrl).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("User-Agent", userAgent);
        conn.setConnectTimeout(10_000);
        conn.setReadTimeout(15_000);
        if (conn.getResponseCode() != 200) {
            throw new IOException("HTTP " + conn.getResponseCode() + " for " + fileUrl);
        }
        try (InputStream in = conn.getInputStream()) {
            Files.copy(in, dest, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Download audio from Google Translate TTS (same endpoint gTTS uses internally).
     * Splits long text into ≤200-char chunks to stay within the URL limit.
     */
    private void downloadGoogleTts(String text, Path dest) throws IOException, InterruptedException {
        // Split into sentences/chunks of ≤200 chars
        List<String> chunks = splitText(text, 200);
        List<byte[]> audioParts = new ArrayList<>();

        for (String chunk : chunks) {
            String encoded = URLEncoder.encode(chunk, StandardCharsets.UTF_8);
            String ttsUrl  = "https://translate.google.com/translate_tts"
                    + "?ie=UTF-8&q=" + encoded
                    + "&tl=en&client=tw-ob";

            URL url = URI.create(ttsUrl).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            conn.setRequestProperty("Referer", "https://translate.google.com/");
            conn.setConnectTimeout(10_000);
            conn.setReadTimeout(15_000);

            if (conn.getResponseCode() == 200) {
                audioParts.add(conn.getInputStream().readAllBytes());
            }
            Thread.sleep(300); // avoid rate-limiting between chunks
        }

        // Concatenate all mp3 chunks into one file (mp3 frames are self-contained)
        try (OutputStream out = Files.newOutputStream(dest)) {
            for (byte[] part : audioParts) { out.write(part); }
        }
    }

    /** Split text into chunks not exceeding maxLen characters, preferring word boundaries. */
    private List<String> splitText(String text, int maxLen) {
        List<String> chunks = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder current = new StringBuilder();
        for (String word : words) {
            if (current.length() + word.length() + 1 > maxLen) {
                if (!current.isEmpty()) { chunks.add(current.toString().trim()); }
                current = new StringBuilder();
            }
            if (!current.isEmpty()) current.append(' ');
            current.append(word);
        }
        if (!current.isEmpty()) chunks.add(current.toString().trim());
        return chunks;
    }

    /** Call Wikipedia REST summary API and return the extract text. */
    private String fetchWikipediaSummary(String bookName, String author) {
        try {
            // Try "{bookName} (novel)" first, then plain book name
            String[] titles = {
                bookName.replace(" ", "_") + "_(novel)",
                bookName.replace(" ", "_")
            };
            for (String title : titles) {
                String apiUrl = "https://en.wikipedia.org/api/rest_v1/page/summary/"
                        + URLEncoder.encode(title, StandardCharsets.UTF_8);
                String json = httpGet(apiUrl);
                if (json != null && json.contains("\"extract\"")) {
                    String extract = extractJsonValue(json, "extract");
                    if (extract != null && extract.length() > 20) {
                        // Return first 3 sentences (approx.)
                        String[] sentences = extract.split("(?<=[.!?])\\s+");
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < Math.min(3, sentences.length); i++) {
                            sb.append(sentences[i]).append(' ');
                        }
                        return sb.toString().trim();
                    }
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    /** Simple HTTP GET → returns response body as a String. */
    private String httpGet(String urlStr) throws IOException {
        URL url = URI.create(urlStr).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("User-Agent", "InnerVoiceApp/1.0");
        conn.setConnectTimeout(10_000);
        conn.setReadTimeout(10_000);
        if (conn.getResponseCode() != 200) return null;
        try (InputStream in = conn.getInputStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    /**
     * Minimal JSON value extractor — pulls the first occurrence of "key":"value"
     * or "key":number from a JSON string (no external library required).
     */
    private String extractJsonValue(String json, String key) {
        if (json == null) return null;
        String search = "\"" + key + "\":";
        int idx = json.indexOf(search);
        if (idx < 0) return null;
        int start = idx + search.length();
        while (start < json.length() && json.charAt(start) == ' ') start++;
        if (start >= json.length()) return null;

        if (json.charAt(start) == '"') {
            // String value
            int end = json.indexOf('"', start + 1);
            return end > start ? json.substring(start + 1, end) : null;
        } else {
            // Numeric / boolean value
            int end = start;
            while (end < json.length() && ",}\n\r".indexOf(json.charAt(end)) < 0) end++;
            return json.substring(start, end).trim();
        }
    }
}
