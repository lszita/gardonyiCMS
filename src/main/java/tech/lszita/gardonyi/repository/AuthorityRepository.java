package tech.lszita.gardonyi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.lszita.gardonyi.domain.Authority;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {}
