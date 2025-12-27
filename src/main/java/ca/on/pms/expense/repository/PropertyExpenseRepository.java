package ca.on.pms.expense.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ca.on.pms.expense.entity.PropertyExpenseEntity;

@Repository
public interface PropertyExpenseRepository extends JpaRepository<PropertyExpenseEntity, UUID> {
	List<PropertyExpenseEntity> findByProperty_PropertyId(UUID propertyId);

	List<PropertyExpenseEntity> findByProperty_Organization_OrgId(UUID orgId);
}