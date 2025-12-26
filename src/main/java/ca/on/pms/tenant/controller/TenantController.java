package ca.on.pms.tenant.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ca.on.pms.tenant.dto.DownloadedFile;
import ca.on.pms.tenant.dto.TenantDocumentResponse;
import ca.on.pms.tenant.dto.TenantDto;
import ca.on.pms.tenant.service.TenantService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
public class TenantController {

	private final TenantService tenantService;

	@GetMapping
	public ResponseEntity<List<TenantDto>> listAll() {
		return ResponseEntity.ok(tenantService.listAll());
	}

	@PostMapping
	public ResponseEntity<TenantDto> create(@RequestBody TenantDto dto) {
		return ResponseEntity.ok(tenantService.createTenant(dto));
	}

	@PutMapping("/{id}")
	public ResponseEntity<TenantDto> update(@PathVariable UUID id, @RequestBody TenantDto dto) {
		return ResponseEntity.ok(tenantService.updateTenant(id, dto));
	}

	@GetMapping("/{id}")
	public ResponseEntity<TenantDto> getById(@PathVariable UUID id) {
		return ResponseEntity.ok(tenantService.getTenantById(id));
	}

	@GetMapping("/property/{propertyId}")
	public ResponseEntity<List<TenantDto>> listByProperty(@PathVariable UUID propertyId) {
		return ResponseEntity.ok(tenantService.listByProperty(propertyId));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable UUID id) {
		tenantService.deleteTenant(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{id}/documents")
	public ResponseEntity<TenantDocumentResponse> uploadDocument(@PathVariable UUID id,
			@RequestParam("file") MultipartFile file, @RequestParam("documentType") String documentType) {
		return ResponseEntity.ok(tenantService.uploadDocument(id, file, documentType));
	}

	@GetMapping("/documents/{id}/download")
	public ResponseEntity<byte[]> downloadTenantDocument(@PathVariable UUID id) {

		DownloadedFile file = tenantService.downloadTenantDocument(id);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.fileName() + "\"")
				.contentType(MediaType.parseMediaType(file.contentType())).body(file.data());
	}

}
