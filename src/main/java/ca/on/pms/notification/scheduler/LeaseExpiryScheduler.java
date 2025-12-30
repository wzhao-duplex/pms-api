package ca.on.pms.notification.scheduler;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ca.on.pms.notification.service.EmailService;
import ca.on.pms.tenant.entity.TenantEntity;
import ca.on.pms.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LeaseExpiryScheduler {

	private final TenantRepository tenantRepository;
	private final EmailService emailService;

	// Run every day at 9:00 AM
	@Scheduled(cron = "0 0 9 * * ?")
	@Transactional(readOnly = true)
	public void checkLeaseExpiries() {
		System.out.println("⏰ Checking for expiring leases...");

		LocalDate today = LocalDate.now();
		// Look for leases expiring exactly 60 days from now (Typical Ontario Notice
		// period)
		LocalDate targetDate = today.plusDays(60);

		// Also check for 30 days as a secondary reminder
		// For simplicity in this query, we fetch a range: 30 to 60 days
		List<TenantEntity> expiringTenants = tenantRepository.findExpiringLeases(today, targetDate);

		for (TenantEntity tenant : expiringTenants) {
			String ownerEmail = tenant.getProperty().getOrganization().getOwnerUser().getEmail();
			String subject = "Action Required: Lease Expiring for " + tenant.getFullName();

			String body = String.format("""
					Hello,

					The lease for tenant %s at %s is set to expire on %s.

					Please prepare the necessary renewal forms (N1/N2) or termination notices if applicable.

					Regards,
					PMS Automation
					""", tenant.getFullName(), tenant.getProperty().getAddress(), tenant.getLeaseEnd().toString());

			// Send Email
			emailService.sendSimpleEmail(ownerEmail, subject, body);
		}
	}
}