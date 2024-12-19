package top.ikaori.bot.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * @author origin
 */

@Data
public class Aria2DTO {

    private String id;
    private String jsonrpc;
    private List<ResultBean> result;

    @Data
    public static class ResultBean {

        private BittorrentBean bittorrent;
        private String completedLength;
        private String connections;
        private String dir;
        private String downloadSpeed;
        private String gid;
        private String infoHash;
        private String numSeeders;
        private String seeder;
        private String status;
        private String totalLength;
        private String uploadSpeed;
        private List<FilesBean> files;

        @Data
        public static class BittorrentBean {

            private InfoBean info;
            private String mode;
            private List<List<String>> announceList;

            @Data
            public static class InfoBean {

                private String name;

            }
        }

        @Data
        public static class FilesBean {

            private String completedLength;
            private String index;
            private String length;
            private String path;
            private String selected;
            private List<?> uris;

        }
    }
}
