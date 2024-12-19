package top.ikaori.bot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author origin
 */
@ConfigurationProperties(prefix = "bot-config")
@Configuration
@Data
public class BotConfig {
    private Base base;
    private Plugins plugins;

    @Data
    public static class Base {
        private String nickName;
        private Long botId;
        private List<Long> master;
        private String prefix;
        private Proxy proxy;

        @Data
        public static class Proxy {
            private String host;
            private int port;
            private String type;
        }

    }

    @Data
    public static class Plugins {

        private ChatGPTConfig chatGPTConfig;

        private TarotConfig tarotConfig;

        private Aria2Config aria2Config;

        private PicSearchConfig picSearchConfig;

        private SteamConfig steamConfig;

        private WordCloudConfig wordCloudConfig;

        @Data
        public static class ChatGPTConfig {
            private String token;
            private int timeout;
            private String module;

        }

        @Data
        public static class TarotConfig {
            private Long cd;

        }

        @Data
        public static class Aria2Config {
            private String token;
            private String url;
        }

        @Data
        public static class PicSearchConfig {
            private Long timeout;
            private boolean proxy;
            private String token;
            private double similarity;
            private boolean alwaysUseAscii2d;
            private boolean animePreviewVideo;
        }

        @Data
        public static class SteamConfig {
            private String apikey;
        }

        @Data
        public static class WordCloudConfig {
            private int cronTaskRate;
            private int minFontSize;
            private int maxFontSize;
            private List<String> filterRule;
        }
    }
}
