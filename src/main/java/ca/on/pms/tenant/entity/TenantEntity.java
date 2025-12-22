package ca.on.pms.tenant.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import ca.on.pms.property.entity.PropertyEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tenants")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantEntity {

	@Id
	@GeneratedValue
	@Column(name = "tenant_id", columnDefinition = "uuid")
	private UUID tenantId;

	@ManyToOne
	@JoinColumn(name = "property_id", nullable = false)
	private PropertyEntity property;

	private String fullName;
	private String phone;
	private String email;
	private LocalDate leaseStart;
	private LocalDate leaseEnd;
	@Column(name = "monthly_rent", precision = 10, scale = 2)
	private BigDecimal monthlyRent;


	@OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL)
	private List<TenantDocumentEntity> documents;

	private LocalDateTime createdAt;
}
