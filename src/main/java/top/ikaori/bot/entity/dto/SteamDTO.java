package top.ikaori.bot.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class SteamDTO {

    private Response response;

    @Data
    public static class Response {
        private List<players> players;

        @Data
        public static class players {

            private String steamid;
            private int communityvisibilitystate;
            private int profilestate;
            private String personaname;
            private int commentpermission;
            private String profileurl;
            private String avatar;
            private String avatarmedium;
            private String avatarfull;
            private String avatarhash;
            private int lastlogoff;
            private int personastate;
            private String realname;
            private String primaryclanid;
            private int timecreated;
            private int personastateflags;
            private String gameextrainfo;
            private int gameid;
            private String loccountrycode;

        }
    }
}
