package life.kaori.bot.entity.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * author: origin
 */
@lombok.Data
public class BiliMiniAppDTO {
    private int code;
    private Data data;

    @lombok.Data
    public static class Data {
        private String title;
        private Owner owner;
        private Stat stat;
        private String pic;
        private String bvid;
    }

    @lombok.Data
    public static class Owner {
        private String name;
    }

    @lombok.Data
    public static class Stat {
        private int view;
        private int danmaku;
    }
}


