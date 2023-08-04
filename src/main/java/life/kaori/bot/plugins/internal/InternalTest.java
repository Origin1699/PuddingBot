package life.kaori.bot.plugins.internal;

import com.mikuac.shiro.annotation.GroupMessageHandler;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import org.springframework.stereotype.Component;

/**
 * author: origin
 */
@Shiro
@Component
public class InternalTest {

//    @GroupMessageHandler
    public void test(Bot bot, GroupMessageEvent event){
        System.out.println(this.getClass().getName());
    }
}
