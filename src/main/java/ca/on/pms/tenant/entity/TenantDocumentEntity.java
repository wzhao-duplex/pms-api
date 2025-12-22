package ca.on.pms.tenant.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tenant_documents")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantDocumentEntity {

	@Id
	@GeneratedValue
	private UUID documentId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tenant_id", nullable = false)
	private TenantEntity tenant;

	@Column(nullable = false)
	private String documentType; // ✅ ADD THIS

	@Column(nullable = false)
	private String s3Key;

	@Column(nullable = false, length = 512)
	private String encryptedKey;

	@Column(nullable = false)
	private String originalFileName;

	@Column(nullable = false)
	private String contentType;

	@Column(nullable = false)
	private long fileSize;

	private Instant uploadedAt;
}
