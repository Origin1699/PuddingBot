package life.kaori.bot.repository;

import life.kaori.bot.entity.Plugin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 *
 * author: origin
 */
@Repository
public interface PluginRepository extends JpaRepository<Plugin, Integer> {
    Optional<Plugin> getByName(String name);

}
