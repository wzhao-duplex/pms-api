package ca.on.pms.maintenance.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import ca.on.pms.property.entity.PropertyEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "maintenance_records")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceRecordEntity {

	@Id
	@GeneratedValue
	@Column(name = "maintenance_id")
	private UUID maintenanceId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "property_id", nullable = false)
	private PropertyEntity property;

	private String category;

	@Column(length = 1000)
	private String description;

	private BigDecimal cost;

	@Column(name = "contractor_name")
	private String contractorName;

	@Column(name = "contractor_phone")
	private String contractorPhone;

	@Column(name = "service_date")
	private LocalDate serviceDate;

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		if (this.createdAt == null)
			this.createdAt = LocalDateTime.now();
	}
}