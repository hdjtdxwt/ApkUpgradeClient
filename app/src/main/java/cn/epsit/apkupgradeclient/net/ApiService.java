package cn.epsit.apkupgradeclient.net;
import java.io.File;

import cn.epsit.apkupgradeclient.bean.CheckResponse;
import cn.epsit.apkupgradeclient.bean.IpResponse;
import okhttp3.Callback;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Thinkpad on 2016/5/10.
 */
public interface ApiService {
    //String BASE_URL ="http://ip.taobao.com/";
    String BASE_URL = "http://192.168.1.134:8080/ApkUpgradeServer/";

    @GET("service/getIpInfo.php")
    Observable<IpResponse> getIpInfo(@Query("ip")String ip);
    //2.0就一种模式，Call<返回值类型>或者Call<Void>  1.x 在1.x版本中，如果你想定义一个异步的API请求而非同步请求，最后一个参数必须是Callback类型//而2.x创建service 的方法也变得和OkHttp的模式一模一样,返回值就一个Call<XXX>。如果要调用同步请求，只需调用execute；而发起一个异步请求则是调用enqueue。

    //这个试过了，ok,测试可以看到返回的信息
    /*@GET("CheckUpgradeServlet")
    Call<CheckResponse> checkUpgrade(@Query("versionCode") int versionCode);*/

    @GET("CheckUpgradeServlet")
    Observable<CheckResponse> checkUpgrade(@Query("versionCode") int versionCode);


    @GET("service/getIpInfo.php")
    Call<IpResponse> getIpInfoWithCallback(@Query("ip")String ip);

    //下载差分包
    @GET("DownloadServlet")
    Call<ResponseBody> downloadFile(@Query("fileName") String fileName);
}