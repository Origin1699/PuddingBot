package life.kaori.bot.config;

import life.kaori.bot.common.PluginEnum;
import life.kaori.bot.entity.GroupPluginEntity;
import life.kaori.bot.entity.PluginEntity;
import life.kaori.bot.repository.GroupPluginRepository;
import life.kaori.bot.repository.PluginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * author: origin
 */
@Component
@Lazy(value = false)
public class PluginConfig {

    private final HashMap<String, Boolean> pluginMap = new HashMap<>();
    private final HashMap<String, HashMap<Long, Boolean>> groupPluginMap = new HashMap<>();

    private PluginRepository pluginRepository;
    private GroupPluginRepository groupPluginRepository;

    @Autowired
    public void setPluginRepository(PluginRepository pluginRepository) {
        this.pluginRepository = pluginRepository;
    }

    @Autowired
    public void setGroupPluginRepository(GroupPluginRepository groupPluginRepository) {
        this.groupPluginRepository = groupPluginRepository;
    }


    @PostConstruct
    public void init() {
        List<PluginEntity> plugins = pluginRepository.findAll();
        plugins.forEach(pluginEntity -> {
            try {
                PluginEnum p = PluginEnum.valueOf(pluginEntity.getName());
                pluginMap.put(p.name, pluginEntity.isEnable());
            } catch (Exception e) {
                pluginRepository.delete(pluginEntity);
                groupPluginRepository.deleteByPluginName(pluginEntity.getName());
            }
        });

        Arrays.stream(PluginEnum.class.getEnumConstants()).forEach(pluginEnum -> {
            if (!pluginMap.containsKey(pluginEnum.name)) {
                pluginMap.put(pluginEnum.name, true);
                PluginEntity pluginEntity = new PluginEntity(pluginEnum.name, pluginEnum.alias, true);
                pluginRepository.save(pluginEntity);
            }
        });

        pluginMap.keySet().forEach(key -> {
            List<GroupPluginEntity> list = groupPluginRepository.findByPluginName(key);
            HashMap<Long, Boolean> map = groupPluginMap.computeIfAbsent(key, v -> new HashMap<Long, Boolean>());
            list.forEach(plugin -> {
                map.put(plugin.getGroupId(), plugin.isEnable());
            });
        });
    }

    public boolean pluginEnableCheck(String pluginName, Long groupId) {
        return isPluginEnable(pluginName) && isGroupPluginEnable(pluginName, groupId);
    }

    public boolean isPluginEnable(String pluginName) {
        return pluginMap.containsKey(pluginName) ? pluginMap.get(pluginName) : true;
    }

    public boolean isGroupPluginEnable(String pluginName, Long groupId) {
        HashMap<Long, Boolean> map = groupPluginMap.computeIfAbsent(pluginName, v -> new HashMap<>());
        return map.compute(groupId, (k, v) -> v == null || v);
    }
}
