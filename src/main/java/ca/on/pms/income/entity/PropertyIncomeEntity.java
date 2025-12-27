package ca.on.pms.income.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import ca.on.pms.property.entity.PropertyEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "property_income")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyIncomeEntity {

	@Id
	@GeneratedValue
	@Column(name = "income_id")
	private UUID incomeId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "property_id", nullable = false)
	private PropertyEntity property;

	@Column(name = "income_type", nullable = false)
	private String incomeType;

	@Column(nullable = false)
	private BigDecimal amount;

	@Column(name = "income_date", nullable = false)
	private LocalDate incomeDate;

	@Column(name = "tax_year", nullable = false)
	private Integer taxYear;

	@Column(length = 500)
	private String notes;

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		if (this.createdAt == null) {
			this.createdAt = LocalDateTime.now();
		}
	}
}