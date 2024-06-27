package top.ikaori.bot.entity.dto;

import java.util.List;

/**
 * @author origin
 */

public class Aria2DTO {

    private String id;
    private String jsonrpc;
    private List<ResultBean> result;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

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

        public BittorrentBean getBittorrent() {
            return bittorrent;
        }

        public void setBittorrent(BittorrentBean bittorrent) {
            this.bittorrent = bittorrent;
        }

        public String getCompletedLength() {
            return completedLength;
        }

        public void setCompletedLength(String completedLength) {
            this.completedLength = completedLength;
        }

        public String getConnections() {
            return connections;
        }

        public void setConnections(String connections) {
            this.connections = connections;
        }

        public String getDir() {
            return dir;
        }

        public void setDir(String dir) {
            this.dir = dir;
        }

        public String getDownloadSpeed() {
            return downloadSpeed;
        }

        public void setDownloadSpeed(String downloadSpeed) {
            this.downloadSpeed = downloadSpeed;
        }

        public String getGid() {
            return gid;
        }

        public void setGid(String gid) {
            this.gid = gid;
        }

        public String getInfoHash() {
            return infoHash;
        }

        public void setInfoHash(String infoHash) {
            this.infoHash = infoHash;
        }

        public String getNumSeeders() {
            return numSeeders;
        }

        public void setNumSeeders(String numSeeders) {
            this.numSeeders = numSeeders;
        }

        public String getSeeder() {
            return seeder;
        }

        public void setSeeder(String seeder) {
            this.seeder = seeder;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTotalLength() {
            return totalLength;
        }

        public void setTotalLength(String totalLength) {
            this.totalLength = totalLength;
        }

        public String getUploadSpeed() {
            return uploadSpeed;
        }

        public void setUploadSpeed(String uploadSpeed) {
            this.uploadSpeed = uploadSpeed;
        }

        public List<FilesBean> getFiles() {
            return files;
        }

        public void setFiles(List<FilesBean> files) {
            this.files = files;
        }

        public static class BittorrentBean {

            private InfoBean info;
            private String mode;
            private List<List<String>> announceList;

            public InfoBean getInfo() {
                return info;
            }

            public void setInfo(InfoBean info) {
                this.info = info;
            }

            public String getMode() {
                return mode;
            }

            public void setMode(String mode) {
                this.mode = mode;
            }

            public List<List<String>> getAnnounceList() {
                return announceList;
            }

            public void setAnnounceList(List<List<String>> announceList) {
                this.announceList = announceList;
            }

            public static class InfoBean {
                /**
                 * name : [DMG] この素晴らしい世界に爆焔を！ [BDRip][AVC_AAC][1080P][CHS][MP4]
                 */

                private String name;

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }
            }
        }

        public static class FilesBean {

            private String completedLength;
            private String index;
            private String length;
            private String path;
            private String selected;
            private List<?> uris;

            public String getCompletedLength() {
                return completedLength;
            }

            public void setCompletedLength(String completedLength) {
                this.completedLength = completedLength;
            }

            public String getIndex() {
                return index;
            }

            public void setIndex(String index) {
                this.index = index;
            }

            public String getLength() {
                return length;
            }

            public void setLength(String length) {
                this.length = length;
            }

            public String getPath() {
                return path;
            }

            public void setPath(String path) {
                this.path = path;
            }

            public String getSelected() {
                return selected;
            }

            public void setSelected(String selected) {
                this.selected = selected;
            }

            public List<?> getUris() {
                return uris;
            }

            public void setUris(List<?> uris) {
                this.uris = uris;
            }
        }
    }
}
