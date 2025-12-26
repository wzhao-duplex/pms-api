package ca.on.pms.tenant.service;

import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;
import ca.on.pms.tenant.dto.DownloadedFile;
import ca.on.pms.tenant.dto.TenantDocumentResponse;
import ca.on.pms.tenant.dto.TenantDto;

public interface TenantService {
	TenantDto createTenant(TenantDto dto);

	TenantDto updateTenant(UUID tenantId, TenantDto dto);

	void deleteTenant(UUID tenantId);

	TenantDto getTenantById(UUID tenantId);

	List<TenantDto> listByProperty(UUID propertyId);

	// ✅ NEW: List all tenants for the current user's organization
	List<TenantDto> listAll();

	TenantDocumentResponse uploadDocument(UUID tenantId, MultipartFile file, String documentType);

	DownloadedFile downloadTenantDocument(UUID documentId);
}