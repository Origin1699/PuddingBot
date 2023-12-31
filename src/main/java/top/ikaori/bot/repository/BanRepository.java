package top.ikaori.bot.repository;

import top.ikaori.bot.entity.BanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 *
 * @author origin
 */
@Repository
public interface BanRepository extends JpaRepository<BanEntity, Long> {
    Optional<BanEntity> findByUserId(long userId);
}
