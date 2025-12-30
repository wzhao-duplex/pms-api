package ca.on.pms.mortgage.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import ca.on.pms.property.entity.PropertyEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mortgage_payments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MortgagePaymentEntity {

	@Id
	@GeneratedValue
	@Column(name = "payment_id")
	private UUID paymentId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "property_id", nullable = false)
	private PropertyEntity property;

	@Column(name = "payment_month", nullable = false)
	private LocalDate paymentMonth;

	@Column(name = "principal_amount", nullable = false)
	private BigDecimal principalAmount;

	@Column(name = "interest_amount", nullable = false)
	private BigDecimal interestAmount;

	@Column(name = "tax_year", nullable = false)
	private Integer taxYear;

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		if (this.createdAt == null) {
			this.createdAt = LocalDateTime.now();
		}
	}
}