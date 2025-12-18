package ca.on.pms.organization.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.on.pms.organization.entity.OrganizationEntity;

public interface OrganizationRepository extends JpaRepository<OrganizationEntity, UUID> {

    /**
     * Find organizations owned by a specific user
     */
    List<OrganizationEntity> findByOwnerUser_UserId(UUID ownerUserId);

    /**
     * Find active organization by id
     */
    Optional<OrganizationEntity> findByOrgIdAndStatus(UUID orgId, String status);

    /**
     * Check if organization exists and is active
     */
    boolean existsByOrgIdAndStatus(UUID orgId, String status);
}
