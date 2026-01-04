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
import java.util.Random;
import ca.on.pms.auth.dto.VerifyRequest;
import ca.on.pms.notification.service.EmailService;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final OrganizationRepository orgRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtils jwtUtils;
	private final EmailService emailService;

	private final TokenBlacklistRepository blacklistRepository;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already exists");
        }

        // 1. Generate 6-Digit Code
        String code = String.valueOf(new Random().nextInt(900000) + 100000);

        // 2. Create User with PENDING status
        UserEntity user = UserEntity.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .role("OWNER")
                .status("PENDING") // ✅ Default to PENDING
                .verificationCode(code) // ✅ Save Code
                .verificationExpiry(LocalDateTime.now().plusMinutes(15)) // ✅ Valid for 15 mins
                .build();
        
        UserEntity savedUser = userRepository.save(user);

        // 3. Create Org (Same as before)
        OrganizationEntity org = OrganizationEntity.builder()
                .orgName(request.orgName())
                .ownerUser(savedUser)
                .subscriptionStart(LocalDate.now())
                .subscriptionEnd(LocalDate.now().plusYears(1))
                .status("ACTIVE")
                .build();
        OrganizationEntity savedOrg = orgRepository.save(org);
        savedUser.setOrgId(savedOrg.getOrgId());
        userRepository.save(savedUser);

        // 4. Send Email
        emailService.sendSimpleEmail(
            user.getEmail(), 
            "Verify your PMS Account", 
            "Welcome! Your verification code is: " + code + "\nIt expires in 15 minutes."
        );

        // Return empty token or specific message indicating verification needed
        return new AuthResponse("VERIFY_NEEDED"); 
    }

    @Transactional
    public void verify(VerifyRequest request) {
        UserEntity user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if ("ACTIVE".equals(user.getStatus())) {
            return; // Already verified
        }

        if (user.getVerificationExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Verification code expired. Please register again or request new code.");
        }

        if (!user.getVerificationCode().equals(request.code())) {
            throw new RuntimeException("Invalid verification code");
        }

        // Success
        user.setStatus("ACTIVE");
        user.setVerificationCode(null);
        user.setVerificationExpiry(null);
        userRepository.saveAndFlush(user);
    }

	public AuthResponse login(AuthRequest request) {
		UserEntity user = userRepository.findByEmail(request.email())
				.orElseThrow(() -> new RuntimeException("Invalid credentials"));

		if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
			throw new RuntimeException("Invalid credentials");
		}

        // ✅ Check if Verified
        if ("PENDING".equals(user.getStatus())) {
            throw new RuntimeException("Account is not verified. Please check your email.");
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