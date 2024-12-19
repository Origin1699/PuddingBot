package top.ikaori.bot.plugins.management;

import cn.hutool.extra.spring.SpringUtil;
import com.mikuac.shiro.annotation.AnyMessageHandler;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.constant.ActionParams;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import top.ikaori.bot.common.constant.BotStrings;
import top.ikaori.bot.common.constant.Constant;
import top.ikaori.bot.common.util.AuthUtil;
import top.ikaori.bot.common.util.MessageUtil;
import top.ikaori.bot.core.exception.BotException;
import top.ikaori.bot.entity.GroupPluginEntity;
import top.ikaori.bot.entity.PluginEntity;
import top.ikaori.bot.plugins.AbstractPlugin;
import top.ikaori.bot.repository.GroupPluginRepository;
import top.ikaori.bot.repository.PluginRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * @author admin
 */
@Component
@Shiro
public class PluginManager {

    private final String name = this.getClass().getSimpleName();
    private AuthUtil authUtil;
    private GroupPluginRepository groupPluginRepository;
    private PluginRepository pluginRepository;


    @AnyMessageHandler
    @MessageHandlerFilter(cmd = "^(开启|关闭)\\s+(.*)$")
    public void enablePlugin(Bot bot, AnyMessageEvent event, Matcher matcher) {
        String pluginName = getPluginName(matcher.group(2));
        switch (event.getMessageType()) {
            case ActionParams.PRIVATE -> {
                authUtil.masterCheck(event);
                if ("开启".equals(matcher.group(1))) {
                    pluginEnable(pluginName, Boolean.TRUE);
                } else if ("关闭".equals(matcher.group(1))) {
                    pluginEnable(pluginName, Boolean.FALSE);
                }
            }
            case ActionParams.GROUP -> {
                Long groupId = event.getGroupId();
                if (!authUtil.groupMasterAuth(event)) {
                    MessageUtil.sendMsg(bot, event, "您不是群主或管理员，因此没有操作权限。");
                    return;
                }
                if ("开启".equals(matcher.group(1))) {
                    groupEnable(pluginName, groupId, Boolean.TRUE);

                } else if ("关闭".equals(matcher.group(1))) {
                    groupEnable(pluginName, groupId, Boolean.FALSE);
                }
            }
            default -> {
            }
        }
        MessageUtil.sendMsg(bot, event, String.format("插件: %s 已%s", matcher.group(2), matcher.group(1)));
    }

    @AnyMessageHandler
    @MessageHandlerFilter(cmd = "^插件列表$")
    public void plugins(Bot bot, AnyMessageEvent event) {

        switch (event.getMessageType()) {
            case ActionParams.PRIVATE -> {
            }
            case ActionParams.GROUP -> {
            }
            default -> {
            }
        }

        MsgUtils text = MsgUtils.builder().text("插件列表如下: ");
        getPlugins().forEach(plugin -> text.text(Constant.RN).text(plugin.getNickName().get(0)));
        text.text(Constant.RN).text("发送 帮助 插件名称 可查看帮助信息");
        MessageUtil.sendMsg(bot, event, text.build());
    }

    @AnyMessageHandler
    @MessageHandlerFilter(cmd = "^(帮助|help)\\s+(.*)$")
    public void getHelp(Bot bot, AnyMessageEvent event, Matcher matcher) {
        MessageUtil.sendMsg(bot, event, getPluginHelp(matcher.group(2)));
    }

    private final Map<String, Boolean> pluginMap = new HashMap<>();
    private final Map<String, Map<Long, Boolean>> groupPluginMap = new HashMap<>();


    public static AbstractPlugin getPlugin(String name) {
        Map<String, AbstractPlugin> plugins = SpringUtil.getBeansOfType(AbstractPlugin.class);
        for (AbstractPlugin abstractPlugin : plugins.values()) {
            String pluginName = abstractPlugin.getName();
            if (pluginName.equalsIgnoreCase(name)) {
                return abstractPlugin;
            }
            for (String nickName : abstractPlugin.getNickName()) {
                if (nickName.equalsIgnoreCase(name)) {
                    return abstractPlugin;
                }
            }
        }
        throw BotException.create(BotStrings.PLUGIN_FIND_ERROR, name);
    }

    @EventListener(ContextRefreshedEvent.class)
    public void init() {
        List<PluginEntity> plugins = pluginRepository.findAll();
        plugins.forEach(pluginEntity -> {
            try {
                AbstractPlugin abstractPlugin = getPlugin(pluginEntity.getName());
                pluginMap.put(abstractPlugin.getName(), pluginEntity.isEnable());
            } catch (Exception e) {
                pluginRepository.delete(pluginEntity);
                groupPluginRepository.deleteByPluginName(pluginEntity.getName());
            }
        });

        getPlugins().forEach(plugin -> {
            if (!pluginMap.containsKey(plugin.getName())) {
                pluginMap.put(plugin.getName(), true);
                PluginEntity pluginEntity = new PluginEntity(plugin.getName(), plugin.getNickName().get(0), true);
                pluginRepository.save(pluginEntity);
            }
        });

        pluginMap.keySet().forEach(key -> {
            List<GroupPluginEntity> list = groupPluginRepository.findByPluginName(key);
            Map<Long, Boolean> map = groupPluginMap.computeIfAbsent(key, v -> new HashMap<Long, Boolean>());
            list.forEach(plugin -> {
                map.put(plugin.getGroupId(), plugin.isEnable());
            });
        });
    }

    public static String getPluginName(String name) {
        return getPlugin(name).getName();
    }

    public static String getPluginHelp(String name) {
        return getPlugin(name).getHelp();
    }

    public static List<String> getPluginNickName(String name) {
        return getPlugin(name).getNickName();
    }

    public Collection<AbstractPlugin> getPlugins() {
        return SpringUtil.getBeansOfType(AbstractPlugin.class).values();
    }

    public boolean groupReply(Long groupId, String pluginName) {
        return pluginIsEnable(pluginName) && groupIsEnable(groupId, pluginName);
    }

    public boolean groupIsEnable(Long groupId, String pluginName) {
        Map<Long, Boolean> map = groupPluginMap.computeIfAbsent(pluginName, v -> new HashMap<>());
        return map.compute(groupId, (k, v) -> v == null || v);
    }

    public boolean pluginIsEnable(String pluginName) {
        return pluginMap.compute(pluginName, (k, v) -> {
            if (v == null) {
                return true;
            }
            return v;
        });
    }

    private void groupEnable(String pluginName, Long groupId, Boolean flag) {
        groupPluginMap.compute(pluginName, (k, v) -> {
            if (v == null) {
                v = Map.of(groupId, flag);
            } else {
                v.put(groupId, flag);
            }
            return v;
        });
        GroupPluginEntity entity = groupPluginRepository.findByGroupIdAndPluginName(groupId, pluginName);
        if (entity == null) {
            entity = new GroupPluginEntity();
            AbstractPlugin abstractPlugin = getPlugin(pluginName);
            entity.setPluginName(abstractPlugin.getName());
            entity.setGroupId(groupId);
            entity.setEnable(flag);
        }
        entity.setEnable(flag);
        groupPluginRepository.save(entity);

    }

    private void pluginEnable(String pluginName, Boolean flag) {
        pluginMap.put(pluginName, flag);
        PluginEntity entity = pluginRepository.findByName(pluginName);
        if (entity == null) {
            AbstractPlugin abstractPlugin = getPlugin(pluginName);
            entity = new PluginEntity(abstractPlugin.getName(), abstractPlugin.getNickName().get(0), flag);
        }
        entity.setEnable(flag);
        pluginRepository.save(entity);
    }


    @Autowired
    public void setAuthUtil(AuthUtil authUtil) {
        this.authUtil = authUtil;
    }

    @Autowired
    public void setGroupPluginRepository(GroupPluginRepository groupPluginRepository) {
        this.groupPluginRepository = groupPluginRepository;
    }

    @Autowired
    public void setPluginRepository(PluginRepository pluginRepository) {
        this.pluginRepository = pluginRepository;
    }
}
