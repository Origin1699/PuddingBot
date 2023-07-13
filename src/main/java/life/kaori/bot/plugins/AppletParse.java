package life.kaori.bot.plugins;

import com.mikuac.shiro.annotation.GroupMessageHandler;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;

/**
 * author: origin
 */
@Component
@Shiro
public class AppletParse {
    @GroupMessageHandler(cmd = "^(.*?)1109937557(.*)")
    public void biliAppletParseGA(Bot bot, GroupMessageEvent event, Matcher matcher) {
        System.out.println(event.getMessage());
        System.out.println(AppletParse.class);
    }

    @GroupMessageHandler(cmd = ".*com.tencent.miniapp_01.*")
    public void biliAppletParse2GA(Bot bot, GroupMessageEvent event) {
        String message = event.getMessage();

            System.out.println(event.getMessage());
            System.out.println(AppletParse.class);

    }

}
