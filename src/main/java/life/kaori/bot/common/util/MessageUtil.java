package life.kaori.bot.common.util;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.MessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;

import java.io.File;

/**
 * author: origin
 */
public class MessageUtil {
    public static final String BASE64_PREFIX = "base64://";

    public static void sendGroupImg(Bot bot, GroupMessageEvent event, File file) {
        sendGroupImg(bot, event, file, false);
    }

    public static void sendGroupImg(Bot bot, GroupMessageEvent event, File file, boolean autoEscape) {
        bot.sendGroupMsg(event.getGroupId(), MsgUtils.builder().img(BASE64_PREFIX + FileUtils.toBase64(file)).build(), autoEscape);
    }

    public static void sendGroupMsg(Bot bot, GroupMessageEvent event, String msg) {
        bot.sendGroupMsg(event.getGroupId(), msg, false);
    }

    public static void sendGroupMsg(Bot bot, GroupMessageEvent event, String msg, boolean autoEscape) {
        bot.sendGroupMsg(event.getGroupId(), msg, autoEscape);
    }

    public static void sendPrivateMsg(Bot bot, PrivateMessageEvent event, String msg) {
        bot.sendPrivateMsg(event.getUserId(), msg, false);
    }


    public static void sendPrivateMsg(Bot bot, PrivateMessageEvent event, String msg, boolean autoEscape) {
        bot.sendPrivateMsg(event.getUserId(), msg, autoEscape);
    }

    public static void sendMsg(Bot bot, MessageEvent event, String msg) {
        if (GroupMessageEvent.class.isInstance(event)) {
            sendGroupMsg(bot, (GroupMessageEvent) event, msg);
        } else if (PrivateMessageEvent.class.isInstance(event)) {
            sendPrivateMsg(bot, (PrivateMessageEvent) event, msg);
        }
    }
}
