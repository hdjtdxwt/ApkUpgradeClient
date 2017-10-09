package cn.epsit.apkupgradeclient.bean;

/**
 * Created by Administrator on 2017/10/9.
 */

public class CheckResponse {

    /**
     * state : -1
     * message : error paramters
     * data : {}
     */
    public CheckResponse(){

    }
    private int state;
    private String message;
    private DataBean data;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        public int versionCode;
        //客户端apk版本号  比如：5.2.10
        public String version;
        //客户端apk上传后保存在本地的路径
        public String apkPath;

        public DataBean(){

        }

        public int getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getApkPath() {
            return apkPath;
        }

        public void setApkPath(String apkPath) {
            this.apkPath = apkPath;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "versionCode=" + versionCode +
                    ", version='" + version + '\'' +
                    ", apkPath='" + apkPath + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "CheckResponse{" +
                "state=" + state +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
