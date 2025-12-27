package ca.on.pms.income.dto;

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
public class PropertyIncomeDto {

	private UUID incomeId;

	@NotNull(message = "Property ID is required")
	private UUID propertyId;

	@NotBlank(message = "Income Type is required")
	private String incomeType; // e.g., "RENT", "PARKING", "LAUNDRY"

	@NotNull(message = "Amount is required")
	@DecimalMin(value = "0.0", message = "Amount cannot be negative")
	private BigDecimal amount;

	@NotNull(message = "Income Date is required")
	private LocalDate incomeDate;

	@NotNull(message = "Tax Year is required")
	private Integer taxYear;

	private String notes;
}