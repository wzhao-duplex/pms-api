package ca.on.pms.expense.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.on.pms.exception.ResourceNotFoundException;
import ca.on.pms.expense.dto.PropertyExpenseDto;
import ca.on.pms.expense.entity.PropertyExpenseEntity;
import ca.on.pms.expense.repository.PropertyExpenseRepository;
import ca.on.pms.expense.service.PropertyExpenseService;
import ca.on.pms.property.entity.PropertyEntity;
import ca.on.pms.property.repository.PropertyRepository;
import ca.on.pms.security.UserPrincipal;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PropertyExpenseServiceImpl implements PropertyExpenseService {

	private final PropertyExpenseRepository expenseRepository;
	private final PropertyRepository propertyRepository;

	private UserPrincipal getCurrentUser() {
		return (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	@Override
	public PropertyExpenseDto create(PropertyExpenseDto dto) {
		PropertyEntity property = propertyRepository.findById(dto.getPropertyId())
				.orElseThrow(() -> new ResourceNotFoundException("Property not found"));

		validatePropertyAccess(property);

		PropertyExpenseEntity entity = PropertyExpenseEntity.builder().property(property)
				.expenseType(dto.getExpenseType()).amount(dto.getAmount()).expenseDate(dto.getExpenseDate())
				.taxYear(dto.getTaxYear()).notes(dto.getNotes()).build();

		return toDto(expenseRepository.save(entity));
	}

	@Override
	public PropertyExpenseDto update(UUID expenseId, PropertyExpenseDto dto) {
		PropertyExpenseEntity entity = expenseRepository.findById(expenseId)
				.orElseThrow(() -> new ResourceNotFoundException("Expense record not found"));

		validatePropertyAccess(entity.getProperty());

		entity.setExpenseType(dto.getExpenseType());
		entity.setAmount(dto.getAmount());
		entity.setExpenseDate(dto.getExpenseDate());
		entity.setTaxYear(dto.getTaxYear());
		entity.setNotes(dto.getNotes());

		return toDto(expenseRepository.save(entity));
	}

	@Override
	public void delete(UUID expenseId) {
		PropertyExpenseEntity entity = expenseRepository.findById(expenseId)
				.orElseThrow(() -> new ResourceNotFoundException("Expense record not found"));

		validatePropertyAccess(entity.getProperty());
		expenseRepository.delete(entity);
	}

	@Override
	@Transactional(readOnly = true)
	public PropertyExpenseDto getById(UUID expenseId) {
		PropertyExpenseEntity entity = expenseRepository.findById(expenseId)
				.orElseThrow(() -> new ResourceNotFoundException("Expense record not found"));

		validatePropertyAccess(entity.getProperty());
		return toDto(entity);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PropertyExpenseDto> listAll() {
		UserPrincipal user = getCurrentUser();
		return expenseRepository.findByProperty_Organization_OrgId(user.getOrgId()).stream().map(this::toDto)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<PropertyExpenseDto> listByProperty(UUID propertyId) {
		PropertyEntity property = propertyRepository.findById(propertyId)
				.orElseThrow(() -> new ResourceNotFoundException("Property not found"));

		validatePropertyAccess(property);

		return expenseRepository.findByProperty_PropertyId(propertyId).stream().map(this::toDto)
				.collect(Collectors.toList());
	}

	private void validatePropertyAccess(PropertyEntity property) {
		UserPrincipal currentUser = getCurrentUser();
		if (!property.getOrganization().getOrgId().equals(currentUser.getOrgId())) {
			throw new AccessDeniedException("You do not have permission to access this property.");
		}
	}

	private PropertyExpenseDto toDto(PropertyExpenseEntity e) {
		return PropertyExpenseDto.builder().expenseId(e.getExpenseId()).propertyId(e.getProperty().getPropertyId())
				.expenseType(e.getExpenseType()).amount(e.getAmount()).expenseDate(e.getExpenseDate())
				.taxYear(e.getTaxYear()).notes(e.getNotes()).build();
	}
}