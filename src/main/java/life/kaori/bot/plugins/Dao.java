package life.kaori.bot.plugins;

import com.mikuac.shiro.annotation.GroupMessageHandler;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import life.kaori.bot.common.CommonUtil;
import life.kaori.bot.config.PluginConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.regex.Matcher;

/**
 * author: origin
 */
@Component
@Shiro
public class Dao {

    private final String name = this.getClass().getSimpleName();
    private PluginConfig pluginConfig;

    @Autowired
    public void setPluginConfig(PluginConfig pluginConfig) {
        this.pluginConfig = pluginConfig;
    }

    @GroupMessageHandler(cmd = "^几点了")
    public void daoGA(Bot bot, GroupMessageEvent event, Matcher matcher) {

        System.out.println(CommonUtil.jarDir);
    }
}
