package ca.on.pms.property.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import ca.on.pms.organization.entity.OrganizationEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "properties")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // ⭐ REQUIRED
@AllArgsConstructor
@Builder
public class PropertyEntity {

	@Id
	@GeneratedValue
	@Column(name = "property_id")
	private UUID propertyId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "org_id")
	private OrganizationEntity organization;

	private String address;
	private String city;
	private String province;
	private String postalCode;

	private String propertyType;
	private BigDecimal ownershipPercent;
	private BigDecimal selfUsePercent;
	private String managementCompany;

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		if (this.createdAt == null) {
			this.createdAt = LocalDateTime.now();
		}
	}
}
