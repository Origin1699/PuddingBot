package life.kaori.bot.plugins.common;

import com.mikuac.shiro.annotation.AnyMessageHandler;
import com.mikuac.shiro.annotation.GroupMessageHandler;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.annotation.PrivateMessageHandler;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import life.kaori.bot.common.util.AuthUtil;
import life.kaori.bot.config.BotConfig;
import life.kaori.bot.repository.GroupPluginRepository;
import life.kaori.bot.repository.PluginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;

/**
 * @author admin
 */
@Component
@Shiro
public class PluginManager {

    private String name = this.getClass().getSimpleName();
    private BotConfig botConfig;

    private AuthUtil authUtil;

    private GroupPluginRepository groupPluginRepository;
    private PluginRepository pluginRepository;


    public PluginManager(BotConfig botConfig) {
        this.botConfig = botConfig;
    }

    @GroupMessageHandler
    @MessageHandlerFilter(cmd = "^(开启|关闭)\\s(.*)?$")
    public void EnablePlugin(Bot bot, GroupMessageEvent event, Matcher matcher) {
        Long groupId = event.getGroupId();
        if (!authUtil.groupAuth(event)) {
            bot.sendGroupMsg(groupId, "您不是群主或管理员，因此没有操作权限。", false);
            return;
        }
        String pluginName = matcher.group(1);

        if ("开启".equals(matcher.group(0))) {

        } else if ("关闭".equals(matcher.group(0))) {

        }

    }

    @PrivateMessageHandler
    @MessageHandlerFilter(cmd = "^(开启|关闭)\\s(.*)?$")
    public void EnablePlugin(Bot bot, PrivateMessageEvent event, Matcher matcher) {
        System.out.println(event.getUserId());
        System.out.println("111");
    }

    @PrivateMessageHandler
    @MessageHandlerFilter(cmd = "^[帮助|help]\\s?(.*)?$")
    public void getHelp(Bot bot, PrivateMessageEvent event, Matcher matcher) {
//        Long groupId = event.getGroupId();
//        if (groupId == null || groupId == 0) {
//
//        }
        System.out.println(matcher.group());
        System.out.println(matcher.group(1));
        System.out.println(event.getMessage());
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
    public void setGroupPluginRepository(PluginRepository pluginRepository) {
        this.pluginRepository = pluginRepository;
    }

}
