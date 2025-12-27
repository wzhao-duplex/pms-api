package ca.on.pms.property.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ca.on.pms.property.dto.PropertyCreateRequest;
import ca.on.pms.property.dto.PropertyResponse;
import ca.on.pms.property.dto.PropertyUpdateRequest;
import ca.on.pms.property.service.PropertyService;
import jakarta.validation.Valid; // ⭐ Import this
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {

	private final PropertyService propertyService;

	// Create
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED) // Return 201 instead of 200
	public PropertyResponse create(@Valid @RequestBody PropertyCreateRequest request) {
		return propertyService.create(request);
	}

	// Update
	@PutMapping("/{propertyId}")
	public PropertyResponse update(@PathVariable UUID propertyId, @Valid @RequestBody PropertyUpdateRequest request) {
		return propertyService.update(propertyId, request);
	}

	// Read One
	@GetMapping("/{propertyId}")
	public PropertyResponse getById(@PathVariable UUID propertyId) {
		return propertyService.getById(propertyId);
	}

	// Read All (For Current User's Org)
	@GetMapping
	public List<PropertyResponse> listMyProperties() {
		return propertyService.listPropertiesForCurrentUser();
	}

	// Delete
	@DeleteMapping("/{propertyId}")
	@ResponseStatus(HttpStatus.NO_CONTENT) // Return 204 for delete
	public void delete(@PathVariable UUID propertyId) {
		propertyService.delete(propertyId);
	}
}