package ca.on.pms.tenant.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ca.on.pms.tenant.entity.TenantEntity;

@Repository
public interface TenantRepository extends JpaRepository<TenantEntity, UUID> {
	List<TenantEntity> findByProperty_PropertyId(UUID propertyId);
}
