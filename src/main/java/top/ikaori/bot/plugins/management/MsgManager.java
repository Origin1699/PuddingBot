package top.ikaori.bot.plugins.management;

import com.mikuac.shiro.annotation.AnyMessageHandler;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.enums.MsgTypeEnum;
import com.mikuac.shiro.model.ArrayMsg;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import top.ikaori.bot.plugins.Plugin;
import top.ikaori.bot.plugins.picSearch.PicSearch;

import java.io.IOException;
import java.util.List;

/**
 * @author origin
 */
@Shiro
@Component
@AllArgsConstructor
public class MsgManager {

    private ChatModeUtil chatModeUtil;

    @AnyMessageHandler
    public void handle(Bot bot, AnyMessageEvent event) throws IOException {
        List<ArrayMsg> list = event.getArrayMsg().stream().filter(msg -> msg.getType() == MsgTypeEnum.image).toList();
        if (!list.isEmpty()) {
            var chatMode = chatModeUtil.isChatMode(event.getUserId(), event.getGroupId());
            if (chatMode != null) {
                Plugin plugin = PluginManager.getPlugin(chatMode.getMode());
                if (plugin instanceof PicSearch picSearch) {
                    picSearch.chat(bot, event, list);
                }
            }
        }
    }
}
