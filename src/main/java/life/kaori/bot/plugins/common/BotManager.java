package life.kaori.bot.plugins.common;

import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.annotation.PrivateMessageHandler;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import life.kaori.bot.common.util.AssertUtil;
import life.kaori.bot.common.util.AuthUtil;
import life.kaori.bot.common.util.MessageUtil;
import life.kaori.bot.core.ExecutorUtil;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;

/**
 * author: origin
 */
@Shiro
@Component
public class BotManager {

    private String name = this.getClass().getSimpleName();

    private AuthUtil authUtil;

    public BotManager(AuthUtil authUtil) {
        this.authUtil = authUtil;
    }

    @PrivateMessageHandler
    @MessageHandlerFilter(cmd = "^[退出?|加入?][群|群组]\\s([0-9]*)$")
    public void group(Bot bot, PrivateMessageEvent event, Matcher matcher) {
        ExecutorUtil.exec(bot, event, name, () -> {
            authUtil.masterCheck(event);
            Long groupId = Long.parseLong(matcher.group(1));
            if (event.getMessage().contains("退")) {
                AssertUtil.notEmpty(bot.getGroupList().getData().stream().filter(groupInfoResp -> groupId.equals(groupInfoResp.getGroupId())).toList(), "没有加入该群捏。");
                bot.setGroupLeave(groupId, false);
            } else {
                MessageUtil.sendPrivateMsg(bot, event, "无法主动加群捏！请邀请我加入群组捏。");
            }
        });
    }
}
