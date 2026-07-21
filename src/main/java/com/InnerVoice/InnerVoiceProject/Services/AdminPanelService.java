package com.InnerVoice.InnerVoiceProject.Services;

import com.InnerVoice.InnerVoiceProject.Model.Admin;
import com.InnerVoice.InnerVoiceProject.Model.Book;
import com.InnerVoice.InnerVoiceProject.Model.LoginHistory;
import com.InnerVoice.InnerVoiceProject.Model.User;
import com.InnerVoice.InnerVoiceProject.Model.UserActivity;
import com.InnerVoice.InnerVoiceProject.Repositories.AdminRepository;
import com.InnerVoice.InnerVoiceProject.Repositories.BookRepository;
import com.InnerVoice.InnerVoiceProject.Repositories.LoginHistoryRepository;
import com.InnerVoice.InnerVoiceProject.Repositories.UserActivityRepository;
import com.InnerVoice.InnerVoiceProject.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * AdminPanelService — Business logic for the Admin Panel.
 *
 * Provides:
 *  - Admin registration (starts as PENDING)
 *  - Admin login (only approved admins can log in)
 *  - SUPER_ADMIN approval / revocation of other admins
 *  - Dashboard statistics (users, books, premium counts)
 *  - Full user management (view all, approve premium, delete)
 *  - Full book management (view all, add, update, delete)
 *
 * Security model (simple, no Spring Security):
 *   - Every protected operation receives an adminId and validates:
 *       1. Admin exists in DB
 *       2. Admin is approved (isApproved = true)
 *       3. For SUPER_ADMIN-only operations, role == SUPER_ADMIN
 */
@Service
public class AdminPanelService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private LoginHistoryRepository loginHistoryRepository;

    @Autowired
    private UserActivityRepository userActivityRepository;

    // ── In-memory settings store (no extra table needed) ──────────────────────
    private final Map<String, String> systemSettings = new LinkedHashMap<>();

    // Seed defaults
    {
        systemSettings.put("appName",             "InnerVoice");
        systemSettings.put("premiumMonthlyPrice", "99");
        systemSettings.put("premiumYearlyPrice",  "999");
        systemSettings.put("maxBooksPerPage",     "20");
        systemSettings.put("allowNewRegistration","true");
        systemSettings.put("maintenanceMode",     "false");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ADMIN REGISTRATION & LOGIN
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Register a new admin account.
     * New admins always start as PENDING (isApproved = false, role = PENDING).
     * A SUPER_ADMIN must call approveAdmin() before the new admin can operate.
     */
    public ResponseEntity<Object> registerAdmin(Admin admin) {
        // Duplicate username check
        if (adminRepository.findByAdminName(admin.getAdminName()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Admin username already exists.");
        }
        // Duplicate email check
        if (adminRepository.findByEmail(admin.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("An admin with this email already exists.");
        }

        // Force PENDING state regardless of what was sent
        admin.setRole(Admin.Role.PENDING);
        admin.setApproved(false);
        admin.setCreatedAt(LocalDateTime.now());

        Admin saved = adminRepository.save(admin);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(buildAdminResponse(saved,
                        "Admin registered successfully. Awaiting SUPER_ADMIN approval."));
    }

    /**
     * Admin login.
     * Only admins with isApproved = true are allowed to log in.
     */
    public ResponseEntity<Object> loginAdmin(String adminName, String password) {
        Optional<Admin> opt = adminRepository.findByAdminName(adminName);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Admin not found with username: " + adminName);
        }

        Admin admin = opt.get();

        if (!password.equals(admin.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Incorrect password.");
        }

        if (!admin.isApproved()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Your admin account is pending approval. Please contact a SUPER_ADMIN.");
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Login successful.");
        response.put("adminId", admin.getAdminId());
        response.put("adminName", admin.getAdminName());
        response.put("email", admin.getEmail());
        response.put("role", admin.getRole());
        response.put("isApproved", admin.isApproved());
        return ResponseEntity.ok(response);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SUPER_ADMIN — APPROVAL / REVOCATION
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Approve a PENDING admin.
     * Only a SUPER_ADMIN can perform this action.
     * On approval, the admin's role is promoted from PENDING to ADMIN.
     */
    public ResponseEntity<Object> approveAdmin(int approverId, int targetAdminId) {
        ResponseEntity<Object> authCheck = requireSuperAdmin(approverId);
        if (authCheck != null) return authCheck;

        Optional<Admin> targetOpt = adminRepository.findById(targetAdminId);
        if (targetOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Target admin not found with ID: " + targetAdminId);
        }

        Admin target = targetOpt.get();
        if (target.isApproved()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Admin is already approved.");
        }

        target.setApproved(true);
        target.setRole(Admin.Role.ADMIN);
        adminRepository.save(target);

        return ResponseEntity.ok(buildAdminResponse(target,
                "Admin '" + target.getAdminName() + "' has been approved successfully."));
    }

    /**
     * Revoke (de-approve) an admin.
     * Only a SUPER_ADMIN can perform this action.
     * The admin's role is reverted to PENDING and isApproved is set to false.
     */
    public ResponseEntity<Object> revokeAdmin(int approverId, int targetAdminId) {
        ResponseEntity<Object> authCheck = requireSuperAdmin(approverId);
        if (authCheck != null) return authCheck;

        // Prevent self-revocation
        if (approverId == targetAdminId) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("SUPER_ADMIN cannot revoke their own access.");
        }

        Optional<Admin> targetOpt = adminRepository.findById(targetAdminId);
        if (targetOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Target admin not found with ID: " + targetAdminId);
        }

        Admin target = targetOpt.get();
        if (target.getRole() == Admin.Role.SUPER_ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Cannot revoke another SUPER_ADMIN.");
        }

        target.setApproved(false);
        target.setRole(Admin.Role.PENDING);
        adminRepository.save(target);

        return ResponseEntity.ok(buildAdminResponse(target,
                "Admin '" + target.getAdminName() + "' has been revoked."));
    }

    /**
     * List all admins with PENDING (unapproved) status.
     * Requires SUPER_ADMIN.
     */
    public ResponseEntity<Object> getPendingAdmins(int approverId) {
        ResponseEntity<Object> authCheck = requireSuperAdmin(approverId);
        if (authCheck != null) return authCheck;

        List<Admin> pending = adminRepository.findByIsApproved(false);
        return ResponseEntity.ok(pending);
    }

    /**
     * List all admins regardless of status.
     * Requires SUPER_ADMIN.
     */
    public ResponseEntity<Object> getAllAdmins(int approverId) {
        ResponseEntity<Object> authCheck = requireSuperAdmin(approverId);
        if (authCheck != null) return authCheck;

        List<Admin> admins = adminRepository.findAll();
        return ResponseEntity.ok(admins);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DASHBOARD STATISTICS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns a consolidated dashboard summary:
     *   - Total users, Free users, Premium users
     *   - Total books, Free books, Premium books
     *   - Total admins, Pending admins
     * Requires an approved admin.
     */
    public ResponseEntity<Object> getDashboardStats(int adminId) {
        ResponseEntity<Object> authCheck = requireApprovedAdmin(adminId);
        if (authCheck != null) return authCheck;

        List<User> users = userRepository.findAll();
        long totalUsers   = users.size();
        long premiumUsers = users.stream()
                .filter(u -> u.getUserCategory() == User.Category.PREMIUM_USER).count();
        long freeUsers    = totalUsers - premiumUsers;

        List<Book> books = bookRepository.findAll();
        long totalBooks   = books.size();
        long premiumBooks = books.stream()
                .filter(b -> b.getBookCategory() == Book.Category.Premium).count();
        long freeBooks    = totalBooks - premiumBooks;

        List<Admin> allAdmins  = adminRepository.findAll();
        long totalAdmins   = allAdmins.size();
        long pendingAdmins = allAdmins.stream().filter(a -> !a.isApproved()).count();
        long activeAdmins  = totalAdmins - pendingAdmins;

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("users", Map.of(
                "total",   totalUsers,
                "premium", premiumUsers,
                "free",    freeUsers
        ));
        stats.put("books", Map.of(
                "total",   totalBooks,
                "premium", premiumBooks,
                "free",    freeBooks
        ));
        stats.put("admins", Map.of(
                "total",   totalAdmins,
                "active",  activeAdmins,
                "pending", pendingAdmins
        ));
        stats.put("generatedAt", LocalDateTime.now().toString());

        return ResponseEntity.ok(stats);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // USER MANAGEMENT
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Return all registered users.
     * Requires an approved admin.
     */
    public ResponseEntity<Object> getAllUsers(int adminId) {
        ResponseEntity<Object> authCheck = requireApprovedAdmin(adminId);
        if (authCheck != null) return authCheck;

        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    /**
     * Return user count breakdown (total, free, premium).
     * Requires an approved admin.
     */
    public ResponseEntity<Object> getUserStats(int adminId) {
        ResponseEntity<Object> authCheck = requireApprovedAdmin(adminId);
        if (authCheck != null) return authCheck;

        List<User> users = userRepository.findAll();
        long totalUsers   = users.size();
        long premiumUsers = users.stream()
                .filter(u -> u.getUserCategory() == User.Category.PREMIUM_USER).count();
        long freeUsers    = totalUsers - premiumUsers;

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalUsers",   totalUsers);
        stats.put("premiumUsers", premiumUsers);
        stats.put("freeUsers",    freeUsers);
        return ResponseEntity.ok(stats);
    }

    /**
     * Manually upgrade a user to PREMIUM_USER.
     * Admin can choose MONTHLY or YEARLY plan.
     * Requires an approved admin.
     */
    public ResponseEntity<Object> approvePremiumForUser(int adminId, int userId, User.PremiumPlan plan) {
        ResponseEntity<Object> authCheck = requireApprovedAdmin(adminId);
        if (authCheck != null) return authCheck;

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found with ID: " + userId);
        }

        User user = userOpt.get();
        user.setUserCategory(User.Category.PREMIUM_USER);
        user.setPremiumPlan(plan);
        user.setPremiumStartDate(LocalDate.now());
        userRepository.save(user);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "User '" + user.getUserName() + "' has been upgraded to PREMIUM.");
        response.put("userId",        user.getUserId());
        response.put("userName",      user.getUserName());
        response.put("plan",          plan);
        response.put("premiumStart",  user.getPremiumStartDate());
        return ResponseEntity.ok(response);
    }

    /**
     * Revoke premium from a user — downgrade back to FREE_USER.
     * Requires an approved admin.
     */
    public ResponseEntity<Object> revokePremiumFromUser(int adminId, int userId) {
        ResponseEntity<Object> authCheck = requireApprovedAdmin(adminId);
        if (authCheck != null) return authCheck;

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found with ID: " + userId);
        }

        User user = userOpt.get();
        if (user.getUserCategory() != User.Category.PREMIUM_USER) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("User '" + user.getUserName() + "' is already a FREE user.");
        }

        user.setUserCategory(User.Category.FREE_USER);
        user.setPremiumPlan(null);
        user.setPremiumStartDate(null);
        userRepository.save(user);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Premium revoked from '" + user.getUserName() + "'. Now FREE_USER.");
        response.put("userId",   user.getUserId());
        response.put("userName", user.getUserName());
        return ResponseEntity.ok(response);
    }

    /**
     * Reset a user's password.
     * Requires SUPER_ADMIN.
     */
    public ResponseEntity<Object> resetUserPassword(int adminId, int userId, String newPassword) {
        ResponseEntity<Object> authCheck = requireSuperAdmin(adminId);
        if (authCheck != null) return authCheck;

        if (newPassword == null || newPassword.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("New password cannot be empty.");
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found with ID: " + userId);
        }

        User user = userOpt.get();
        user.setPassword(newPassword);
        userRepository.save(user);
        return ResponseEntity.ok("Password reset successfully for user '" + user.getUserName() + "'.");
    }

    /**
     * Delete a user by ID.
     * Requires an approved admin.
     */
    public ResponseEntity<Object> deleteUser(int adminId, int userId) {
        ResponseEntity<Object> authCheck = requireApprovedAdmin(adminId);
        if (authCheck != null) return authCheck;

        if (!userRepository.existsById(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found with ID: " + userId);
        }
        userRepository.deleteById(userId);
        return ResponseEntity.ok("User with ID " + userId + " has been deleted.");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // BOOK MANAGEMENT
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Return all audiobooks in the library.
     * Requires an approved admin.
     */
    public ResponseEntity<Object> getAllBooks(int adminId) {
        ResponseEntity<Object> authCheck = requireApprovedAdmin(adminId);
        if (authCheck != null) return authCheck;

        return ResponseEntity.ok(bookRepository.findAll());
    }

    /**
     * Add a new audiobook to the library.
     * Requires an approved admin.
     */
    public ResponseEntity<Object> addBook(int adminId, Book book) {
        ResponseEntity<Object> authCheck = requireApprovedAdmin(adminId);
        if (authCheck != null) return authCheck;

        Book saved = bookRepository.save(book);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Book '" + saved.getBookName() + "' added successfully.");
        response.put("book", saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update an existing audiobook.
     * Requires an approved admin.
     */
    public ResponseEntity<Object> updateBook(int adminId, int bookId, Book updatedBook) {
        ResponseEntity<Object> authCheck = requireApprovedAdmin(adminId);
        if (authCheck != null) return authCheck;

        Optional<Book> bookOpt = bookRepository.findById(bookId);
        if (bookOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Book not found with ID: " + bookId);
        }

        Book existing = bookOpt.get();

        // Apply updates only for non-null / non-blank provided values
        if (updatedBook.getBookName() != null && !updatedBook.getBookName().isBlank()) {
            existing.setBookName(updatedBook.getBookName());
        }
        if (updatedBook.getBookAuthor() != null && !updatedBook.getBookAuthor().isBlank()) {
            existing.setBookAuthor(updatedBook.getBookAuthor());
        }
        if (updatedBook.getBookCategory() != null) {
            existing.setBookCategory(updatedBook.getBookCategory());
        }
        if (updatedBook.getAudioFileLink() != null && !updatedBook.getAudioFileLink().isBlank()) {
            existing.setAudioFileLink(updatedBook.getAudioFileLink());
        }
        if (updatedBook.getImageLink() != null && !updatedBook.getImageLink().isBlank()) {
            existing.setImageLink(updatedBook.getImageLink());
        }
        if (updatedBook.getBookType() != null && !updatedBook.getBookType().isBlank()) {
            existing.setBookType(updatedBook.getBookType());
        }

        Book saved = bookRepository.save(existing);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Book updated successfully.");
        response.put("book", saved);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete an audiobook from the library.
     * Requires an approved admin.
     */
    public ResponseEntity<Object> deleteBook(int adminId, int bookId) {
        ResponseEntity<Object> authCheck = requireApprovedAdmin(adminId);
        if (authCheck != null) return authCheck;

        if (!bookRepository.existsById(bookId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Book not found with ID: " + bookId);
        }
        bookRepository.deleteById(bookId);
        return ResponseEntity.ok("Book with ID " + bookId + " has been deleted.");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ADMIN ROLE MANAGEMENT
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Promote an ADMIN to SUPER_ADMIN.
     * Only an existing SUPER_ADMIN can do this.
     */
    public ResponseEntity<Object> promoteToSuperAdmin(int callerId, int targetId) {
        ResponseEntity<Object> authCheck = requireSuperAdmin(callerId);
        if (authCheck != null) return authCheck;

        Optional<Admin> targetOpt = adminRepository.findById(targetId);
        if (targetOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Target admin not found.");
        }

        Admin target = targetOpt.get();
        if (target.getRole() == Admin.Role.SUPER_ADMIN) {
            return ResponseEntity.badRequest().body("Admin is already a SUPER_ADMIN.");
        }
        if (!target.isApproved()) {
            return ResponseEntity.badRequest().body("Cannot promote a PENDING admin. Approve them first.");
        }

        target.setRole(Admin.Role.SUPER_ADMIN);
        adminRepository.save(target);
        return ResponseEntity.ok(buildAdminResponse(target,
                "Admin '" + target.getAdminName() + "' promoted to SUPER_ADMIN."));
    }

    /**
     * Demote a SUPER_ADMIN back to ADMIN.
     * Only an existing SUPER_ADMIN can do this (cannot demote themselves).
     */
    public ResponseEntity<Object> demoteToAdmin(int callerId, int targetId) {
        ResponseEntity<Object> authCheck = requireSuperAdmin(callerId);
        if (authCheck != null) return authCheck;

        if (callerId == targetId) {
            return ResponseEntity.badRequest().body("SUPER_ADMIN cannot demote themselves.");
        }

        Optional<Admin> targetOpt = adminRepository.findById(targetId);
        if (targetOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Target admin not found.");
        }

        Admin target = targetOpt.get();
        if (target.getRole() != Admin.Role.SUPER_ADMIN) {
            return ResponseEntity.badRequest().body("Admin is not a SUPER_ADMIN.");
        }

        target.setRole(Admin.Role.ADMIN);
        adminRepository.save(target);
        return ResponseEntity.ok(buildAdminResponse(target,
                "Admin '" + target.getAdminName() + "' demoted to ADMIN."));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LOGIN HISTORY & USER ACTIVITY
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * View all login history across all users.
     * Requires an approved admin.
     */
    public ResponseEntity<Object> getAllLoginHistory(int adminId) {
        ResponseEntity<Object> authCheck = requireApprovedAdmin(adminId);
        if (authCheck != null) return authCheck;

        List<LoginHistory> history = loginHistoryRepository.findAllByOrderByLoginTimeDesc();
        return ResponseEntity.ok(history);
    }

    /**
     * View login history for a specific user.
     * Requires an approved admin.
     */
    public ResponseEntity<Object> getLoginHistoryForUser(int adminId, int userId) {
        ResponseEntity<Object> authCheck = requireApprovedAdmin(adminId);
        if (authCheck != null) return authCheck;

        if (!userRepository.existsById(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with ID: " + userId);
        }
        List<LoginHistory> history = loginHistoryRepository.findByUserIdOrderByLoginTimeDesc(userId);
        return ResponseEntity.ok(history);
    }

    /**
     * View all user activity (what books were listened to).
     * Requires an approved admin.
     */
    public ResponseEntity<Object> getAllUserActivity(int adminId) {
        ResponseEntity<Object> authCheck = requireApprovedAdmin(adminId);
        if (authCheck != null) return authCheck;

        List<UserActivity> activities = userActivityRepository.findAll();
        return ResponseEntity.ok(activities);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SYSTEM SETTINGS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Get all system settings.
     * Requires SUPER_ADMIN.
     */
    public ResponseEntity<Object> getSettings(int adminId) {
        ResponseEntity<Object> authCheck = requireSuperAdmin(adminId);
        if (authCheck != null) return authCheck;

        return ResponseEntity.ok(Collections.unmodifiableMap(systemSettings));
    }

    /**
     * Update one or more system settings.
     * Requires SUPER_ADMIN.
     * Only known keys are accepted to prevent arbitrary injection.
     */
    public ResponseEntity<Object> updateSettings(int adminId, Map<String, String> updates) {
        ResponseEntity<Object> authCheck = requireSuperAdmin(adminId);
        if (authCheck != null) return authCheck;

        List<String> rejected = new ArrayList<>();
        for (Map.Entry<String, String> entry : updates.entrySet()) {
            if (systemSettings.containsKey(entry.getKey())) {
                systemSettings.put(entry.getKey(), entry.getValue());
            } else {
                rejected.add(entry.getKey());
            }
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message",         "Settings updated.");
        response.put("currentSettings", Collections.unmodifiableMap(systemSettings));
        if (!rejected.isEmpty()) {
            response.put("rejectedKeys",
                    "Unknown setting keys (ignored): " + rejected);
        }
        return ResponseEntity.ok(response);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PRIVATE HELPERS — Authentication Guards
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Validates that the given admin ID belongs to an approved admin.
     * Returns a ResponseEntity error if validation fails, null if OK.
     */
    private ResponseEntity<Object> requireApprovedAdmin(int adminId) {
        Optional<Admin> adminOpt = adminRepository.findById(adminId);
        if (adminOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized: Admin not found with ID " + adminId);
        }
        Admin admin = adminOpt.get();
        if (!admin.isApproved()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: Admin account is not yet approved.");
        }
        return null; // Validation passed
    }

    /**
     * Validates that the given admin ID belongs to an approved SUPER_ADMIN.
     * Returns a ResponseEntity error if validation fails, null if OK.
     */
    private ResponseEntity<Object> requireSuperAdmin(int adminId) {
        Optional<Admin> adminOpt = adminRepository.findById(adminId);
        if (adminOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized: Admin not found with ID " + adminId);
        }
        Admin admin = adminOpt.get();
        if (!admin.isApproved()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: Admin account is not yet approved.");
        }
        if (admin.getRole() != Admin.Role.SUPER_ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: Only SUPER_ADMIN can perform this operation.");
        }
        return null; // Validation passed
    }

    /**
     * Build a safe admin response map (excludes password).
     */
    private Map<String, Object> buildAdminResponse(Admin admin, String message) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("message",    message);
        map.put("adminId",    admin.getAdminId());
        map.put("adminName",  admin.getAdminName());
        map.put("email",      admin.getEmail());
        map.put("role",       admin.getRole());
        map.put("isApproved", admin.isApproved());
        map.put("createdAt",  admin.getCreatedAt());
        return map;
    }
}
