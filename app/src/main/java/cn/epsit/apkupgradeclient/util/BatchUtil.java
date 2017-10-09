package cn.epsit.apkupgradeclient.util;

/**
 * Created by Administrator on 2017/9/27.
 */

public class BatchUtil {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    /**
     * 将增量文件合成为新的Apk
     * @param oldApkPath 当前Apk路径
     * @param newApkPath 合成后的Apk保存路径
     * @param patchPath 增量文件路径
     */
    public static native void batch(String oldApkPath, String newApkPath , String patchPath );

    /**
     * 生成差分包
     * @param oldApkPath  旧版本apk
     * @param newApkPath  新一点的apk
     * @param patchPath 生成的差分包路径
     */
    //public static native void diff(String oldApkPath, String newApkPath , String patchPath );
}
