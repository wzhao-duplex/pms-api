package ca.on.pms.report.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ca.on.pms.property.entity.PropertyEntity;
import ca.on.pms.property.repository.PropertyRepository;
import ca.on.pms.report.dto.T776ReportLineDto;
import ca.on.pms.security.UserPrincipal;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaxReportService {

	private final JdbcTemplate jdbcTemplate; // Using JDBC for efficient aggregation
	private final PropertyRepository propertyRepository;

	private UserPrincipal getCurrentUser() {
		return (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	public List<T776ReportLineDto> generateT776Report(UUID propertyId, int year) {
		// 1. Security Check
		PropertyEntity property = propertyRepository.findById(propertyId)
				.orElseThrow(() -> new RuntimeException("Property not found"));

		UserPrincipal user = getCurrentUser();
		if (!property.getOrganization().getOrgId().equals(user.getOrgId())) {
			throw new AccessDeniedException("Access Denied");
		}

		// 2. Query
		// Join expenses with tax_codes to get description and sum amounts
		String sql = """
				    SELECT tc.code, tc.description, COALESCE(SUM(pe.amount), 0) as total
				    FROM tax_codes tc
				    LEFT JOIN property_expenses pe
				        ON tc.code = pe.tax_code
				        AND pe.property_id = ?
				        AND pe.tax_year = ?
				    GROUP BY tc.code, tc.description
				    ORDER BY tc.code ASC
				""";

		// 3. Execute
		return jdbcTemplate.query(sql, (rs, rowNum) -> new T776ReportLineDto(rs.getString("code"),
				rs.getString("description"), rs.getBigDecimal("total")), propertyId, year);
	}
}