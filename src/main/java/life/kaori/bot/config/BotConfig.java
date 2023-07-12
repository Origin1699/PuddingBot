package life.kaori.bot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * author: origin
 */
@Component
@ConfigurationProperties(prefix = "bot-config")
@Data
public class BotConfig {
    private String nickName;
    private String master;
    private String prefix;
}
