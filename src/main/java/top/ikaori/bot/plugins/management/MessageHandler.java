package top.ikaori.bot.plugins.management;

import com.mikuac.shiro.annotation.AnyMessageHandler;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.enums.MsgTypeEnum;
import com.mikuac.shiro.model.ArrayMsg;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import top.ikaori.bot.common.constant.Constant;
import top.ikaori.bot.entity.MessageRecordEntity;
import top.ikaori.bot.plugins.Plugin;
import top.ikaori.bot.plugins.picSearch.PicSearch;
import top.ikaori.bot.repository.MessageRecordRepository;

import java.io.IOException;
import java.util.List;

/**
 * @author origin
 */
@Shiro
@Component
@AllArgsConstructor
public class MessageHandler {

    private ChatModeUtil chatModeUtil;

    private final MessageRecordRepository repository;

    @AnyMessageHandler
    public void handler(Bot bot, AnyMessageEvent event) throws IOException {
        switch (event.getMessageType()) {
            case Constant.MESSAGE_TYPE_GROUP:
                repository.save(new MessageRecordEntity(event.getUserId(), event.getGroupId(), event.getMessage()));
                break;
            case Constant.MESSAGE_TYPE_PRIVATE:
                repository.save(new MessageRecordEntity(event.getUserId(), event.getMessage()));
                break;
            default:
                break;
        }

        ChatModeUtil.ChatMode chatMode1 = chatModeUtil.isChatMode(event.getUserId(), event.getGroupId());

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
