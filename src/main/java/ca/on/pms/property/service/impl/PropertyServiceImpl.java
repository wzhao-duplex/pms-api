package ca.on.pms.property.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
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
import ca.on.pms.security.UserPrincipal;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PropertyServiceImpl implements PropertyService {

	private final PropertyRepository propertyRepository;
	private final OrganizationRepository organizationRepository;

	private UserPrincipal getCurrentUser() {
		return (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	// ==========================================
	// CREATE
	// ==========================================
	@Override
	public PropertyResponse create(PropertyCreateRequest request) {
		UserPrincipal currentUser = getCurrentUser();

		// Safety check: User must be in an org
		if (currentUser.getOrgId() == null) {
			throw new IllegalStateException("User is not associated with an organization.");
		}

		// We use reference here to avoid a DB select if we trust the ID (optional
		// optimization)
		// But finding it ensures it exists.
		OrganizationEntity org = organizationRepository.findById(currentUser.getOrgId())
				.orElseThrow(() -> new IllegalStateException("Organization not found"));

		PropertyEntity entity = PropertyEntity.builder().organization(org).address(request.getAddress())
				.city(request.getCity()).province(request.getProvince()).postalCode(request.getPostalCode())
				.propertyType(request.getPropertyType()).ownershipPercent(request.getOwnershipPercent())
				.selfUsePercent(request.getSelfUsePercent()).managementCompany(request.getManagementCompany())
				// .createdAt is handled by @PrePersist in Entity
				.build();

		return toResponse(propertyRepository.save(entity));
	}

	// ==========================================
	// UPDATE
	// ==========================================
	@Override
	public PropertyResponse update(UUID propertyId, PropertyUpdateRequest request) {
		PropertyEntity entity = propertyRepository.findById(propertyId)
				.orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propertyId));

		validateOwnership(entity);

		entity.setAddress(request.getAddress());
		entity.setCity(request.getCity());
		entity.setProvince(request.getProvince());
		entity.setPostalCode(request.getPostalCode());
		entity.setPropertyType(request.getPropertyType());
		entity.setOwnershipPercent(request.getOwnershipPercent());
		entity.setSelfUsePercent(request.getSelfUsePercent());
		entity.setManagementCompany(request.getManagementCompany());

		return toResponse(propertyRepository.save(entity));
	}

	// ==========================================
	// GET ONE
	// ==========================================
	@Override
	@Transactional(readOnly = true)
	public PropertyResponse getById(UUID propertyId) {
		PropertyEntity entity = propertyRepository.findById(propertyId)
				.orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propertyId));

		validateOwnership(entity);

		return toResponse(entity);
	}

	// ==========================================
	// LIST ALL (Per Org)
	// ==========================================
	@Override
	@Transactional(readOnly = true)
	public List<PropertyResponse> listPropertiesForCurrentUser() {
		UserPrincipal currentUser = getCurrentUser();
		return propertyRepository.findByOrganization_OrgId(currentUser.getOrgId()).stream().map(this::toResponse)
				.toList();
	}

	// ==========================================
	// DELETE
	// ==========================================
	@Override
	public void delete(UUID propertyId) {
		PropertyEntity entity = propertyRepository.findById(propertyId)
				.orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propertyId));

		validateOwnership(entity);

		propertyRepository.delete(entity);
	}

	// ==========================================
	// HELPERS
	// ==========================================

	/**
	 * Prevents Horizontal Privilege Escalation. Users from Org A cannot touch
	 * Properties of Org B.
	 */
	private void validateOwnership(PropertyEntity entity) {
		UserPrincipal currentUser = getCurrentUser();
		if (!entity.getOrganization().getOrgId().equals(currentUser.getOrgId())) {
			throw new AccessDeniedException("You do not have permission to access this property.");
		}
	}

	private PropertyResponse toResponse(PropertyEntity e) {
		return PropertyResponse.builder().propertyId(e.getPropertyId()).orgId(e.getOrganization().getOrgId())
				.address(e.getAddress()).city(e.getCity()).province(e.getProvince()).postalCode(e.getPostalCode())
				.propertyType(e.getPropertyType()).ownershipPercent(e.getOwnershipPercent())
				.selfUsePercent(e.getSelfUsePercent()).managementCompany(e.getManagementCompany()).build();
	}
}