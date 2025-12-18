package ca.on.pms.property.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.on.pms.property.entity.PropertyEntity;

public interface PropertyRepository extends JpaRepository<PropertyEntity, UUID> {

    List<PropertyEntity> findByOrganization_OrgId(UUID orgId);
}
