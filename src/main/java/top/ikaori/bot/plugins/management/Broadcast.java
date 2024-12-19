package top.ikaori.bot.plugins.management;

import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.annotation.PrivateMessageHandler;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.ikaori.bot.common.util.AuthUtil;
import top.ikaori.bot.config.Global;
import top.ikaori.bot.core.ExecutorUtil;

import java.util.regex.Matcher;

/**
 * @author origin
 */
@Shiro
@Component
@Slf4j
@RequiredArgsConstructor
public class Broadcast {

    private final Global global;
    private final AuthUtil authUtil;

    @PrivateMessageHandler
    @MessageHandlerFilter(cmd = "^广播\\s+([\\s\\S]+)$")
    public void handler(Bot bot, PrivateMessageEvent event, Matcher matcher) {
        authUtil.masterCheck(event);
        String msg = matcher.group(1);
        ExecutorUtil.exec(bot, event, this.getClass().getSimpleName(), () -> {
            global.bot().getGroupList().getData().forEach(group -> {
                System.out.println(group.getGroupId());
                //bot.sendGroupMsg(group.getGroupId(), msg, false);
            });
        });
    }
}
