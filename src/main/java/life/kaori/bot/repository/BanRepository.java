package life.kaori.bot.repository;

import life.kaori.bot.entity.Ban;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 *
 * author: origin
 */
@Repository
public interface BanRepository extends JpaRepository<Ban, Long> {
    Optional<Ban> findByUserId(long userId);
}
