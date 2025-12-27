package ca.on.pms.property.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PropertyUpdateRequest {

	@NotBlank(message = "Address is required")
	private String address;

	@NotBlank(message = "City is required")
	private String city;

	@NotBlank(message = "Province is required")
	private String province;

	@NotBlank(message = "Postal code is required")
	private String postalCode;

	@NotBlank(message = "Property type is required")
	private String propertyType;

	@NotNull(message = "Ownership percent is required")
	@DecimalMin(value = "0.0")
	@DecimalMax(value = "100.0")
	private BigDecimal ownershipPercent;

	@NotNull(message = "Self use percent is required")
	@DecimalMin(value = "0.0")
	@DecimalMax(value = "100.0")
	private BigDecimal selfUsePercent;

	private String managementCompany;
}