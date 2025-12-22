package ca.on.pms.tenant.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import ca.on.pms.exception.ResourceNotFoundException;
import ca.on.pms.property.repository.PropertyRepository;
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

	@Override
	public TenantDto createTenant(TenantDto dto) {
		var property = propertyRepository.findById(dto.getPropertyId())
				.orElseThrow(() -> new RuntimeException("Property not found"));

		TenantEntity tenant = TenantEntity.builder().property(property).fullName(dto.getFullName())
				.phone(dto.getPhone()).email(dto.getEmail()).leaseStart(dto.getLeaseStart()).leaseEnd(dto.getLeaseEnd())
				.monthlyRent(dto.getMonthlyRent()).createdAt(LocalDateTime.now()).build();

		return toDto(tenantRepository.save(tenant));
	}

	@Override
	public TenantDto updateTenant(UUID tenantId, TenantDto dto) {
		TenantEntity tenant = tenantRepository.findById(tenantId)
				.orElseThrow(() -> new RuntimeException("Tenant not found"));

		tenant.setFullName(dto.getFullName());
		tenant.setPhone(dto.getPhone());
		tenant.setEmail(dto.getEmail());
		tenant.setLeaseStart(dto.getLeaseStart());
		tenant.setLeaseEnd(dto.getLeaseEnd());
		tenant.setMonthlyRent(dto.getMonthlyRent());

		return toDto(tenantRepository.save(tenant));
	}

	@Override
	public void deleteTenant(UUID tenantId) {
		tenantRepository.deleteById(tenantId);
	}

	@Override
	@Transactional(readOnly = true)
	public TenantDto getTenantById(UUID tenantId) {
		return tenantRepository.findById(tenantId).map(this::toDto)
				.orElseThrow(() -> new RuntimeException("Tenant not found"));
	}

	@Override
	@Transactional(readOnly = true)
	public List<TenantDto> listByProperty(UUID propertyId) {
		return tenantRepository.findByProperty_PropertyId(propertyId).stream().map(this::toDto)
				.collect(Collectors.toList());
	}

	@Override
	public TenantDocumentResponse uploadDocument(UUID tenantId, MultipartFile file, String documentType) {
		TenantEntity tenant = tenantRepository.findById(tenantId)
				.orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

		try {
			AesEncryptionUtil.EncryptionResult encrypted = AesEncryptionUtil.encrypt(file.getBytes());

			String encryptedKey = encrypted.base64Key() + ":" + Base64.getEncoder().encodeToString(encrypted.iv());

			String s3Key = s3Service.uploadFile(encrypted.encryptedData(), file.getOriginalFilename());

			// 3️⃣ Persist metadata
			TenantDocumentEntity entity = TenantDocumentEntity.builder()
			        .tenant(tenant)
			        .documentType(documentType)
			        .s3Key(s3Key)
			        .encryptedKey(encryptedKey)
			        .originalFileName(file.getOriginalFilename())
			        .contentType(file.getContentType())
			        .fileSize(file.getSize())
			        .uploadedAt(Instant.now())
			        .build();

			tenantDocumentRepository.save(entity);

			return TenantDocumentResponse.from(entity);

		} catch (Exception e) {
			throw new RuntimeException("Document upload failed", e);
		}
	}

	private TenantDto toDto(TenantEntity tenant) {
		return TenantDto.builder().tenantId(tenant.getTenantId()).propertyId(tenant.getProperty().getPropertyId())
				.fullName(tenant.getFullName()).phone(tenant.getPhone()).email(tenant.getEmail())
				.leaseStart(tenant.getLeaseStart()).leaseEnd(tenant.getLeaseEnd()).monthlyRent(tenant.getMonthlyRent())
				.build();
	}

	@Override
	@Transactional(readOnly = true)
	public DownloadedFile downloadTenantDocument(UUID documentId) {

		TenantDocumentEntity doc = tenantDocumentRepository.findById(documentId)
				.orElseThrow(() -> new ResourceNotFoundException("Document not found"));

		try {
			// 1️⃣ Extract AES key + IV
			String[] parts = doc.getEncryptedKey().split(":");
			SecretKey aesKey = AesEncryptionUtil.decodeKey(parts[0]);
			byte[] iv = Base64.getDecoder().decode(parts[1]);

			// 2️⃣ Download encrypted data
			byte[] encryptedBytes = s3Service.downloadFile(doc.getS3Key());

			// 3️⃣ Decrypt
			byte[] decrypted = AesEncryptionUtil.decrypt(encryptedBytes, aesKey, iv);

			return new DownloadedFile(decrypted, doc.getOriginalFileName(), doc.getContentType());

		} catch (Exception e) {
			throw new RuntimeException("Failed to decrypt document", e);
		}
	}
}
