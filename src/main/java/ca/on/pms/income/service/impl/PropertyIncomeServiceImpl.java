package ca.on.pms.income.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.on.pms.exception.ResourceNotFoundException;
import ca.on.pms.income.dto.PropertyIncomeDto;
import ca.on.pms.income.entity.PropertyIncomeEntity;
import ca.on.pms.income.repository.PropertyIncomeRepository;
import ca.on.pms.income.service.PropertyIncomeService;
import ca.on.pms.property.entity.PropertyEntity;
import ca.on.pms.property.repository.PropertyRepository;
import ca.on.pms.security.UserPrincipal;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PropertyIncomeServiceImpl implements PropertyIncomeService {

	private final PropertyIncomeRepository incomeRepository;
	private final PropertyRepository propertyRepository;

	private UserPrincipal getCurrentUser() {
		return (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	@Override
	public PropertyIncomeDto create(PropertyIncomeDto dto) {
		// 1. Fetch Property
		PropertyEntity property = propertyRepository.findById(dto.getPropertyId())
				.orElseThrow(() -> new ResourceNotFoundException("Property not found"));

		// 2. Security Check: Does this property belong to user's org?
		validatePropertyAccess(property);

		// 3. Create Entity
		PropertyIncomeEntity entity = PropertyIncomeEntity.builder().property(property).incomeType(dto.getIncomeType())
				.amount(dto.getAmount()).incomeDate(dto.getIncomeDate()).taxYear(dto.getTaxYear()).notes(dto.getNotes())
				.build();

		return toDto(incomeRepository.save(entity));
	}

	@Override
	public PropertyIncomeDto update(UUID incomeId, PropertyIncomeDto dto) {
		PropertyIncomeEntity entity = incomeRepository.findById(incomeId)
				.orElseThrow(() -> new ResourceNotFoundException("Income record not found"));

		// Security Check
		validatePropertyAccess(entity.getProperty());

		entity.setIncomeType(dto.getIncomeType());
		entity.setAmount(dto.getAmount());
		entity.setIncomeDate(dto.getIncomeDate());
		entity.setTaxYear(dto.getTaxYear());
		entity.setNotes(dto.getNotes());

		return toDto(incomeRepository.save(entity));
	}

	@Override
	public void delete(UUID incomeId) {
		PropertyIncomeEntity entity = incomeRepository.findById(incomeId)
				.orElseThrow(() -> new ResourceNotFoundException("Income record not found"));

		validatePropertyAccess(entity.getProperty());

		incomeRepository.delete(entity);
	}

	@Override
	@Transactional(readOnly = true)
	public PropertyIncomeDto getById(UUID incomeId) {
		PropertyIncomeEntity entity = incomeRepository.findById(incomeId)
				.orElseThrow(() -> new ResourceNotFoundException("Income record not found"));

		validatePropertyAccess(entity.getProperty());

		return toDto(entity);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PropertyIncomeDto> listAll() {
		UserPrincipal user = getCurrentUser();
		return incomeRepository.findByProperty_Organization_OrgId(user.getOrgId()).stream().map(this::toDto)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<PropertyIncomeDto> listByProperty(UUID propertyId) {
		PropertyEntity property = propertyRepository.findById(propertyId)
				.orElseThrow(() -> new ResourceNotFoundException("Property not found"));

		validatePropertyAccess(property);

		return incomeRepository.findByProperty_PropertyId(propertyId).stream().map(this::toDto)
				.collect(Collectors.toList());
	}

	// --- Helpers ---

	private void validatePropertyAccess(PropertyEntity property) {
		UserPrincipal currentUser = getCurrentUser();
		if (!property.getOrganization().getOrgId().equals(currentUser.getOrgId())) {
			throw new AccessDeniedException("You do not have permission to access this property.");
		}
	}

	private PropertyIncomeDto toDto(PropertyIncomeEntity e) {
		return PropertyIncomeDto.builder().incomeId(e.getIncomeId()).propertyId(e.getProperty().getPropertyId())
				.incomeType(e.getIncomeType()).amount(e.getAmount()).incomeDate(e.getIncomeDate())
				.taxYear(e.getTaxYear()).notes(e.getNotes()).build();
	}
}