package ca.on.pms.income.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.on.pms.income.dto.PropertyIncomeDto;
import ca.on.pms.income.service.PropertyIncomeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/incomes")
@RequiredArgsConstructor
public class PropertyIncomeController {

	private final PropertyIncomeService incomeService;

	@GetMapping
	public ResponseEntity<List<PropertyIncomeDto>> listAll() {
		return ResponseEntity.ok(incomeService.listAll());
	}

	@GetMapping("/property/{propertyId}")
	public ResponseEntity<List<PropertyIncomeDto>> listByProperty(@PathVariable UUID propertyId) {
		return ResponseEntity.ok(incomeService.listByProperty(propertyId));
	}

	@GetMapping("/{id}")
	public ResponseEntity<PropertyIncomeDto> getById(@PathVariable UUID id) {
		return ResponseEntity.ok(incomeService.getById(id));
	}

	@PostMapping
	public ResponseEntity<PropertyIncomeDto> create(@Valid @RequestBody PropertyIncomeDto dto) {
		return new ResponseEntity<>(incomeService.create(dto), HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public ResponseEntity<PropertyIncomeDto> update(@PathVariable UUID id, @Valid @RequestBody PropertyIncomeDto dto) {
		return ResponseEntity.ok(incomeService.update(id, dto));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable UUID id) {
		incomeService.delete(id);
		return ResponseEntity.noContent().build();
	}
}