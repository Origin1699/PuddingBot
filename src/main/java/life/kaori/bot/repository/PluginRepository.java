package life.kaori.bot.repository;

import life.kaori.bot.entity.PluginEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 *
 * author: origin
 */
@Repository
public interface PluginRepository extends JpaRepository<PluginEntity, Integer> {
    Optional<PluginEntity> getByName(String name);

}
