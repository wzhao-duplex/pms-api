package ca.on.pms.tenant.dto;

import java.time.Instant;
import java.util.UUID;

import ca.on.pms.tenant.entity.TenantDocumentEntity;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TenantDocumentResponse {

	UUID documentId;
	UUID tenantId;

	String originalFileName;
	String contentType;
	long fileSize;

	Instant uploadedAt;

	public static TenantDocumentResponse from(TenantDocumentEntity entity) {
		return TenantDocumentResponse.builder().documentId(entity.getDocumentId())
				.tenantId(entity.getTenant().getTenantId()).originalFileName(entity.getOriginalFileName())
				.contentType(entity.getContentType()).fileSize(entity.getFileSize()).uploadedAt(entity.getUploadedAt())
				.build();
	}

}
