package ca.on.pms.security;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import ca.on.pms.auth.repository.TokenBlacklistRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final JwtUtils jwtUtils;
	private final TokenBlacklistRepository blacklistRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		String authHeader = request.getHeader("Authorization");

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);

			if (blacklistRepository.existsByToken(token)) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}

			try {
				if (jwtUtils.validateToken(token)) {
					var claims = jwtUtils.getClaims(token);

					String email = claims.getSubject();
					String userIdStr = claims.get("userId", String.class);
					String orgIdStr = claims.get("orgId", String.class);
					String role = claims.get("role", String.class);

					// Reconstruct Principal from Token (avoid DB hit on every request)
					UserPrincipal principal = new UserPrincipal(UUID.fromString(userIdStr), email, "", // Password not
																										// needed here
							orgIdStr != null ? UUID.fromString(orgIdStr) : null,
							List.of(new SimpleGrantedAuthority(role)));

					UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null,
							principal.getAuthorities());

					SecurityContextHolder.getContext().setAuthentication(auth);
				}
			} catch (Exception e) {
				SecurityContextHolder.clearContext();
			}
		}
		chain.doFilter(request, response);
	}
}