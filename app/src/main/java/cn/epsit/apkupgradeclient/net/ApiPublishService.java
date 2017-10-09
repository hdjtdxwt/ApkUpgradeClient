package cn.epsit.apkupgradeclient.net;


import cn.epsit.apkupgradeclient.bean.CheckResponse;
import retrofit2.http.Body;
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
    @POST("CheckUpgradeServlet")
    Observable<CheckResponse> checkUpgrade(@Query("versionCode") int versionCode);
}
