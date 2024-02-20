package top.ikaori.bot.plugins.management;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.springframework.stereotype.Component;
import top.ikaori.bot.common.util.MessageUtil;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * @author origin
 */
@Component
public class ChatModeUtil {

    @Data
    @AllArgsConstructor
    public static class ChatMode {
        private Bot bot;
        private Long userId;
        private Long groupId;
        private String mode;
        private String expiringMsg;
    }


    private ExpiringMap<String, ChatMode> expiringMap;


    @PostConstruct
    private void init() {
        expiringMap = ExpiringMap.builder().
                variableExpiration().
                expirationPolicy(ExpirationPolicy.CREATED).
                asyncExpirationListener((k, v) -> onExpiration((ChatMode) v)).
                build();
    }

    public void onExpiration(ChatMode value) {
        MessageUtil.at(value.getBot(),
                value.getGroupId(),
                value.getUserId(),
                value.getExpiringMsg());
    }

    public ChatMode isChatMode(Long userId, Long groupId) {
        String key = userId + "" + groupId;
        return expiringMap.get(key);
    }

    public void setChatMode(Bot bot, AnyMessageEvent event, Long timeout, String mode, String expiringMsg) {
        Long userId = event.getUserId();
        Long groupId = event.getGroupId();
        String key = userId + "" + groupId;
        ChatMode chatMode = new ChatMode(bot, userId, groupId, mode, expiringMsg);
        expiringMap.put(key, chatMode, timeout, TimeUnit.SECONDS);
    }

}
