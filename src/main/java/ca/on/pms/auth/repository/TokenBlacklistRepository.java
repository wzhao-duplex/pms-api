package ca.on.pms.auth.repository;

import ca.on.pms.auth.entity.TokenBlacklistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklistEntity, UUID> {
	boolean existsByToken(String token);
}