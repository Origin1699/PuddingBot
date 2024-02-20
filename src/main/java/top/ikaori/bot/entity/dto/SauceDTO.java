package top.ikaori.bot.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @author origin
 */
@Data
public class SauceDTO {

    private Header header;
    private List<Results> results;


    @Data
    public static class Header {

        @JsonProperty("long_remaining")
        private int longRemaining;

        @JsonProperty("short_remaining")
        private int shortRemaining;

    }


    @Data
    public static class Results {

        private Header header;

        private Data data;


        @lombok.Data
        public static class Header {

            private String similarity;
            private String thumbnail;
            @JsonProperty("index_id")
            private int indexId;
            @JsonProperty("index_name")
            private String indexName;
            private int dupes;
            private int hidden;
        }

        @lombok.Data
        public static class Data {
            private String source;
            @JsonProperty("anidb_aid")
            private int anidbAid;
            @JsonProperty("mal_id")
            private int malId;
            @JsonProperty("anilist_id")
            private int anilisId;
            private String part;
            private String year;
            @JsonProperty("est_time")
            private String estTime;
            @JsonProperty("ext_urls")
            private List<String> extUrls;
            private String title;
            @JsonProperty("pixiv_id")
            private String pixivId;
            @JsonProperty("member_name")
            private String memberName;
            @JsonProperty("member_id")
            private String memberId;

            @JsonProperty("eng_name")
            private String engName;
            @JsonProperty("jp_name")
            private String jpName;
            @JsonProperty("tweet_id")
            private String tweetId;
            @JsonProperty("twitter_user_id")
            private String twitterUserId;
            @JsonProperty("twitter_user_handle")
            private String twitterUserHandle;
        }
    }
}
