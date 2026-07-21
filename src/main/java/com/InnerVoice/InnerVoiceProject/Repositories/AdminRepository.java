package com.InnerVoice.InnerVoiceProject.Repositories;

import com.InnerVoice.InnerVoiceProject.Model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * AdminRepository — JPA repository for the Admin entity.
 *
 * Provides lookup methods used by AdminPanelService for login,
 * duplicate-check, and listing admins by approval status or role.
 */
public interface AdminRepository extends JpaRepository<Admin, Integer> {

    /** Find admin by username — used for login. */
    @Query("SELECT a FROM Admin a WHERE a.adminName = :adminName")
    Optional<Admin> findByAdminName(@Param("adminName") String adminName);

    /** Find admin by email — used for registration conflict check. */
    @Query("SELECT a FROM Admin a WHERE a.email = :email")
    Optional<Admin> findByEmail(@Param("email") String email);

    /** List all admins with the given approval status. */
    @Query("SELECT a FROM Admin a WHERE a.isApproved = :approved")
    List<Admin> findByIsApproved(@Param("approved") boolean approved);

    /** List all admins with the given role. */
    @Query("SELECT a FROM Admin a WHERE a.role = :role")
    List<Admin> findByRole(@Param("role") Admin.Role role);
}
