package ca.on.pms.mortgage.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.on.pms.exception.ResourceNotFoundException;
import ca.on.pms.mortgage.dto.MortgagePaymentDto;
import ca.on.pms.mortgage.entity.MortgagePaymentEntity;
import ca.on.pms.mortgage.repository.MortgageRepository;
import ca.on.pms.property.entity.PropertyEntity;
import ca.on.pms.property.repository.PropertyRepository;
import ca.on.pms.security.UserPrincipal;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MortgageService {

	private final MortgageRepository mortgageRepository;
	private final PropertyRepository propertyRepository;

	private UserPrincipal getCurrentUser() {
		return (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	public MortgagePaymentDto create(MortgagePaymentDto dto) {
		PropertyEntity property = propertyRepository.findById(dto.getPropertyId())
				.orElseThrow(() -> new ResourceNotFoundException("Property not found"));
		validatePropertyAccess(property);

		MortgagePaymentEntity entity = MortgagePaymentEntity.builder().property(property)
				.paymentMonth(dto.getPaymentMonth()).principalAmount(dto.getPrincipalAmount())
				.interestAmount(dto.getInterestAmount()).taxYear(dto.getTaxYear()).build();

		return toDto(mortgageRepository.save(entity));
	}

	public MortgagePaymentDto update(UUID id, MortgagePaymentDto dto) {
		MortgagePaymentEntity entity = mortgageRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
		validatePropertyAccess(entity.getProperty());

		entity.setPaymentMonth(dto.getPaymentMonth());
		entity.setPrincipalAmount(dto.getPrincipalAmount());
		entity.setInterestAmount(dto.getInterestAmount());
		entity.setTaxYear(dto.getTaxYear());

		return toDto(mortgageRepository.save(entity));
	}

	public void delete(UUID id) {
		MortgagePaymentEntity entity = mortgageRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
		validatePropertyAccess(entity.getProperty());
		mortgageRepository.delete(entity);
	}

	@Transactional(readOnly = true)
	public MortgagePaymentDto getById(UUID id) {
		MortgagePaymentEntity entity = mortgageRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
		validatePropertyAccess(entity.getProperty());
		return toDto(entity);
	}

	@Transactional(readOnly = true)
	public List<MortgagePaymentDto> listAll() {
		return mortgageRepository.findByProperty_Organization_OrgId(getCurrentUser().getOrgId()).stream()
				.map(this::toDto).collect(Collectors.toList());
	}

	private void validatePropertyAccess(PropertyEntity property) {
		if (!property.getOrganization().getOrgId().equals(getCurrentUser().getOrgId())) {
			throw new AccessDeniedException("Access Denied");
		}
	}

	private MortgagePaymentDto toDto(MortgagePaymentEntity e) {
		return MortgagePaymentDto.builder().paymentId(e.getPaymentId()).propertyId(e.getProperty().getPropertyId())
				.paymentMonth(e.getPaymentMonth()).principalAmount(e.getPrincipalAmount())
				.interestAmount(e.getInterestAmount()).taxYear(e.getTaxYear()).build();
	}
}