package ca.on.pms.mortgage.controller;

import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ca.on.pms.mortgage.dto.MortgagePaymentDto;
import ca.on.pms.mortgage.service.MortgageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/mortgages")
@RequiredArgsConstructor
public class MortgageController {

	private final MortgageService mortgageService;

	@GetMapping
	public ResponseEntity<List<MortgagePaymentDto>> listAll() {
		return ResponseEntity.ok(mortgageService.listAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<MortgagePaymentDto> getById(@PathVariable UUID id) {
		return ResponseEntity.ok(mortgageService.getById(id));
	}

	@PostMapping
	public ResponseEntity<MortgagePaymentDto> create(@Valid @RequestBody MortgagePaymentDto dto) {
		return new ResponseEntity<>(mortgageService.create(dto), HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public ResponseEntity<MortgagePaymentDto> update(@PathVariable UUID id,
			@Valid @RequestBody MortgagePaymentDto dto) {
		return ResponseEntity.ok(mortgageService.update(id, dto));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable UUID id) {
		mortgageService.delete(id);
		return ResponseEntity.noContent().build();
	}
}