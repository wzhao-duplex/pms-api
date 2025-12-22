package ca.on.pms.tenant.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ca.on.pms.tenant.entity.TenantDocumentEntity;

@Repository
public interface TenantDocumentRepository extends JpaRepository<TenantDocumentEntity, UUID> {
}
