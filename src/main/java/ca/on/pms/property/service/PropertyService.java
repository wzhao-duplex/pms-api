package ca.on.pms.property.service;

import java.util.List;
import java.util.UUID;

import ca.on.pms.property.dto.PropertyCreateRequest;
import ca.on.pms.property.dto.PropertyResponse;
import ca.on.pms.property.dto.PropertyUpdateRequest;

public interface PropertyService {

	PropertyResponse create(PropertyCreateRequest request);

	PropertyResponse update(UUID propertyId, PropertyUpdateRequest request);

	PropertyResponse getById(UUID propertyId);

	// Renamed from listByOrg(UUID orgId) to reflect security context usage
	List<PropertyResponse> listPropertiesForCurrentUser();

	void delete(UUID propertyId);
}