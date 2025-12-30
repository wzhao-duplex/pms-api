package ca.on.pms.maintenance.dto;

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
public class MaintenanceRecordDto {
	private UUID maintenanceId;

	@NotNull(message = "Property ID is required")
	private UUID propertyId;

	@NotBlank(message = "Category is required")
	private String category;

	@NotBlank(message = "Description is required")
	private String description;

	@NotNull(message = "Cost is required")
	@DecimalMin(value = "0.0")
	private BigDecimal cost;

	private String contractorName;
	private String contractorPhone;

	@NotNull(message = "Service Date is required")
	private LocalDate serviceDate;
}