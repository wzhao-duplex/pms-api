package ca.on.pms.property.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.on.pms.exception.ResourceNotFoundException;
import ca.on.pms.organization.entity.OrganizationEntity;
import ca.on.pms.organization.repository.OrganizationRepository;
import ca.on.pms.property.dto.PropertyCreateRequest;
import ca.on.pms.property.dto.PropertyResponse;
import ca.on.pms.property.dto.PropertyUpdateRequest;
import ca.on.pms.property.entity.PropertyEntity;
import ca.on.pms.property.repository.PropertyRepository;
import ca.on.pms.property.service.PropertyService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PropertyServiceImpl implements PropertyService {

	private final PropertyRepository propertyRepository;
	private final OrganizationRepository organizationRepository;

	@Override
	public PropertyResponse create(PropertyCreateRequest request) {

		OrganizationEntity org = organizationRepository.findById(request.getOrgId())
				.orElseThrow(() -> new IllegalArgumentException("Organization not found"));

		PropertyEntity entity = PropertyEntity.builder().organization(org).address(request.getAddress())
				.city(request.getCity()).province(request.getProvince()).postalCode(request.getPostalCode())
				.propertyType(request.getPropertyType()).ownershipPercent(request.getOwnershipPercent())
				.selfUsePercent(request.getSelfUsePercent()).managementCompany(request.getManagementCompany()).build();

		return toResponse(propertyRepository.save(entity));
	}

	@Override
	public PropertyResponse update(UUID propertyId, PropertyUpdateRequest request) {

		PropertyEntity entity = propertyRepository.findById(propertyId)
				.orElseThrow(() -> new ResourceNotFoundException("Property not found"));

		entity.setAddress(request.getAddress());
		entity.setCity(request.getCity());
		entity.setProvince(request.getProvince());
		entity.setPostalCode(request.getPostalCode());
		entity.setPropertyType(request.getPropertyType());
		entity.setOwnershipPercent(request.getOwnershipPercent());
		entity.setSelfUsePercent(request.getSelfUsePercent());
		entity.setManagementCompany(request.getManagementCompany());

		PropertyEntity updated = propertyRepository.save(entity); // explicitly save
		return toResponse(updated);
	}

	@Override
	@Transactional(readOnly = true)
	public PropertyResponse getById(UUID propertyId) {
		return propertyRepository.findById(propertyId).map(this::toResponse)
				.orElseThrow(() -> new ResourceNotFoundException("Property not found"));
	}

	@Override
	@Transactional(readOnly = true)
	public List<PropertyResponse> listByOrg(UUID orgId) {
		return propertyRepository.findByOrganization_OrgId(orgId).stream().map(this::toResponse).toList();
	}

	@Override
	public void delete(UUID propertyId) {
		propertyRepository.deleteById(propertyId);
	}

	private PropertyResponse toResponse(PropertyEntity e) {
		return PropertyResponse.builder().propertyId(e.getPropertyId()).orgId(e.getOrganization().getOrgId())
				.address(e.getAddress()).city(e.getCity()).province(e.getProvince()).postalCode(e.getPostalCode())
				.propertyType(e.getPropertyType()).ownershipPercent(e.getOwnershipPercent())
				.selfUsePercent(e.getSelfUsePercent()).managementCompany(e.getManagementCompany()).build();
	}
}
