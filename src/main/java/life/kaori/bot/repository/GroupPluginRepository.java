package life.kaori.bot.repository;

import life.kaori.bot.entity.GroupPluginEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 *
 * author: origin
 */
@Repository
public interface GroupPluginRepository extends JpaRepository<GroupPluginEntity, Integer> {
    Optional<GroupPluginEntity> findByGroupIdAndPluginName(Long groupId, String pluginName);

    void deleteByPluginName(String pluginName);

    List<GroupPluginEntity> findByPluginName(String pluginName);
}
