package ca.on.pms.tenant.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ca.on.pms.tenant.entity.TenantEntity;

@Repository
public interface TenantRepository extends JpaRepository<TenantEntity, UUID> {

	// Existing
	List<TenantEntity> findByProperty_PropertyId(UUID propertyId);

	// ✅ NEW: Find all tenants belonging to a specific Organization
	// JPA automatically joins Tenant -> Property -> Organization -> OrgId
	List<TenantEntity> findByProperty_Organization_OrgId(UUID orgId);

	// ✅ NEW: Find leases expiring between two dates (e.g., next 60 days)
	// We join Property -> Organization -> OwnerUser to get the email address later
	@Query("SELECT t FROM TenantEntity t " + "JOIN FETCH t.property p " + "JOIN FETCH p.organization o "
			+ "JOIN FETCH o.ownerUser u " + "WHERE t.leaseEnd BETWEEN :startDate AND :endDate")
	List<TenantEntity> findExpiringLeases(LocalDate startDate, LocalDate endDate);
}