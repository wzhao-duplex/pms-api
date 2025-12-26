package ca.on.pms.property.service.impl;

import java.util.List;
import java.util.UUID;

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

    // Helper to get current user
    private UserPrincipal getCurrentUser() {
        return (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public PropertyResponse create(PropertyCreateRequest request) {
        UserPrincipal currentUser = getCurrentUser();
        UUID userOrgId = currentUser.getOrgId();

        if (userOrgId == null) {
            throw new IllegalStateException("User does not belong to an organization");
        }

        OrganizationEntity org = organizationRepository.findById(userOrgId)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));

        PropertyEntity entity = PropertyEntity.builder()
                .organization(org)
                .address(request.getAddress())
                .city(request.getCity())
                .province(request.getProvince())
                .postalCode(request.getPostalCode())
                .propertyType(request.getPropertyType())
                .ownershipPercent(request.getOwnershipPercent())
                .selfUsePercent(request.getSelfUsePercent())
                .managementCompany(request.getManagementCompany())
                .createdAt(java.time.LocalDateTime.now())
                .build();

        return toResponse(propertyRepository.save(entity));
    }

    @Override
    public PropertyResponse update(UUID propertyId, PropertyUpdateRequest request) {
        PropertyEntity entity = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        // Optional: Security check to ensure user owns this property
        UserPrincipal currentUser = getCurrentUser();
        if (!entity.getOrganization().getOrgId().equals(currentUser.getOrgId())) {
             throw new SecurityException("Access Denied");
        }

        entity.setAddress(request.getAddress());
        entity.setCity(request.getCity());
        entity.setProvince(request.getProvince());
        entity.setPostalCode(request.getPostalCode());
        entity.setPropertyType(request.getPropertyType());
        entity.setOwnershipPercent(request.getOwnershipPercent());
        entity.setSelfUsePercent(request.getSelfUsePercent());
        entity.setManagementCompany(request.getManagementCompany());

        PropertyEntity updated = propertyRepository.save(entity);
        return toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public PropertyResponse getById(UUID propertyId) {
        PropertyEntity entity = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        // Security check
        UserPrincipal currentUser = getCurrentUser();
        if (!entity.getOrganization().getOrgId().equals(currentUser.getOrgId())) {
             throw new SecurityException("Access Denied");
        }

        return toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropertyResponse> listPropertiesForCurrentUser() {
        UserPrincipal currentUser = getCurrentUser();
        // Automatically filter by the logged-in user's Organization ID
        return propertyRepository.findByOrganization_OrgId(currentUser.getOrgId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void delete(UUID propertyId) {
        // You should also fetch before delete to check permissions
        PropertyEntity entity = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        
        UserPrincipal currentUser = getCurrentUser();
        if (!entity.getOrganization().getOrgId().equals(currentUser.getOrgId())) {
             throw new SecurityException("Access Denied");
        }

        propertyRepository.deleteById(propertyId);
    }

    private PropertyResponse toResponse(PropertyEntity e) {
        return PropertyResponse.builder()
                .propertyId(e.getPropertyId())
                .orgId(e.getOrganization().getOrgId())
                .address(e.getAddress())
                .city(e.getCity())
                .province(e.getProvince())
                .postalCode(e.getPostalCode())
                .propertyType(e.getPropertyType())
                .ownershipPercent(e.getOwnershipPercent())
                .selfUsePercent(e.getSelfUsePercent())
                .managementCompany(e.getManagementCompany())
                .build();
    }
}