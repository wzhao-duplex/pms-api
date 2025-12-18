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

    List<PropertyResponse> listByOrg(UUID orgId);

    void delete(UUID propertyId);
}
