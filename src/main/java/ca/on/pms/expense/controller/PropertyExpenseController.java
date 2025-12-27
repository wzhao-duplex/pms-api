package ca.on.pms.expense.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.on.pms.expense.dto.PropertyExpenseDto;
import ca.on.pms.expense.service.PropertyExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class PropertyExpenseController {

	private final PropertyExpenseService expenseService;

	@GetMapping
	public ResponseEntity<List<PropertyExpenseDto>> listAll() {
		return ResponseEntity.ok(expenseService.listAll());
	}

	@GetMapping("/property/{propertyId}")
	public ResponseEntity<List<PropertyExpenseDto>> listByProperty(@PathVariable UUID propertyId) {
		return ResponseEntity.ok(expenseService.listByProperty(propertyId));
	}

	@GetMapping("/{id}")
	public ResponseEntity<PropertyExpenseDto> getById(@PathVariable UUID id) {
		return ResponseEntity.ok(expenseService.getById(id));
	}

	@PostMapping
	public ResponseEntity<PropertyExpenseDto> create(@Valid @RequestBody PropertyExpenseDto dto) {
		return new ResponseEntity<>(expenseService.create(dto), HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public ResponseEntity<PropertyExpenseDto> update(@PathVariable UUID id,
			@Valid @RequestBody PropertyExpenseDto dto) {
		return ResponseEntity.ok(expenseService.update(id, dto));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable UUID id) {
		expenseService.delete(id);
		return ResponseEntity.noContent().build();
	}
}