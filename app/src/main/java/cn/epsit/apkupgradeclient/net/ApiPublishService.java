package cn.epsit.apkupgradeclient.net;


import cn.epsit.apkupgradeclient.bean.CheckResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * 发布环境  网络请求接口类
 * Created by Nicholas on 2016/10/30.
 * API：http://robot.epsit.cn:7078
 语音：http://robot.epsit.cn:7074

 */

public interface ApiPublishService {

    String BASE_URL = "http://192.168.1.124:8080/ApkUpgradeServer";//对外发布版本的baseUrl


    //参数userId是用户的唯一表示，可以用uuid，也可以用一个特殊规律的时间戳
    //上传用户头像
    /*
        @POST("FileUploadAndDown/servlet/uploadUserImage")
        @Multipart
        Observable<FileUploadResponse> uploadUserImage(@Part MultipartBody.Part file, @Part("userId") RequestBody userId, @Part("appid") RequestBody appid, @Part("accesstoken") RequestBody accesstoken);
    */

    //获取广播播报内容
    @GET("http://192.168.1.124:8080/ApkUpgradeServer/CheckUpgradeServlet")
    Observable<CheckResponse> checkUpgrade(@Query("versionCode") int versionCode);

    //baseUrl和这个post的路径拼接的换要正好可以访问到，也就是要自己注意 斜杆 的问题 ，是在baseUrl最后加/还是在post的路径最开头加/自己决定，反正都可以，要能正确访问到路径就是
}
