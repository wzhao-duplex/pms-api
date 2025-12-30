package ca.on.pms.maintenance.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.on.pms.exception.ResourceNotFoundException;
import ca.on.pms.maintenance.dto.MaintenanceRecordDto;
import ca.on.pms.maintenance.entity.MaintenanceRecordEntity;
import ca.on.pms.maintenance.repository.MaintenanceRepository;
import ca.on.pms.property.entity.PropertyEntity;
import ca.on.pms.property.repository.PropertyRepository;
import ca.on.pms.security.UserPrincipal;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MaintenanceService {

	private final MaintenanceRepository maintenanceRepository;
	private final PropertyRepository propertyRepository;

	private UserPrincipal getCurrentUser() {
		return (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	public MaintenanceRecordDto create(MaintenanceRecordDto dto) {
		PropertyEntity property = propertyRepository.findById(dto.getPropertyId())
				.orElseThrow(() -> new ResourceNotFoundException("Property not found"));
		validatePropertyAccess(property);

		MaintenanceRecordEntity entity = MaintenanceRecordEntity.builder().property(property)
				.category(dto.getCategory()).description(dto.getDescription()).cost(dto.getCost())
				.contractorName(dto.getContractorName()).contractorPhone(dto.getContractorPhone())
				.serviceDate(dto.getServiceDate()).build();

		return toDto(maintenanceRepository.save(entity));
	}

	public MaintenanceRecordDto update(UUID id, MaintenanceRecordDto dto) {
		MaintenanceRecordEntity entity = maintenanceRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Record not found"));
		validatePropertyAccess(entity.getProperty());

		entity.setCategory(dto.getCategory());
		entity.setDescription(dto.getDescription());
		entity.setCost(dto.getCost());
		entity.setContractorName(dto.getContractorName());
		entity.setContractorPhone(dto.getContractorPhone());
		entity.setServiceDate(dto.getServiceDate());

		return toDto(maintenanceRepository.save(entity));
	}

	public void delete(UUID id) {
		MaintenanceRecordEntity entity = maintenanceRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Record not found"));
		validatePropertyAccess(entity.getProperty());
		maintenanceRepository.delete(entity);
	}

	@Transactional(readOnly = true)
	public List<MaintenanceRecordDto> listAll() {
		return maintenanceRepository.findByProperty_Organization_OrgId(getCurrentUser().getOrgId()).stream()
				.map(this::toDto).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public MaintenanceRecordDto getById(UUID id) {
		MaintenanceRecordEntity entity = maintenanceRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Record not found"));
		validatePropertyAccess(entity.getProperty());
		return toDto(entity);
	}

	private void validatePropertyAccess(PropertyEntity property) {
		if (!property.getOrganization().getOrgId().equals(getCurrentUser().getOrgId())) {
			throw new AccessDeniedException("Access Denied");
		}
	}

	private MaintenanceRecordDto toDto(MaintenanceRecordEntity e) {
		return MaintenanceRecordDto.builder().maintenanceId(e.getMaintenanceId())
				.propertyId(e.getProperty().getPropertyId()).category(e.getCategory()).description(e.getDescription())
				.cost(e.getCost()).contractorName(e.getContractorName()).contractorPhone(e.getContractorPhone())
				.serviceDate(e.getServiceDate()).build();
	}
}