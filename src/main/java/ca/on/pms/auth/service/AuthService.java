package ca.on.pms.auth.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.on.pms.auth.dto.AuthRequest;
import ca.on.pms.auth.dto.AuthResponse;
import ca.on.pms.auth.dto.RegisterRequest;
import ca.on.pms.auth.entity.TokenBlacklistEntity;
import ca.on.pms.auth.repository.TokenBlacklistRepository;
import ca.on.pms.organization.entity.OrganizationEntity;
import ca.on.pms.organization.repository.OrganizationRepository;
import ca.on.pms.security.JwtUtils;
import ca.on.pms.security.UserPrincipal;
import ca.on.pms.user.entity.UserEntity;
import ca.on.pms.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final OrganizationRepository orgRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtils jwtUtils;

	private final TokenBlacklistRepository blacklistRepository;

	@Transactional
	public AuthResponse register(RegisterRequest request) {
		if (userRepository.existsByEmail(request.email())) {
			throw new RuntimeException("Email already exists");
		}

		// 1. Create User (Initially no Org ID)
		UserEntity user = UserEntity.builder().email(request.email())
				.passwordHash(passwordEncoder.encode(request.password())).fullName(request.fullName()).role("OWNER")
				.status("ACTIVE").build();

		UserEntity savedUser = userRepository.save(user);

		// 2. Create Organization (1 Year Free)
		OrganizationEntity org = OrganizationEntity.builder().orgName(request.orgName()).ownerUser(savedUser)
				.subscriptionStart(LocalDate.now()).subscriptionEnd(LocalDate.now().plusYears(1)).status("ACTIVE")
				.build();

		OrganizationEntity savedOrg = orgRepository.save(org);

		// 3. Link User to Organization
		savedUser.setOrgId(savedOrg.getOrgId());
		userRepository.save(savedUser);

		// 4. Generate Token
		String token = jwtUtils.generateToken(UserPrincipal.create(savedUser));
		return new AuthResponse(token);
	}

	public AuthResponse login(AuthRequest request) {
		UserEntity user = userRepository.findByEmail(request.email())
				.orElseThrow(() -> new RuntimeException("Invalid credentials"));

		if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
			throw new RuntimeException("Invalid credentials");
		}

		// Subscription Check
		if (user.getOrgId() != null) {
			OrganizationEntity org = orgRepository.findById(user.getOrgId())
					.orElseThrow(() -> new RuntimeException("Organization not found"));

			if (LocalDate.now().isAfter(org.getSubscriptionEnd())) {
				throw new RuntimeException("Subscription expired on " + org.getSubscriptionEnd());
			}
		}

		String token = jwtUtils.generateToken(UserPrincipal.create(user));
		return new AuthResponse(token);
	}

	public void logout(String tokenHeader) {
		if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
			String token = tokenHeader.substring(7);

			// Get expiration from token so we don't keep it in DB forever
			// (Using your JwtUtils to extract expiration)
			try {
				java.util.Date expiration = jwtUtils.getClaims(token).getExpiration();
				LocalDateTime expiryDate = expiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

				TokenBlacklistEntity blacklisted = TokenBlacklistEntity.builder().token(token).expiryDate(expiryDate)
						.createdAt(LocalDateTime.now()).build();

				blacklistRepository.save(blacklisted);
			} catch (Exception e) {
				// Token might already be expired or invalid, just ignore
			}
		}
	}
}