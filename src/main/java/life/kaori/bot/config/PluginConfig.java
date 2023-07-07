package life.kaori.bot.config;

import life.kaori.bot.entity.Plugin;
import life.kaori.bot.repository.PluginRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * author: origin
 */
@Component
@Lazy(value = false)
public class PluginConfig {

    private PluginRepository pluginRepository;

    public PluginConfig(PluginRepository pluginRepository) {
        this.pluginRepository = pluginRepository;
    }

    @PostConstruct
    public void init() {
        List<Plugin> plugins = pluginRepository.findAll();

    }




}
