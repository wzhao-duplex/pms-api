package ca.on.pms.expense.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PropertyExpenseDto {

	private UUID expenseId;

	@NotNull(message = "Property ID is required")
	private UUID propertyId;

	@NotBlank(message = "Expense Type is required")
	private String expenseType; // e.g., "TAX", "INSURANCE", "MAINTENANCE"

	@NotNull(message = "Amount is required")
	@DecimalMin(value = "0.0", message = "Amount cannot be negative")
	private BigDecimal amount;

	@NotNull(message = "Expense Date is required")
	private LocalDate expenseDate;

	@NotNull(message = "Tax Year is required")
	private Integer taxYear;

	private String notes;
}