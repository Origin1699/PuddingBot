package top.ikaori.bot.repository;

import top.ikaori.bot.entity.GroupPluginEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @author origin
 */
@Repository
public interface GroupPluginRepository extends JpaRepository<GroupPluginEntity, Integer> {
    GroupPluginEntity findByGroupIdAndPluginName(Long groupId, String pluginName);

    void deleteByPluginName(String pluginName);

    List<GroupPluginEntity> findByPluginName(String pluginName);
}
