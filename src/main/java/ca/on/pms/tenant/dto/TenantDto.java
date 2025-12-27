package ca.on.pms.tenant.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
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
public class TenantDto {

	private UUID tenantId;

	@NotNull(message = "Property ID is required")
	private UUID propertyId;

	@NotBlank(message = "Full Name is required")
	private String fullName;

	@NotBlank(message = "Phone number is required")
	private String phone;

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	private String email;

	@NotNull(message = "Lease start date is required")
	private LocalDate leaseStart;

	private LocalDate leaseEnd;

	@NotNull(message = "Monthly rent is required")
	@DecimalMin(value = "0.0", message = "Rent cannot be negative")
	private BigDecimal monthlyRent;
}