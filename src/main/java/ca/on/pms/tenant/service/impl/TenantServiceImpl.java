package ca.on.pms.tenant.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.security.access.AccessDeniedException; // ✅ Standard Spring Security Exception
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import ca.on.pms.exception.ResourceNotFoundException;
import ca.on.pms.property.entity.PropertyEntity;
import ca.on.pms.property.repository.PropertyRepository;
import ca.on.pms.security.UserPrincipal;
import ca.on.pms.security.crypto.AesEncryptionUtil;
import ca.on.pms.storage.S3Service;
import ca.on.pms.tenant.dto.DownloadedFile;
import ca.on.pms.tenant.dto.TenantDocumentResponse;
import ca.on.pms.tenant.dto.TenantDto;
import ca.on.pms.tenant.entity.TenantDocumentEntity;
import ca.on.pms.tenant.entity.TenantEntity;
import ca.on.pms.tenant.repository.TenantDocumentRepository;
import ca.on.pms.tenant.repository.TenantRepository;
import ca.on.pms.tenant.service.TenantService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TenantServiceImpl implements TenantService {

	private final TenantRepository tenantRepository;
	private final TenantDocumentRepository tenantDocumentRepository;
	private final PropertyRepository propertyRepository;
	private final S3Service s3Service;

	private UserPrincipal getCurrentUser() {
		return (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	// =================================================================
	// CREATE
	// =================================================================
	@Override
	public TenantDto createTenant(TenantDto dto) {
		// 1. Fetch Property
		PropertyEntity property = propertyRepository.findById(dto.getPropertyId())
				.orElseThrow(() -> new ResourceNotFoundException("Property not found"));

		// 2. ✅ SECURITY CHECK: Ensure Property belongs to User's Org
		validatePropertyAccess(property);

		// 3. Save
		TenantEntity tenant = TenantEntity.builder().property(property).fullName(dto.getFullName())
				.phone(dto.getPhone()).email(dto.getEmail()).leaseStart(dto.getLeaseStart()).leaseEnd(dto.getLeaseEnd())
				.monthlyRent(dto.getMonthlyRent()).createdAt(LocalDateTime.now()).build();

		return toDto(tenantRepository.save(tenant));
	}

	// =================================================================
	// UPDATE
	// =================================================================
	@Override
	public TenantDto updateTenant(UUID tenantId, TenantDto dto) {
		TenantEntity tenant = tenantRepository.findById(tenantId)
				.orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

		// ✅ SECURITY CHECK
		validateTenantAccess(tenant);

		tenant.setFullName(dto.getFullName());
		tenant.setPhone(dto.getPhone());
		tenant.setEmail(dto.getEmail());
		tenant.setLeaseStart(dto.getLeaseStart());
		tenant.setLeaseEnd(dto.getLeaseEnd());
		tenant.setMonthlyRent(dto.getMonthlyRent());

		return toDto(tenantRepository.save(tenant));
	}

	// =================================================================
	// DELETE
	// =================================================================
	@Override
	public void deleteTenant(UUID tenantId) {
		TenantEntity tenant = tenantRepository.findById(tenantId)
				.orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

		// ✅ SECURITY CHECK
		validateTenantAccess(tenant);

		tenantRepository.delete(tenant);
	}

	// =================================================================
	// GET ONE
	// =================================================================
	@Override
	@Transactional(readOnly = true)
	public TenantDto getTenantById(UUID tenantId) {
		TenantEntity tenant = tenantRepository.findById(tenantId)
				.orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

		// ✅ SECURITY CHECK
		validateTenantAccess(tenant);

		return toDto(tenant);
	}

	// =================================================================
	// LIST ALL (By Org)
	// =================================================================
	@Override
	@Transactional(readOnly = true)
	public List<TenantDto> listAll() {
		UserPrincipal user = getCurrentUser();
		// Securely fetch only tenants belonging to user's org
		return tenantRepository.findByProperty_Organization_OrgId(user.getOrgId()).stream().map(this::toDto)
				.collect(Collectors.toList());
	}

	// =================================================================
	// LIST BY PROPERTY
	// =================================================================
	@Override
	@Transactional(readOnly = true)
	public List<TenantDto> listByProperty(UUID propertyId) {
		// 1. Fetch Property to check ownership first
		PropertyEntity property = propertyRepository.findById(propertyId)
				.orElseThrow(() -> new ResourceNotFoundException("Property not found"));

		// ✅ SECURITY CHECK
		validatePropertyAccess(property);

		return tenantRepository.findByProperty_PropertyId(propertyId).stream().map(this::toDto)
				.collect(Collectors.toList());
	}

	// =================================================================
	// LIST DOCUMENT
	// =================================================================
	@Override
	@Transactional(readOnly = true)
	public List<TenantDocumentResponse> listDocuments(UUID tenantId) {
		TenantEntity tenant = tenantRepository.findById(tenantId)
				.orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

		// ✅ Security Check
		validateTenantAccess(tenant);

		// Convert Entities to DTOs
		return tenant.getDocuments().stream().map(TenantDocumentResponse::from).collect(Collectors.toList());
	}

	// =================================================================
	// UPLOAD DOCUMENT
	// =================================================================
	@Override
	public TenantDocumentResponse uploadDocument(UUID tenantId, MultipartFile file, String documentType) {
		TenantEntity tenant = tenantRepository.findById(tenantId)
				.orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

		// ✅ SECURITY CHECK
		validateTenantAccess(tenant);

		try {
			AesEncryptionUtil.EncryptionResult encrypted = AesEncryptionUtil.encrypt(file.getBytes());
			String encryptedKey = encrypted.base64Key() + ":" + Base64.getEncoder().encodeToString(encrypted.iv());
			String s3Key = s3Service.uploadFile(encrypted.encryptedData(), file.getOriginalFilename());

			TenantDocumentEntity entity = TenantDocumentEntity.builder().tenant(tenant).documentType(documentType)
					.s3Key(s3Key).encryptedKey(encryptedKey).originalFileName(file.getOriginalFilename())
					.contentType(file.getContentType()).fileSize(file.getSize()).uploadedAt(Instant.now()).build();

			tenantDocumentRepository.save(entity);
			return TenantDocumentResponse.from(entity);

		} catch (Exception e) {
			throw new RuntimeException("Document upload failed", e);
		}
	}

	// =================================================================
	// DOWNLOAD DOCUMENT
	// =================================================================
	@Override
	@Transactional(readOnly = true)
	public DownloadedFile downloadTenantDocument(UUID documentId) {
		TenantDocumentEntity doc = tenantDocumentRepository.findById(documentId)
				.orElseThrow(() -> new ResourceNotFoundException("Document not found"));

		// ✅ SECURITY CHECK (Traverse: Doc -> Tenant -> Property -> Org)
		validateTenantAccess(doc.getTenant());

		try {
			String[] parts = doc.getEncryptedKey().split(":");
			SecretKey aesKey = AesEncryptionUtil.decodeKey(parts[0]);
			byte[] iv = Base64.getDecoder().decode(parts[1]);

			byte[] encryptedBytes = s3Service.downloadFile(doc.getS3Key());
			byte[] decrypted = AesEncryptionUtil.decrypt(encryptedBytes, aesKey, iv);

			return new DownloadedFile(decrypted, doc.getOriginalFileName(), doc.getContentType());

		} catch (Exception e) {
			throw new RuntimeException("Failed to decrypt document", e);
		}
	}

	// =================================================================
	// HELPERS
	// =================================================================

	private TenantDto toDto(TenantEntity tenant) {
		return TenantDto.builder().tenantId(tenant.getTenantId()).propertyId(tenant.getProperty().getPropertyId())
				.fullName(tenant.getFullName()).phone(tenant.getPhone()).email(tenant.getEmail())
				.leaseStart(tenant.getLeaseStart()).leaseEnd(tenant.getLeaseEnd()).monthlyRent(tenant.getMonthlyRent())
				.build();
	}

	/**
	 * Ensures the property belongs to the logged-in user's organization.
	 */
	private void validatePropertyAccess(PropertyEntity property) {
		UserPrincipal currentUser = getCurrentUser();
		if (!property.getOrganization().getOrgId().equals(currentUser.getOrgId())) {
			throw new AccessDeniedException("You do not have permission to access this property.");
		}
	}

	/**
	 * Ensures the tenant belongs to a property owned by the logged-in user's
	 * organization.
	 */
	private void validateTenantAccess(TenantEntity tenant) {
		validatePropertyAccess(tenant.getProperty());
	}
}