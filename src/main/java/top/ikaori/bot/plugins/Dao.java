package top.ikaori.bot.plugins;

import com.mikuac.shiro.annotation.GroupMessageHandler;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import lombok.Getter;
import org.springframework.stereotype.Component;
import top.ikaori.bot.common.CommonUtil;
import top.ikaori.bot.common.util.ImageUtils;
import top.ikaori.bot.common.util.MessageUtil;
import top.ikaori.bot.core.ExecutorUtil;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.UUID;

/**
 * @author origin
 */
@Component
@Shiro
public class Dao implements AbstractPlugin {

    @Getter
    private final List<String> nickName = List.of("刀","几点了");
    @Getter
    private final String help = """
            根据当前时间生成图片
            命令: 几点了
            """;
    private final File resource = CommonUtil.getPluginResourceDir(getName());

    @GroupMessageHandler
    @MessageHandlerFilter(cmd = "^几点了")
    public void dao(Bot bot, GroupMessageEvent event) {
        ExecutorUtil.exec(bot, event, getName(), () -> {
            File tempFile = File.createTempFile(UUID.randomUUID().toString(), ".jpg");
            ImageUtils.pressText(CommonUtil.getCurrentDate(), CommonUtil.getRandomFile(resource), tempFile, null, 0, Color.BLACK, 55, 0, -210, 1);
            MessageUtil.sendGroupImg(bot, event, tempFile);
        });
    }
}
