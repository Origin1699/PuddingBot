package top.ikaori.bot.common.util;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.MessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author origin
 */
@Slf4j
public class MessageUtil {
    public static final String BASE64_PREFIX = "base64://";

    public static void sendGroupImg(Bot bot, GroupMessageEvent event, File file) {
        sendGroupImg(bot, event, file, false);
    }

    public static void sendGroupImg(Bot bot, GroupMessageEvent event, File file, boolean autoEscape) {
        bot.sendGroupMsg(event.getGroupId(), MsgUtils.builder().img(BASE64_PREFIX + FileUtils.toBase64(file)).build(), autoEscape);
    }

    public static void sendMsg(Bot bot, AnyMessageEvent event, String msg) {
        sendMsg(bot, event, msg, false);
    }

    private static void sendMsg(Bot bot, AnyMessageEvent event, String msg, boolean autoEscape) {
        bot.sendMsg(event, msg, autoEscape);
    }


    public static void sendGroupMsg(Bot bot, GroupMessageEvent event, String msg) {
        bot.sendGroupMsg(event.getGroupId(), msg, false);
    }

    public static void sendGroupMsg(Bot bot, GroupMessageEvent event, String msg, boolean autoEscape) {
        bot.sendGroupMsg(event.getGroupId(), msg, autoEscape);
    }

    public static void sendGroupMsg(Bot bot, GroupMessageEvent event, List<String> messages) {
        sendGroupMsg(bot, event, messages, false);
    }

    public static void sendGroupMsg(Bot bot, GroupMessageEvent event, List<String> messages, boolean autoEscape) {
        List<Map<String, Object>> forwardMsg = ShiroUtils.generateForwardMsg(bot.getSelfId(), "布丁", messages);
        bot.sendGroupForwardMsg(event.getGroupId(), forwardMsg);
    }

    public static void sendPrivateMsg(Bot bot, PrivateMessageEvent event, String msg) {
        sendPrivateMsg(bot, event.getUserId(), msg);
    }

    public static void sendPrivateMsg(Bot bot, Long userId, String msg) {
        bot.sendPrivateMsg(userId, msg, false);
    }


    public static void sendPrivateMsg(Bot bot, PrivateMessageEvent event, String msg, boolean autoEscape) {
        bot.sendPrivateMsg(event.getUserId(), msg, autoEscape);
    }

    public static void sendMsg(Bot bot, MessageEvent event, String msg) {
        if (event instanceof GroupMessageEvent) {
            sendGroupMsg(bot, (GroupMessageEvent) event, msg);
        } else if (event instanceof PrivateMessageEvent) {
            sendPrivateMsg(bot, (PrivateMessageEvent) event, msg);
        } else if (event instanceof AnyMessageEvent) {
            sendAnyMsg(bot, (AnyMessageEvent) event, msg);
        }
    }

    public static void sendAnyMsg(Bot bot, AnyMessageEvent event, String msg) {
        sendAnyMsg(bot, event, msg, false);
    }

    public static void sendAnyMsg(Bot bot, AnyMessageEvent event, String msg, boolean flag) {
        if (event.getGroupId() == null) {
            bot.sendPrivateMsg(event.getUserId(), msg, flag);
        } else {
            bot.sendGroupMsg(event.getGroupId(), msg, flag);
        }
    }

    public static void sendAnyMsg(Bot bot, AnyMessageEvent event, List messages) {
        sendAnyMsg(bot, event, messages, false);
    }

    public static void sendAnyMsg(Bot bot, AnyMessageEvent event, List messages, boolean flag) {
        if (event.getGroupId() == null) {
            List<Map<String, Object>> forwardMsg = ShiroUtils.generateForwardMsg(bot.getSelfId(), "布丁", messages);
            bot.sendPrivateForwardMsg(event.getUserId(), forwardMsg);
        } else {
            List<Map<String, Object>> forwardMsg = ShiroUtils.generateForwardMsg(bot.getSelfId(), "布丁", messages);
            bot.sendGroupForwardMsg(event.getGroupId(), forwardMsg);
        }
    }
}
