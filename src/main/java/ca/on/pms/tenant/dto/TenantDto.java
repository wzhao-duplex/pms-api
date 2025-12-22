package ca.on.pms.tenant.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

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
	private UUID propertyId;
	private String fullName;
	private String phone;
	private String email;
	private LocalDate leaseStart;
	private LocalDate leaseEnd;
	private BigDecimal monthlyRent;
}
