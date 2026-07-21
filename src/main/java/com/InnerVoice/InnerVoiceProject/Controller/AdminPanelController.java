package com.InnerVoice.InnerVoiceProject.Controller;

import com.InnerVoice.InnerVoiceProject.Model.Admin;
import com.InnerVoice.InnerVoiceProject.Model.Book;
import com.InnerVoice.InnerVoiceProject.Model.User;
import com.InnerVoice.InnerVoiceProject.Services.AdminPanelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AdminPanelController — REST API for Admin Panel operations.
 *
 * Base path: /admin-panel
 *
 * ┌──────────────────────────────────────────────────────────────────────────┐
 * │  ADMIN ACCOUNT MANAGEMENT                                                │
 * ├──────────────┬─────────────────────────────────┬────────────────────────┤
 * │  POST        │ /admin-panel/register            │ Register new admin     │
 * │  POST        │ /admin-panel/login               │ Admin login            │
 * │  PUT         │ /admin-panel/approve/{targetId}  │ Super approves admin   │
 * │  PUT         │ /admin-panel/revoke/{targetId}   │ Super revokes admin    │
 * │  GET         │ /admin-panel/pending             │ List pending admins    │
 * │  GET         │ /admin-panel/all-admins          │ List all admins        │
 * ├──────────────┼─────────────────────────────────┼────────────────────────┤
 * │  DASHBOARD                                                               │
 * ├──────────────┼─────────────────────────────────┼────────────────────────┤
 * │  GET         │ /admin-panel/dashboard           │ Stats summary          │
 * ├──────────────┼─────────────────────────────────┼────────────────────────┤
 * │  USER MANAGEMENT                                                         │
 * ├──────────────┼─────────────────────────────────┼────────────────────────┤
 * │  GET         │ /admin-panel/users               │ All users              │
 * │  GET         │ /admin-panel/users/stats         │ User count stats       │
 * │  PUT         │ /admin-panel/users/{id}/premium  │ Approve premium        │
 * │  DELETE      │ /admin-panel/users/{id}          │ Delete user            │
 * ├──────────────┼─────────────────────────────────┼────────────────────────┤
 * │  BOOK MANAGEMENT                                                         │
 * ├──────────────┼─────────────────────────────────┼────────────────────────┤
 * │  GET         │ /admin-panel/books               │ All books              │
 * │  POST        │ /admin-panel/books               │ Add book               │
 * │  PUT         │ /admin-panel/books/{id}          │ Update book            │
 * │  DELETE      │ /admin-panel/books/{id}          │ Delete book            │
 * └──────────────┴─────────────────────────────────┴────────────────────────┘
 *
 * All protected endpoints require ?adminId=X (the logged-in admin's ID).
 * SUPER_ADMIN-only endpoints additionally verify the role server-side.
 */
@RestController
@RequestMapping("/admin-panel")
@CrossOrigin
public class AdminPanelController {

    @Autowired
    private AdminPanelService adminPanelService;

    // ─────────────────────────────────────────────────────────────────────────
    // ADMIN ACCOUNT MANAGEMENT
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Register a new admin account.
     * The account starts as PENDING and must be approved by a SUPER_ADMIN.
     *
     * POST /admin-panel/register
     * Body: { "adminName": "...", "email": "...", "password": "..." }
     */
    @PostMapping("/register")
    public ResponseEntity<Object> registerAdmin(@RequestBody Admin admin) {
        return adminPanelService.registerAdmin(admin);
    }

    /**
     * Admin login.
     * Only approved admins can log in.
     *
     * POST /admin-panel/login?adminName=xxx&password=yyy
     */
    @PostMapping("/login")
    public ResponseEntity<Object> loginAdmin(
            @RequestParam String adminName,
            @RequestParam String password) {
        return adminPanelService.loginAdmin(adminName, password);
    }

    /**
     * SUPER_ADMIN: Approve a PENDING admin.
     * Promotes the target admin from PENDING → ADMIN.
     *
     * PUT /admin-panel/approve/{targetId}?approverId=X
     */
    @PutMapping("/approve/{targetId}")
    public ResponseEntity<Object> approveAdmin(
            @PathVariable int targetId,
            @RequestParam int approverId) {
        return adminPanelService.approveAdmin(approverId, targetId);
    }

    /**
     * SUPER_ADMIN: Revoke an admin's access.
     * Demotes the target admin from ADMIN → PENDING.
     *
     * PUT /admin-panel/revoke/{targetId}?approverId=X
     */
    @PutMapping("/revoke/{targetId}")
    public ResponseEntity<Object> revokeAdmin(
            @PathVariable int targetId,
            @RequestParam int approverId) {
        return adminPanelService.revokeAdmin(approverId, targetId);
    }

    /**
     * SUPER_ADMIN: List all admins awaiting approval.
     *
     * GET /admin-panel/pending?approverId=X
     */
    @GetMapping("/pending")
    public ResponseEntity<Object> getPendingAdmins(@RequestParam int approverId) {
        return adminPanelService.getPendingAdmins(approverId);
    }

    /**
     * SUPER_ADMIN: List every admin account in the system.
     *
     * GET /admin-panel/all-admins?approverId=X
     */
    @GetMapping("/all-admins")
    public ResponseEntity<Object> getAllAdmins(@RequestParam int approverId) {
        return adminPanelService.getAllAdmins(approverId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DASHBOARD
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Dashboard statistics: users, books, admins summary.
     *
     * GET /admin-panel/dashboard?adminId=X
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Object> getDashboard(@RequestParam int adminId) {
        return adminPanelService.getDashboardStats(adminId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // USER MANAGEMENT
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * List all registered users.
     *
     * GET /admin-panel/users?adminId=X
     */
    @GetMapping("/users")
    public ResponseEntity<Object> getAllUsers(@RequestParam int adminId) {
        return adminPanelService.getAllUsers(adminId);
    }

    /**
     * Get user count statistics (total / free / premium).
     *
     * GET /admin-panel/users/stats?adminId=X
     */
    @GetMapping("/users/stats")
    public ResponseEntity<Object> getUserStats(@RequestParam int adminId) {
        return adminPanelService.getUserStats(adminId);
    }

    /**
     * Manually approve premium access for a user.
     *
     * PUT /admin-panel/users/{userId}/premium?adminId=X&plan=MONTHLY
     */
    @PutMapping("/users/{userId}/premium")
    public ResponseEntity<Object> approvePremium(
            @PathVariable int userId,
            @RequestParam int adminId,
            @RequestParam User.PremiumPlan plan) {
        return adminPanelService.approvePremiumForUser(adminId, userId, plan);
    }

    /**
     * Revoke premium from a user — downgrade back to FREE_USER.
     *
     * DELETE /admin-panel/users/{userId}/premium?adminId=X
     */
    @DeleteMapping("/users/{userId}/premium")
    public ResponseEntity<Object> revokePremium(
            @PathVariable int userId,
            @RequestParam int adminId) {
        return adminPanelService.revokePremiumFromUser(adminId, userId);
    }

    /**
     * Reset a user's password (SUPER_ADMIN only).
     *
     * PUT /admin-panel/users/{userId}/reset-password?adminId=X&newPassword=...
     */
    @PutMapping("/users/{userId}/reset-password")
    public ResponseEntity<Object> resetPassword(
            @PathVariable int userId,
            @RequestParam int adminId,
            @RequestParam String newPassword) {
        return adminPanelService.resetUserPassword(adminId, userId, newPassword);
    }

    /**
     * Delete a user account.
     *
     * DELETE /admin-panel/users/{userId}?adminId=X
     */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Object> deleteUser(
            @PathVariable int userId,
            @RequestParam int adminId) {
        return adminPanelService.deleteUser(adminId, userId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // BOOK MANAGEMENT
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * List all audiobooks in the library.
     *
     * GET /admin-panel/books?adminId=X
     */
    @GetMapping("/books")
    public ResponseEntity<Object> getAllBooks(@RequestParam int adminId) {
        return adminPanelService.getAllBooks(adminId);
    }

    /**
     * Add a new audiobook to the library.
     *
     * POST /admin-panel/books?adminId=X
     * Body: { "bookName": "...", "bookAuthor": "...", "bookCategory": "Free|Premium",
     *         "audioFileLink": "...", "imageLink": "...", "bookType": "..." }
     */
    @PostMapping("/books")
    public ResponseEntity<Object> addBook(
            @RequestParam int adminId,
            @RequestBody Book book) {
        return adminPanelService.addBook(adminId, book);
    }

    /**
     * Update an existing audiobook.
     * Only non-null / non-blank fields in the body are applied.
     *
     * PUT /admin-panel/books/{bookId}?adminId=X
     * Body: (partial or full Book JSON)
     */
    @PutMapping("/books/{bookId}")
    public ResponseEntity<Object> updateBook(
            @PathVariable int bookId,
            @RequestParam int adminId,
            @RequestBody Book book) {
        return adminPanelService.updateBook(adminId, bookId, book);
    }

    /**
     * Remove an audiobook from the library.
     *
     * DELETE /admin-panel/books/{bookId}?adminId=X
     */
    @DeleteMapping("/books/{bookId}")
    public ResponseEntity<Object> deleteBook(
            @PathVariable int bookId,
            @RequestParam int adminId) {
        return adminPanelService.deleteBook(adminId, bookId);
    }

    // ──────────────────────────────────────────────────────────────────
    // ADMIN ROLE MANAGEMENT (SUPER_ADMIN only)
    // ──────────────────────────────────────────────────────────────────

    /**
     * Promote an ADMIN to SUPER_ADMIN (SUPER_ADMIN only).
     *
     * PUT /admin-panel/promote/{targetId}?callerId=X
     */
    @PutMapping("/promote/{targetId}")
    public ResponseEntity<Object> promoteAdmin(
            @PathVariable int targetId,
            @RequestParam int callerId) {
        return adminPanelService.promoteToSuperAdmin(callerId, targetId);
    }

    /**
     * Demote a SUPER_ADMIN back to ADMIN (SUPER_ADMIN only, cannot demote self).
     *
     * PUT /admin-panel/demote/{targetId}?callerId=X
     */
    @PutMapping("/demote/{targetId}")
    public ResponseEntity<Object> demoteAdmin(
            @PathVariable int targetId,
            @RequestParam int callerId) {
        return adminPanelService.demoteToAdmin(callerId, targetId);
    }

    // ──────────────────────────────────────────────────────────────────
    // LOGIN HISTORY & USER ACTIVITY
    // ──────────────────────────────────────────────────────────────────

    /**
     * View all login history across all users (approved admin).
     *
     * GET /admin-panel/login-history?adminId=X
     */
    @GetMapping("/login-history")
    public ResponseEntity<Object> getAllLoginHistory(@RequestParam int adminId) {
        return adminPanelService.getAllLoginHistory(adminId);
    }

    /**
     * View login history for a specific user (approved admin).
     *
     * GET /admin-panel/login-history/{userId}?adminId=X
     */
    @GetMapping("/login-history/{userId}")
    public ResponseEntity<Object> getLoginHistoryForUser(
            @PathVariable int userId,
            @RequestParam int adminId) {
        return adminPanelService.getLoginHistoryForUser(adminId, userId);
    }

    /**
     * View all user listening activity (approved admin).
     *
     * GET /admin-panel/activity?adminId=X
     */
    @GetMapping("/activity")
    public ResponseEntity<Object> getAllActivity(@RequestParam int adminId) {
        return adminPanelService.getAllUserActivity(adminId);
    }

    // ──────────────────────────────────────────────────────────────────
    // SYSTEM SETTINGS (SUPER_ADMIN only)
    // ──────────────────────────────────────────────────────────────────

    /**
     * View all system settings (SUPER_ADMIN only).
     *
     * GET /admin-panel/settings?adminId=X
     */
    @GetMapping("/settings")
    public ResponseEntity<Object> getSettings(@RequestParam int adminId) {
        return adminPanelService.getSettings(adminId);
    }

    /**
     * Update system settings (SUPER_ADMIN only).
     * Body: { "settingKey": "newValue", ... }
     *
     * PUT /admin-panel/settings?adminId=X
     */
    @PutMapping("/settings")
    public ResponseEntity<Object> updateSettings(
            @RequestParam int adminId,
            @RequestBody java.util.Map<String, String> updates) {
        return adminPanelService.updateSettings(adminId, updates);
    }
}
