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
import life.kaori.bot.core.PluginManage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.File;
import java.util.UUID;

/**
 * author: origin
 */
@Component
@Shiro
public class Dao implements PluginManage {

    private final String name = this.getClass().getSimpleName();
    private final String nickName = "刀";
    private final String help = """
            命令: 几点了
            """;
    private PluginConfig pluginConfig;

    private File resource = CommonUtil.getPluginResourceDir(name.toLowerCase());

    @Autowired
    public void setPluginConfig(PluginConfig pluginConfig) {
        this.pluginConfig = pluginConfig;
    }

    @GroupMessageHandler(cmd = "^几点了")
    public void dao(Bot bot, GroupMessageEvent event) {
        OperationUtil.exec(bot, event, name, () -> {
            File tempFile = File.createTempFile(UUID.randomUUID().toString(), ".jpg");
            ImageUtils.pressText(CommonUtil.getCurrentDate(), CommonUtil.getRandomFile(resource), tempFile, null, 0, Color.BLACK, 55, 0, -210, 1);
            MessageUtil.sendGroupImg(bot, event, tempFile);
        });
    }
}
