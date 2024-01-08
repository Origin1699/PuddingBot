package top.ikaori.bot.common.util;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.MessageEvent;
import top.ikaori.bot.config.BotConfig;
import top.ikaori.bot.core.exception.ExceptionMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.ikaori.bot.common.constant.Constant;

/**
 * @author origin
 */
@Component
public class AuthUtil {
    private BotConfig config;

    public boolean groupMasterAuth(GroupMessageEvent event) {
        String role = event.getSender().getRole();
        return (Constant.ROLE_ADMIN.equals(role) || Constant.ROLE_OWNER.equals(role));
    }


    public void masterCheck(MessageEvent messageEvent) {
        if (!config.getMaster().contains(messageEvent.getUserId())) throw ExceptionMsg.AUTH_ERROR;
    }

    private static boolean isAdministrator(Bot bot) {
        return false;
    }

    @Autowired
    public void setConfig(BotConfig config) {
        this.config = config;
    }
}
