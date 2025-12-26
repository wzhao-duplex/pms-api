package ca.on.pms.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final JwtUtils jwtUtils;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		String authHeader = request.getHeader("Authorization");

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			if (jwtUtils.validateToken(token)) {
				var claims = jwtUtils.getClaims(token);

				String email = claims.getSubject();
				String userIdStr = claims.get("userId", String.class);
				String orgIdStr = claims.get("orgId", String.class);
				String role = claims.get("role", String.class);

				// Reconstruct Principal from Token (avoid DB hit on every request)
				UserPrincipal principal = new UserPrincipal(UUID.fromString(userIdStr), email, "", // Password not
																									// needed here
						orgIdStr != null ? UUID.fromString(orgIdStr) : null, List.of(new SimpleGrantedAuthority(role)));

				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null,
						principal.getAuthorities());

				SecurityContextHolder.getContext().setAuthentication(auth);
			}
		}
		chain.doFilter(request, response);
	}
}