package life.kaori.bot.core;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.MessageEvent;
import life.kaori.bot.common.util.MessageUtil;
import life.kaori.bot.core.exception.BotException;
import lombok.extern.slf4j.Slf4j;

/**
 * author: origin
 */
@Slf4j
public class ExecutorUtil {

    public static void exec(Bot bot, MessageEvent event, String name, Executor executor) {
        try {
            executor.executor();
        } catch (BotException botException) {
            MessageUtil.sendMsg(bot, event, botException.getMessage());
        } catch (Exception e) {
//            e.printStackTrace();
            MessageUtil.sendMsg(bot, event, "插件 " + name + " 执行失败, 请联系管理员查看后台日志。");
            log.error(e.getMessage(), e);
        }
    }

    private static void sendMsg(Bot bot, MessageEvent event, String message) {

    }
}
