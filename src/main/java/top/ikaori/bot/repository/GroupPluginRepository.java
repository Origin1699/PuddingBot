package top.ikaori.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import top.ikaori.bot.entity.GroupPluginEntity;

import java.util.List;

/**
 *
 * @author origin
 */
@Repository
public interface GroupPluginRepository extends JpaRepository<GroupPluginEntity, Integer> {
    GroupPluginEntity findByGroupIdAndPluginName(Long groupId, String pluginName);

    @Modifying
    @Transactional
    void deleteByPluginName(String pluginName);

    List<GroupPluginEntity> findByPluginName(String pluginName);
}
