package life.kaori.bot.plugins.common;

import com.mikuac.shiro.annotation.GroupMessageHandler;
import com.mikuac.shiro.annotation.PrivateMessageHandler;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import life.kaori.bot.common.util.AuthUtil;
import life.kaori.bot.config.BotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;

/**
 * @author admin
 */
@Component
@Shiro
public class PluginManager {

    private final BotConfig botConfig;

    public PluginManager(BotConfig botConfig) {
        this.botConfig = botConfig;
    }

    @GroupMessageHandler(cmd = "^(开启|关闭)\\s(.*)?$")
    public void EnablePluginGA(Bot bot, GroupMessageEvent event, Matcher matcher) {
        Long groupId = event.getGroupId();
        if (!AuthUtil.groupAuth(event)) {
            bot.sendGroupMsg(groupId, "您不是群主或管理员，因此没有操作权限。", true);
            return;
        }
        String pluginName = matcher.group(1);

        bot.sendGroupMsg(groupId, botConfig.getNickName(), false);

        if ("开启".equals(matcher.group(0))) {

        } else if ("关闭".equals(matcher.group(0))) {

        } else {

        }
    }

    @PrivateMessageHandler(cmd = "^(开启|关闭)\\s(.*)?$")
    public void EnablePluginPA(Bot bot, PrivateMessageEvent event, Matcher matcher) {
        System.out.println(event.getUserId());
        System.out.println("111");
    }

}
