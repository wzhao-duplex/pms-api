package ca.on.pms.security;

import ca.on.pms.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class UserPrincipal implements UserDetails {

	private final UUID userId;
	private final String email;
	private final String passwordHash;
	private final UUID orgId; // Crucial for Multi-tenancy
	private final Collection<? extends GrantedAuthority> authorities;

	public static UserPrincipal create(UserEntity user) {
		List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
		return new UserPrincipal(user.getUserId(), user.getEmail(), user.getPasswordHash(), user.getOrgId(),
				authorities);
	}

	@Override
	public String getPassword() {
		return passwordHash;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}