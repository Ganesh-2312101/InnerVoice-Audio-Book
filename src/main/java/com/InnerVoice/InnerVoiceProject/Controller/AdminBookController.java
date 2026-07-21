package com.InnerVoice.InnerVoiceProject.Controller;

import com.InnerVoice.InnerVoiceProject.Services.AdminBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AdminBookController — REST endpoints that replace all 6 Python utility scripts.
 *
 *  Python script                  →  HTTP endpoint
 *  ─────────────────────────────────────────────────────────────────────────────
 *  download_and_update_local.py   →  PUT /admin/books/download-covers-local
 *  update_covers.py               →  PUT /admin/books/update-covers-amazon
 *  fix_images_instant.py          →  PUT /admin/books/update-covers-openlibrary
 *  download_missing.py            →  PUT /admin/books/download-missing-covers
 *  generate_audio.py              →  PUT /admin/books/generate-audio
 *  generate_stories.py            →  PUT /admin/books/generate-stories-audio
 */
@RestController
@RequestMapping("/admin/books")
@CrossOrigin
public class AdminBookController {

    @Autowired
    private AdminBookService adminBookService;

    /**
     * Replaces download_and_update_local.py
     * Downloads book cover images from hardcoded Amazon URLs to the local
     * static/images/ folder and updates the DB image_link to /images/filename.jpg
     */
    @PutMapping("/download-covers-local")
    public ResponseEntity<Map<String, Object>> downloadCoversLocal() {
        Map<String, Object> result = adminBookService.downloadAndUpdateCoversLocal();
        return ResponseEntity.ok(result);
    }

    /**
     * Replaces update_covers.py
     * Updates the DB image_link for all books to hardcoded Amazon CDN URLs
     * (no file download — URLs are set directly in the database).
     */
    @PutMapping("/update-covers-amazon")
    public ResponseEntity<Map<String, Object>> updateCoversAmazon() {
        Map<String, Object> result = adminBookService.updateCoversToAmazonUrls();
        return ResponseEntity.ok(result);
    }

    /**
     * Replaces fix_images_instant.py
     * Updates the DB image_link for all books to Open Library ISBN-based URLs.
     * Books without a known ISBN URL fall back to a generated ui-avatars image.
     */
    @PutMapping("/update-covers-openlibrary")
    public ResponseEntity<Map<String, Object>> updateCoversOpenLibrary() {
        Map<String, Object> result = adminBookService.updateCoversToOpenLibraryUrls();
        return ResponseEntity.ok(result);
    }

    /**
     * Replaces download_missing.py
     * Finds books that don't yet have a local /images/ path, searches Open Library
     * for their cover, downloads it, and updates the DB.
     */
    @PutMapping("/download-missing-covers")
    public ResponseEntity<Map<String, Object>> downloadMissingCovers() {
        Map<String, Object> result = adminBookService.downloadMissingCovers();
        return ResponseEntity.ok(result);
    }

    /**
     * Replaces generate_audio.py
     * Generates a short greeting audio track per book using Google TTS,
     * saves the mp3 to static/audio/, and updates the DB audio_file_link.
     */
    @PutMapping("/generate-audio")
    public ResponseEntity<Map<String, Object>> generateAudio() {
        Map<String, Object> result = adminBookService.generateAudioForAllBooks();
        return ResponseEntity.ok(result);
    }

    /**
     * Replaces generate_stories.py
     * Fetches a Wikipedia summary for each book, generates a rich story audio
     * via Google TTS, overwrites the existing mp3, and updates the DB.
     */
    @PutMapping("/generate-stories-audio")
    public ResponseEntity<Map<String, Object>> generateStoriesAudio() {
        Map<String, Object> result = adminBookService.generateStoriesAudio();
        return ResponseEntity.ok(result);
    }
}
