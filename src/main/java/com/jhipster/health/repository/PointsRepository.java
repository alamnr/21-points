package com.jhipster.health.repository;

import com.jhipster.health.domain.Points;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Points entity.
 */
@Repository
public interface PointsRepository extends JpaRepository<Points, Long> {
    @Query("select points from Points points where points.user.login = ?#{principal.username}")
    List<Points> findByUserIsCurrentUser();

    default Optional<Points> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Points> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Points> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct points from Points points left join fetch points.user",
        countQuery = "select count(distinct points) from Points points"
    )
    Page<Points> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct points from Points points left join fetch points.user")
    List<Points> findAllWithToOneRelationships();

    @Query("select points from Points points left join fetch points.user where points.id =:id")
    Optional<Points> findOneWithToOneRelationships(@Param("id") Long id);

    @Query("select points from Points points where points.user.login = ?#{principal.username}")
    Page<Points> findByUserIsCurrentUser(Pageable pageable);

    @Query("select points from Points points where points.user.login = ?#{principal.username} order by date desc")
    Page<Points> findAllByOrderByDateDesc(Pageable pageable);

    List<Points> findAllByDateBetweenAndUserLogin(LocalDate firstDate, LocalDate secondDate, String login);
}
