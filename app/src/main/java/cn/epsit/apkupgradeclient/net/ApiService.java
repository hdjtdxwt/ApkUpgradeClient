package cn.epsit.apkupgradeclient.net;
import cn.epsit.apkupgradeclient.bean.CheckResponse;
import cn.epsit.apkupgradeclient.bean.IpResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Thinkpad on 2016/5/10.
 */
public interface ApiService {
    @GET("service/getIpInfo.php")
    Observable<IpResponse> getIpInfo(@Query("ip")String ip);
    //2.0就一种模式，Call<返回值类型>或者Call<Void>  1.x 在1.x版本中，如果你想定义一个异步的API请求而非同步请求，最后一个参数必须是Callback类型//而2.x创建service 的方法也变得和OkHttp的模式一模一样,返回值就一个Call<XXX>。如果要调用同步请求，只需调用execute；而发起一个异步请求则是调用enqueue。


    @GET("CheckUpgradeServlet")
    Observable<CheckResponse> checkUpgrade(@Query("versionCode") int versionCode);
}