package top.ikaori.bot.config;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author origin
 */
@Component
@Lazy
public class Global {
    @Autowired
    private BotContainer container;
    @Autowired
    private BotConfig.Base base;

    private Bot bot;

    public Bot bot() {
        if (bot == null) {
            bot = container.robots.get(base.getBotId());
        }
        return bot;
    }
}
