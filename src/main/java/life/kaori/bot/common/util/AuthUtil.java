package life.kaori.bot.common.util;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.MessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import org.springframework.stereotype.Component;
import life.kaori.bot.common.constant.Constant;

import java.util.ArrayList;

/**
 *
 * author: origin
 */
@Component
public class AuthUtil {

    public static boolean groupAuth(GroupMessageEvent event){
        String role = event.getSender().getRole();
        return  (Constant.ROLE_ADMIN.equals(role) || Constant.ROLE_OWNER.equals(role));
    }


    public static boolean isMaster(MessageEvent messageEvent) {

        if (messageEvent instanceof GroupMessageEvent) {
            GroupMessageEvent event = (GroupMessageEvent) messageEvent;
            String role = event.getSender().getRole();
            ArrayList<Object> objects = new ArrayList<>();
            objects.clear();

        } else if (messageEvent instanceof PrivateMessageEvent) {
            PrivateMessageEvent event = (PrivateMessageEvent) messageEvent;
        } else {
            AnyMessageEvent event = (AnyMessageEvent) messageEvent;
        }
        return false;
    }

    private static boolean isAdministrator(Bot bot) {
        return false;
    }


}
