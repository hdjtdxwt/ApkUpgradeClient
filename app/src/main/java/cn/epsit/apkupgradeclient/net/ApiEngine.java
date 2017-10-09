package cn.epsit.apkupgradeclient.net;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.plugins.RxJavaErrorHandler;
import rx.plugins.RxJavaPlugins;

import static android.content.ContentValues.TAG;

/**
 * 网络请求控制类
 * Created by Nicholas on 2016/10/30.
 */

public class ApiEngine {
    private volatile static ApiEngine apiEngine;
    private Retrofit retrofit;
    OkHttpClient client;
    static {
        RxJavaPlugins.getInstance().registerErrorHandler(new RxJavaErrorHandler() {
            @Override
            public void handleError(Throwable e) {
                if(e!=null) {
                    e.printStackTrace();
                }
            }
        });
    }
    private ApiEngine() {

        //日志拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(false) //超时不自动重复请求
                .connectTimeout(25, TimeUnit.SECONDS) //网络连接超时时间
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(25, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .addNetworkInterceptor(new NetWorkInterceptor())
                .build();
        Log.e("main","client==null >> ? "+(client==null));
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Log.e("main","gson==null ? "+(gson==null));
        //构建Retrofit对象
        //然后将刚才设置好的okHttp对象,通过retrofit.client()方法 设置到retrofit中去
        retrofit = new Retrofit.Builder()
                .baseUrl(ApiPublishService.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        Log.e("main","retrofit==null ??? "+(retrofit==null));
    }

    public static ApiEngine getInstance() {
        if (apiEngine == null) {
            synchronized (ApiEngine.class) {
                if (apiEngine == null) {
                    apiEngine = new ApiEngine();
                }
            }
        }
        return apiEngine;
    }

    public ApiPublishService getApiService() {
        Log.e("main","retrofit==null--------->"+(retrofit==null));
        return retrofit.create(ApiPublishService.class);
    }

}
