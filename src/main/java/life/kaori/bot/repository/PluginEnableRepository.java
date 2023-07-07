package life.kaori.bot.repository;

import life.kaori.bot.entity.PluginEnable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 *
 * author: origin
 */
@Repository
public interface PluginEnableRepository extends JpaRepository<PluginEnable, Integer> {
    Optional<PluginEnable> findByGroupIdAndPluginId(Integer groupId, Integer pluginId);
}
