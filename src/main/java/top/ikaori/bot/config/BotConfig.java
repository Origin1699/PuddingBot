package top.ikaori.bot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author origin
 */
@Component
@ConfigurationProperties(prefix = "bot-config")
@Data
public class BotConfig {
    private String nickName;
    private String botId;
    private List<Long> master;
    private String prefix;
    private BotProxy proxy;
    private ChatGPT chatGPT;
    private Tarot tarot;
    private Aria2 aria2;
    private PicSearch picSearch;

    @Data
    public static class BotProxy {
        private String host;
        private int port;
        private String type;
    }

    @Data
    public static class ChatGPT {
        private String token;
        private int timeout;
        private String module;

    }

    @Data
    public static class Tarot {
        private Long cd;

    }

    @Data
    public static class Aria2 {
        private String token;
    }

    @Data
    public static class PicSearch {
        private Long timeout;
        private boolean proxy;
        private String token;
        private double similarity;
        private boolean alwaysUseAscii2d;
        private boolean animePreviewVideo;
    }

}
