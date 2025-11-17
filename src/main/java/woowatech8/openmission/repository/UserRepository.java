package woowatech8.openmission.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import woowatech8.openmission.entity.SiteUser;

import java.util.Optional;

public interface UserRepository extends JpaRepository<SiteUser, Long> {
    Optional<SiteUser> findByusername(String username);
}
