package ca.on.pms.mortgage.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MortgagePaymentDto {

	private UUID paymentId;

	@NotNull(message = "Property ID is required")
	private UUID propertyId;

	@NotNull(message = "Payment Date is required")
	private LocalDate paymentMonth;

	@NotNull(message = "Principal amount is required")
	@DecimalMin(value = "0.0", message = "Principal cannot be negative")
	private BigDecimal principalAmount;

	@NotNull(message = "Interest amount is required")
	@DecimalMin(value = "0.0", message = "Interest cannot be negative")
	private BigDecimal interestAmount;

	@NotNull(message = "Tax Year is required")
	private Integer taxYear;
}