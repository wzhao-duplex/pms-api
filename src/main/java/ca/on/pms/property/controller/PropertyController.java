package ca.on.pms.property.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.on.pms.property.dto.PropertyCreateRequest;
import ca.on.pms.property.dto.PropertyResponse;
import ca.on.pms.property.dto.PropertyUpdateRequest;
import ca.on.pms.property.service.PropertyService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {

	private final PropertyService propertyService;

	@PostMapping
	public PropertyResponse create(@RequestBody PropertyCreateRequest request) {
		return propertyService.create(request);
	}

	@PutMapping("/{propertyId}")
	public PropertyResponse update(@PathVariable UUID propertyId, @RequestBody PropertyUpdateRequest request) {
		return propertyService.update(propertyId, request);
	}

	@GetMapping("/{propertyId}")
	public PropertyResponse getById(@PathVariable UUID propertyId) {
		return propertyService.getById(propertyId);
	}

	/**
	 * FIXED: This replaces the old listByOrg method. We do not accept an orgId
	 * parameter anymore. The service extracts the orgId from the logged-in user's
	 * token.
	 */
	@GetMapping
	public List<PropertyResponse> listMyProperties() {
		return propertyService.listPropertiesForCurrentUser();
	}

	@DeleteMapping("/{propertyId}")
	public void delete(@PathVariable UUID propertyId) {
		propertyService.delete(propertyId);
	}
}