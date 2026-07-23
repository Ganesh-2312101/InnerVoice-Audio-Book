package com.InnerVoice.InnerVoiceProject;

import com.InnerVoice.InnerVoiceProject.Model.Admin;
import com.InnerVoice.InnerVoiceProject.Repositories.AdminRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

@SpringBootApplication
public class InnerVoiceProjectApplication {

	private static final Logger log = Logger.getLogger(InnerVoiceProjectApplication.class.getName());

	public static void main(String[] args) {
		String dbUrl = System.getenv("DATABASE_URL");
		if (dbUrl != null && (dbUrl.startsWith("postgres://") || dbUrl.startsWith("postgresql://"))) {
			String jdbcUrl = dbUrl.replaceFirst("^postgres(ql)?://", "jdbc:postgresql://");
			System.setProperty("spring.datasource.url", jdbcUrl);
			try {
				java.net.URI uri = new java.net.URI(dbUrl);
				String userInfo = uri.getUserInfo();
				if (userInfo != null && userInfo.contains(":")) {
					String[] parts = userInfo.split(":");
					String username = java.net.URLDecoder.decode(parts[0], "UTF-8");
					String password = java.net.URLDecoder.decode(parts[1], "UTF-8");
					System.setProperty("spring.datasource.username", username);
					System.setProperty("spring.datasource.password", password);
				}
			} catch (Exception e) {
				System.err.println("Failed to parse DATABASE_URL: " + e.getMessage());
			}
		}
		SpringApplication.run(InnerVoiceProjectApplication.class, args);
	}

	/**
	 * Seeds a default SUPER_ADMIN account on first startup.
	 *
	 * Credentials:
	 *   Username : superadmin
	 *   Password : Admin@123
	 *   Role     : SUPER_ADMIN
	 *
	 * This runs every startup but only creates the account if no
	 * SUPER_ADMIN already exists in the database, so it is safe
	 * to leave in place in production.
	 */
	@Bean
	public CommandLineRunner seedSuperAdmin(AdminRepository adminRepository) {
		return args -> {
			List<Admin> existingSuperAdmins = adminRepository.findByRole(Admin.Role.SUPER_ADMIN);
			if (existingSuperAdmins.isEmpty()) {
				Admin superAdmin = new Admin();
				superAdmin.setAdminName("superadmin");
				superAdmin.setEmail("superadmin@innervoice.com");
				superAdmin.setPassword("Admin@123");
				superAdmin.setRole(Admin.Role.SUPER_ADMIN);
				superAdmin.setApproved(true);
				superAdmin.setCreatedAt(LocalDateTime.now());

				adminRepository.save(superAdmin);
				log.info("========================================================");
				log.info("  DEFAULT SUPER_ADMIN CREATED");
				log.info("  Username : superadmin");
				log.info("  Password : Admin@123");
				log.info("  Role     : SUPER_ADMIN");
				log.info("  PLEASE CHANGE THE PASSWORD AFTER FIRST LOGIN!");
				log.info("========================================================");
			} else {
				log.info("[AdminPanel] SUPER_ADMIN already exists — skipping seed.");
			}
		};
	}
}

