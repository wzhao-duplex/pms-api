package ca.on.pms.report.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class T776ReportLineDto {
	private String taxCode;
	private String description;
	private BigDecimal totalAmount;
}