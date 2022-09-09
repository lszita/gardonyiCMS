package tech.lszita.gardonyi.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import tech.lszita.gardonyi.domain.PageEntity;

/**
 * Spring Data JPA repository for the Page entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PageRepository extends JpaRepository<PageEntity, Long> {}
