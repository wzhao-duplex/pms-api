package ca.on.pms.maintenance.controller;

import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ca.on.pms.maintenance.dto.MaintenanceRecordDto;
import ca.on.pms.maintenance.service.MaintenanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
public class MaintenanceController {

	private final MaintenanceService maintenanceService;

	@GetMapping
	public ResponseEntity<List<MaintenanceRecordDto>> listAll() {
		return ResponseEntity.ok(maintenanceService.listAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<MaintenanceRecordDto> getById(@PathVariable UUID id) {
		return ResponseEntity.ok(maintenanceService.getById(id));
	}

	@PostMapping
	public ResponseEntity<MaintenanceRecordDto> create(@Valid @RequestBody MaintenanceRecordDto dto) {
		return new ResponseEntity<>(maintenanceService.create(dto), HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public ResponseEntity<MaintenanceRecordDto> update(@PathVariable UUID id,
			@Valid @RequestBody MaintenanceRecordDto dto) {
		return ResponseEntity.ok(maintenanceService.update(id, dto));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable UUID id) {
		maintenanceService.delete(id);
		return ResponseEntity.noContent().build();
	}
}