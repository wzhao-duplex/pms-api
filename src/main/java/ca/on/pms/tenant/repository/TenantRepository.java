package ca.on.pms.tenant.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ca.on.pms.tenant.entity.TenantEntity;

@Repository
public interface TenantRepository extends JpaRepository<TenantEntity, UUID> {

	// Existing
	List<TenantEntity> findByProperty_PropertyId(UUID propertyId);

	// ✅ NEW: Find all tenants belonging to a specific Organization
	// JPA automatically joins Tenant -> Property -> Organization -> OrgId
	List<TenantEntity> findByProperty_Organization_OrgId(UUID orgId);
}