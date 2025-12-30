package ca.on.pms.maintenance.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ca.on.pms.maintenance.entity.MaintenanceRecordEntity;

@Repository
public interface MaintenanceRepository extends JpaRepository<MaintenanceRecordEntity, UUID> {
	List<MaintenanceRecordEntity> findByProperty_Organization_OrgId(UUID orgId);
}