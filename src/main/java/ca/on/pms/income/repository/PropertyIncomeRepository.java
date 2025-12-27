package ca.on.pms.income.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ca.on.pms.income.entity.PropertyIncomeEntity;

@Repository
public interface PropertyIncomeRepository extends JpaRepository<PropertyIncomeEntity, UUID> {

	// Find all income for a specific property
	List<PropertyIncomeEntity> findByProperty_PropertyId(UUID propertyId);

	// Security: Find all income belonging to an Organization
	List<PropertyIncomeEntity> findByProperty_Organization_OrgId(UUID orgId);
}