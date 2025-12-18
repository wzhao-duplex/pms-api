package ca.on.pms.organization.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import ca.on.pms.user.entity.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "organizations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationEntity {

    @Id
    @GeneratedValue
    @Column(name = "org_id", updatable = false, nullable = false)
    private UUID orgId;

    @Column(name = "org_name", nullable = false, length = 255)
    private String orgName;

    /**
     * Organization owner (user who created / owns this org)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "owner_user_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_org_owner")
    )
    private UserEntity ownerUser;

    @Column(name = "subscription_start")
    private LocalDate subscriptionStart;

    @Column(name = "subscription_end")
    private LocalDate subscriptionEnd;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Automatically set created_at
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = "ACTIVE";
        }
    }
}
