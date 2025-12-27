package ca.on.pms.income.service;

import java.util.List;
import java.util.UUID;

import ca.on.pms.income.dto.PropertyIncomeDto;

public interface PropertyIncomeService {

	PropertyIncomeDto create(PropertyIncomeDto dto);

	PropertyIncomeDto update(UUID incomeId, PropertyIncomeDto dto);

	void delete(UUID incomeId);

	PropertyIncomeDto getById(UUID incomeId);

	List<PropertyIncomeDto> listAll();

	List<PropertyIncomeDto> listByProperty(UUID propertyId);
}