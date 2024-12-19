package top.ikaori.bot.common.util;

import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.MessageEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import top.ikaori.bot.common.constant.Constant;
import top.ikaori.bot.config.BotConfig;
import top.ikaori.bot.core.exception.ExceptionMsg;

/**
 * @author origin
 */
@Component
@RequiredArgsConstructor
public class AuthUtil {

    private final BotConfig.Base base;

    public boolean groupMasterAuth(GroupMessageEvent event) {
        String role = event.getSender().getRole();
        return (Constant.ROLE_ADMIN.equals(role) || Constant.ROLE_OWNER.equals(role));
    }

    public void masterCheck(MessageEvent messageEvent) {
        if (!base.getMaster().contains(messageEvent.getUserId())) throw ExceptionMsg.AUTH_ERROR;
    }

    public boolean masterAuth(MessageEvent messageEvent) {
        return base.getMaster().contains(messageEvent.getUserId());
    }

}
