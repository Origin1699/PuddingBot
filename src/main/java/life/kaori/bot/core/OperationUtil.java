package life.kaori.bot.core;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.MessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import life.kaori.bot.core.exception.BotException;
import lombok.extern.slf4j.Slf4j;

/**
 * author: origin
 */
@Slf4j
public class OperationUtil {

    public static void exec(Bot bot, MessageEvent event, String name, Executor executor) {
        try {
            executor.executor();

        } catch (BotException botException) {
            sendMsg(bot, event, botException.getMessage());
        } catch (Exception e) {

        }
    }

    private static void sendMsg(Bot bot, MessageEvent event, String message) {

    }
}
