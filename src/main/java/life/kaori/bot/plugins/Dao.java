package life.kaori.bot.plugins;

import com.mikuac.shiro.annotation.GroupMessageHandler;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import life.kaori.bot.common.CommonUtil;
import life.kaori.bot.common.util.ImageUtils;
import life.kaori.bot.common.util.MessageUtil;
import life.kaori.bot.core.ExecutorUtil;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.UUID;

/**
 * author: origin
 */
@Component
@Shiro
public class Dao implements Plugin {

    @Getter
    private final String name = this.getClass().getSimpleName();
    @Getter
    private final List<String> nickName = List.of("刀","几点了");
    @Getter
    private final String help = """
            根据当前时间生成图片
            命令: 几点了
            """;
    private File resource = CommonUtil.getPluginResourceDir(name.toLowerCase());

    @GroupMessageHandler
    @MessageHandlerFilter(cmd = "^几点了")
    public void dao(Bot bot, GroupMessageEvent event) {
        ExecutorUtil.exec(bot, event, name, () -> {
            File tempFile = File.createTempFile(UUID.randomUUID().toString(), ".jpg");
            ImageUtils.pressText(CommonUtil.getCurrentDate(), CommonUtil.getRandomFile(resource), tempFile, null, 0, Color.BLACK, 55, 0, -210, 1);
            MessageUtil.sendGroupImg(bot, event, tempFile);
        });
    }
}
