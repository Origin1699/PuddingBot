package top.ikaori.bot.repository;

import top.ikaori.bot.entity.PluginEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * @author origin
 */
@Repository
public interface PluginRepository extends JpaRepository<PluginEntity, Integer> {
    PluginEntity findByName(String name);

}
