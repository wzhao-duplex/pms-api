package ca.on.pms.expense.service;

import java.util.List;
import java.util.UUID;
import ca.on.pms.expense.dto.PropertyExpenseDto;

public interface PropertyExpenseService {
	PropertyExpenseDto create(PropertyExpenseDto dto);

	PropertyExpenseDto update(UUID expenseId, PropertyExpenseDto dto);

	void delete(UUID expenseId);

	PropertyExpenseDto getById(UUID expenseId);

	List<PropertyExpenseDto> listAll();

	List<PropertyExpenseDto> listByProperty(UUID propertyId);
}