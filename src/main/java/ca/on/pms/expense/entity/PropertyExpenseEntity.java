package ca.on.pms.expense.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import ca.on.pms.property.entity.PropertyEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "property_expenses")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyExpenseEntity {

	@Id
	@GeneratedValue
	@Column(name = "expense_id")
	private UUID expenseId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "property_id", nullable = false)
	private PropertyEntity property;

	@Column(name = "expense_type", nullable = false)
	private String expenseType;

	@Column(nullable = false)
	private BigDecimal amount;

	@Column(name = "expense_date", nullable = false)
	private LocalDate expenseDate;

	@Column(name = "tax_year", nullable = false)
	private Integer taxYear;

	@Column(length = 500)
	private String notes;

	@Column(name = "tax_code")
	private String taxCode; // Stores '8521', '9180', etc.

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		if (this.createdAt == null) {
			this.createdAt = LocalDateTime.now();
		}
	}
}