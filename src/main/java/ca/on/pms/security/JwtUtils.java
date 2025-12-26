package ca.on.pms.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtils {

	@Value("${jwt.secret:MySuperSecretKeyForDevelopmentMustBeChangedInProd1234567890}")
	private String jwtSecret;

	@Value("${jwt.expiration:86400000}") // 24 hours
	private long jwtExpirationMs;

	private SecretKey key;

	@PostConstruct
	public void init() {
		this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
	}

	public String generateToken(UserPrincipal userPrincipal) {
		return Jwts.builder().subject(userPrincipal.getEmail()).claim("userId", userPrincipal.getUserId().toString())
				.claim("orgId", userPrincipal.getOrgId() != null ? userPrincipal.getOrgId().toString() : null)
				.claim("role", userPrincipal.getAuthorities().iterator().next().getAuthority()).issuedAt(new Date())
				.expiration(new Date((new Date()).getTime() + jwtExpirationMs)).signWith(key).compact();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public Claims getClaims(String token) {
		return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
	}
}