package life.kaori.bot.plugins;

import com.mikuac.shiro.annotation.GroupMessageHandler;
import com.mikuac.shiro.annotation.PrivateMessageHandler;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import com.mikuac.shiro.enums.AtEnum;
import life.kaori.bot.common.constant.BotStrings;
import life.kaori.bot.config.BotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;


/**
 * author: origin
 */
@Shiro
@Component
public class DemoTest {
    @Autowired
    private BotConfig config;

    // 符合 cmd 正则表达式的消息会被响应
    @PrivateMessageHandler(cmd = "hi")
    public void fun1(Bot bot, PrivateMessageEvent event, Matcher matcher) {
        // 构建消息
//        String sendMsg = MsgUtils.builder().face(66).text("Hello, this is shiro demo.").build();
        // 发送私聊消息
//        bot.sendPrivateMsg(event.getUserId(), sendMsg, false);
//        System.out.println("----------PrivateMessageHandler-----------");
//        System.out.println(event.getMessage());
//        System.out.println("----------PrivateMessageHandler-----------");
    }

    // 如果 at 参数设定为 AtEnum.NEED 则只有 at 了机器人的消息会被响应
    @GroupMessageHandler(at = AtEnum.NEED)
    public void fun2(Bot bot, GroupMessageEvent event, Matcher matcher) {
        // 以注解方式调用可以根据自己的需要来为方法设定参数
        // 例如群组消息可以传递 GroupMessageEvent, Bot, Matcher 多余的参数会被设定为 null
//        System.out.println("----------GroupMessageHandler-----------");
//        System.out.println(event.getMessage());
//        System.out.println("----------GroupMessageHandler-----------");
        System.out.println(config);
    }

    // 同时监听群组及私聊消息 并根据消息类型（私聊，群聊）回复
//    @AnyMessageHandler
    public void fun3(Bot bot, AnyMessageEvent event) {
        String message = event.getMessage();
//        message = message.substring(message.indexOf("data=") + 5, message.lastIndexOf("]"));
//        message = message.replace("&#44;", ",");
//        System.out.println(message);
//        Map map = JSON.parseObject(message, Map.class);
//        RegexUtils.matcher("")
//
//        System.out.println("---------AnyMessageHandler------------");
//        System.out.println(message);
//        System.out.println("---------AnyMessageHandler------------");
    }

    @PrivateMessageHandler(cmd = "hi")
    public void fun4(Bot bot, PrivateMessageEvent event) {
        try {
            throw BotStrings.PLUGIN_OPERATE.exception("xxxxx");
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }


}
