package ca.on.pms.user.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ca.on.pms.user.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
	Optional<UserEntity> findByEmail(String email);

	boolean existsByEmail(String email);
}