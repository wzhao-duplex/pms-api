package ca.on.pms.mortgage.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ca.on.pms.mortgage.entity.MortgagePaymentEntity;

@Repository
public interface MortgageRepository extends JpaRepository<MortgagePaymentEntity, UUID> {
	List<MortgagePaymentEntity> findByProperty_Organization_OrgId(UUID orgId);

	List<MortgagePaymentEntity> findByProperty_PropertyId(UUID propertyId);
}