package com.InnerVoice.InnerVoiceProject.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Admin entity — represents a platform administrator.
 *
 * Roles:
 *   SUPER_ADMIN  — Full access; can approve/revoke other admins.
 *   ADMIN        — Approved admin; can manage users and books.
 *   PENDING      — Newly registered admin awaiting SUPER_ADMIN approval.
 *
 * An admin with isApproved = false (PENDING role) cannot perform any
 * management operations until a SUPER_ADMIN explicitly approves them.
 */
@Entity
@Data
@Table(name = "admins")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int adminId;

    @Column(unique = true, nullable = false)
    private String adminName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /**
     * true  — admin can log in and perform operations.
     * false — admin is pending; access denied until approved.
     */
    private boolean isApproved;

    private LocalDateTime createdAt;

    /** Called before persist to set default timestamps and pending state. */
    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public enum Role {
        /** Highest privilege: can approve/revoke admins and access all features. */
        SUPER_ADMIN,
        /** Approved admin: can manage users and books. */
        ADMIN,
        /** Newly registered; awaiting SUPER_ADMIN approval. */
        PENDING
    }
}
