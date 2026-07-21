package com.InnerVoice.InnerVoiceProject.Controller;

import com.InnerVoice.InnerVoiceProject.Services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AdminController — REST API for all admin utility operations.
 *
 * Replaces the following Python scripts:
 *
 *  Endpoint                              | Python Script Replaced
 *  ──────────────────────────────────────┼──────────────────────────────────────
 *  PUT  /admin/covers/amazon             | update_covers.py
 *  PUT  /admin/covers/openlibrary        | fix_images_instant.py
 *  POST /admin/covers/download-local     | download_and_update_local.py
 *  POST /admin/covers/download-missing   | download_missing.py
 *  POST /admin/covers/download-google    | download_covers.py
 *  POST /admin/audio/generate            | generate_audio.py
 *  POST /admin/audio/generate-stories    | generate_stories.py
 */
@RestController
@RequestMapping("/admin")
@CrossOrigin
public class AdminController {

    @Autowired
    private AdminService adminService;

    // ─────────────────────────────────────────────────────────────────────────
    // COVER IMAGES — DB-only URL updates (no file download)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Replaces: update_covers.py
     *
     * Updates every book's image_link in the DB to the corresponding Amazon URL.
     * Books not found in the map get a ui-avatars.com generated avatar fallback.
     *
     * Usage: PUT http://localhost:8080/admin/covers/amazon
     */
    @PutMapping("/covers/amazon")
    public ResponseEntity<Map<String, Object>> updateCoversToAmazon() {
        Map<String, Object> result = adminService.updateCoversToAmazonUrls();
        return ResponseEntity.ok(result);
    }

    /**
     * Replaces: fix_images_instant.py
     *
     * Updates every book's image_link in the DB to the Open Library ISBN-based URL.
     * Books not found in the map get a ui-avatars.com generated avatar fallback.
     *
     * Usage: PUT http://localhost:8080/admin/covers/openlibrary
     */
    @PutMapping("/covers/openlibrary")
    public ResponseEntity<Map<String, Object>> updateCoversToOpenLibrary() {
        Map<String, Object> result = adminService.updateCoversToOpenLibraryUrls();
        return ResponseEntity.ok(result);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // COVER IMAGES — Download files to local static/images folder
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Replaces: download_and_update_local.py
     *
     * Downloads Amazon cover images to src/main/resources/static/images/ and
     * updates DB image_link to /images/cover_<safe_name>.jpg
     *
     * Usage: POST http://localhost:8080/admin/covers/download-local
     */
    @PostMapping("/covers/download-local")
    public ResponseEntity<Map<String, Object>> downloadCoversLocally() {
        try {
            Map<String, Object> result = adminService.downloadAndSaveCoversLocally();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Replaces: download_missing.py
     *
     * Finds books whose image_link is NOT a local /images/ path, fetches covers
     * from Open Library API, downloads them, and updates DB.
     *
     * Usage: POST http://localhost:8080/admin/covers/download-missing
     */
    @PostMapping("/covers/download-missing")
    public ResponseEntity<Map<String, Object>> downloadMissingCovers() {
        try {
            Map<String, Object> result = adminService.downloadMissingCoversFromOpenLibrary();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Replaces: download_covers.py
     *
     * Fetches cover thumbnails from Google Books API by title+author,
     * downloads them to local static/images/ and updates DB image_link.
     *
     * Usage: POST http://localhost:8080/admin/covers/download-google
     */
    @PostMapping("/covers/download-google")
    public ResponseEntity<Map<String, Object>> downloadCoversFromGoogle() {
        try {
            Map<String, Object> result = adminService.downloadCoversFromGoogleBooks();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // AUDIO GENERATION
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Replaces: generate_audio.py
     *
     * Generates a welcome-message TTS MP3 audio file for each book using
     * VoiceRSS REST API, saves to static/audio/, updates DB audio_file_link.
     * Requires admin.voicerss.api-key property set in application.properties.
     *
     * Usage: POST http://localhost:8080/admin/audio/generate
     */
    @PostMapping("/audio/generate")
    public ResponseEntity<Map<String, Object>> generateAudio() {
        try {
            Map<String, Object> result = adminService.generateAudio();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Replaces: generate_stories.py
     *
     * Fetches real book summaries from Wikipedia REST API, builds rich TTS scripts,
     * generates MP3 via VoiceRSS, saves to static/audio/, updates DB audio_file_link.
     * Requires admin.voicerss.api-key property set in application.properties.
     *
     * Usage: POST http://localhost:8080/admin/audio/generate-stories
     */
    @PostMapping("/audio/generate-stories")
    public ResponseEntity<Map<String, Object>> generateStoriesAudio() {
        try {
            Map<String, Object> result = adminService.generateStoriesAudio();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
