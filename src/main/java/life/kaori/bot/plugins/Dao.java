package life.kaori.bot.plugins;

import com.mikuac.shiro.annotation.GroupMessageHandler;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import life.kaori.bot.common.CommonUtil;
import life.kaori.bot.common.util.ImageUtils;
import life.kaori.bot.common.util.MessageUtil;
import life.kaori.bot.config.PluginConfig;
import life.kaori.bot.core.OperationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;
import java.util.regex.Matcher;

/**
 * author: origin
 */
@Component
@Shiro
public class Dao {

    private  final String name = this.getClass().getSimpleName();
    private PluginConfig pluginConfig;

    private File resource = CommonUtil.getPluginResourceDir("dao");

    @Autowired
    public void setPluginConfig(PluginConfig pluginConfig) {
        this.pluginConfig = pluginConfig;
    }

    @GroupMessageHandler(cmd = "^几点了")
    public void daoGA(Bot bot, GroupMessageEvent event) {
        OperationUtil.exec(bot, event, name, () -> {
            File tempFile = File.createTempFile(UUID.randomUUID().toString(), ".jpg");
            ImageUtils.pressText(CommonUtil.getCurrentDate(), CommonUtil.getRandomFile(resource), tempFile, null, 0, Color.BLACK, 55, 0, -210, 1);
            MessageUtil.sendGroupImg(bot, event, tempFile);
        });
    }
}
