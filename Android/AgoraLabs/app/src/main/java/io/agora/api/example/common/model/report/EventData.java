package io.agora.api.example.common.model.report;

import java.util.List;

public class EventData {

    private String src;
    private long ts;
    private String sign;



    private List<PTS> pts;

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public List<PTS> getPts() {
        return pts;
    }

    public void setPts(List<PTS> pts) {
        this.pts = pts;
    }

    public static class PTS {
        private String m;
        /**
         * name : entryScene
         * project : KTV
         * version : 4.0.0
         * platform : IOS
         * model : iPhone 11
         */

        private LS ls;
        /**
         * count : 1
         */

        private VS vs;

        public String getM() {
            return m;
        }

        public void setM(String m) {
            this.m = m;
        }

        public LS getLs() {
            return ls;
        }

        public void setLs(LS ls) {
            this.ls = ls;
        }

        public VS getVs() {
            return vs;
        }

        public void setVs(VS vs) {
            this.vs = vs;
        }

        public static class LS {
            private String name;
            private String project;
            private String version;
            private String platform;
            private String model;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getProject() {
                return project;
            }

            public void setProject(String project) {
                this.project = project;
            }

            public String getVersion() {
                return version;
            }

            public void setVersion(String version) {
                this.version = version;
            }

            public String getPlatform() {
                return platform;
            }

            public void setPlatform(String platform) {
                this.platform = platform;
            }

            public String getModel() {
                return model;
            }

            public void setModel(String model) {
                this.model = model;
            }
        }

        public static class VS {
            private int count;

            public int getCount() {
                return count;
            }

            public void setCount(int count) {
                this.count = count;
            }
        }
    }
}
