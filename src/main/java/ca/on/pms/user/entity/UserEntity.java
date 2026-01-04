package ca.on.pms.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(name = "uk_users_email", columnNames = "email") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

	@Id
	@GeneratedValue
	@Column(name = "user_id", updatable = false, nullable = false)
	private UUID userId;

	@Column(name = "email", nullable = false, length = 255)
	private String email;

	@Column(name = "password_hash", nullable = false)
	private String passwordHash; // New Field

	@Column(name = "full_name", nullable = false, length = 255)
	private String fullName;

	@Column(name = "role", nullable = false, length = 50)
	private String role;

	@Column(name = "status", length = 50)
	private String status;

	@Column(name = "org_id")
	private UUID orgId; // New Field: The tenant this user belongs to

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "verification_code")
	private String verificationCode;

	@Column(name = "verification_expiry")
	private LocalDateTime verificationExpiry;

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
		if (this.status == null) {
			this.status = "ACTIVE";
		}
	}
}