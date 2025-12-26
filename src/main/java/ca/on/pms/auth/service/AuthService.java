package ca.on.pms.auth.service;

import ca.on.pms.auth.dto.AuthRequest;
import ca.on.pms.auth.dto.AuthResponse;
import ca.on.pms.auth.dto.RegisterRequest;
import ca.on.pms.organization.entity.OrganizationEntity;
import ca.on.pms.organization.repository.OrganizationRepository;
import ca.on.pms.security.JwtUtils;
import ca.on.pms.security.UserPrincipal;
import ca.on.pms.user.entity.UserEntity;
import ca.on.pms.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final OrganizationRepository orgRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already exists");
        }

        // 1. Create User (Initially no Org ID)
        UserEntity user = UserEntity.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .role("OWNER")
                .status("ACTIVE")
                .build();
        
        UserEntity savedUser = userRepository.save(user);

        // 2. Create Organization (1 Year Free)
        OrganizationEntity org = OrganizationEntity.builder()
                .orgName(request.orgName())
                .ownerUser(savedUser)
                .subscriptionStart(LocalDate.now())
                .subscriptionEnd(LocalDate.now().plusYears(1))
                .status("ACTIVE")
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
}