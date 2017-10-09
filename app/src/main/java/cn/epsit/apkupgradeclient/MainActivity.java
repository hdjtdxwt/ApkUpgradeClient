package cn.epsit.apkupgradeclient;

import android.Manifest;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.database.Observable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;

import java.io.File;

import cn.epsit.apkupgradeclient.bean.CheckResponse;
import cn.epsit.apkupgradeclient.bean.IpResponse;
import cn.epsit.apkupgradeclient.net.ApiEngine;
import cn.epsit.apkupgradeclient.net.ApiPublishService;
import cn.epsit.apkupgradeclient.net.ApiService;
import cn.epsit.apkupgradeclient.util.VersionUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {
    TextView tv;
    Button checkBtn,upgradeBtn;
    String TAG = "main";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.sample_text);

        checkBtn = (Button) findViewById(R.id.check);

        upgradeBtn = (Button) findViewById(R.id.upgrade);

        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG,"onCkick------");
                if(android.os.Build.VERSION.SDK_INT >= 21){ //高版本
                    Log.e(TAG,"onCkick------0");
                    if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED){
                        Log.e(TAG,"onCkick------1");
                        checkAndDownload();
                    }else{ //提示是否给权限
                        Log.e(TAG,"onCkick------2");
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, 1);
                    }
                }else{ //低版本不需要权限申请
                    Log.e(TAG,"onCkick------3");
                    checkAndDownload();
                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1 && grantResults[0]==PermissionChecker.PERMISSION_GRANTED){
            checkAndDownload();
        }else{
            Toast.makeText(this, "权限申请失败！", Toast.LENGTH_SHORT).show();
        }
    }

    public void checkAndDownload(){
        Log.e(TAG,"onCkick------4----------checkAndDownload");
        //网络请求是否有新版本要下载
        int versionCode = VersionUtil.getAppVersionCode(getApplicationContext());
        Log.e(TAG,"versionCode="+versionCode+"  thread="+Thread.currentThread().getName());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiPublishService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Log.e(TAG,"retrofit==null  ? "+(retrofit==null));

        ApiPublishService apiService = retrofit.create(ApiPublishService.class);
        Log.e(TAG,"apiService==null  ? "+(apiService==null));

        rx.Observable<CheckResponse>observable = apiService.checkUpgrade(versionCode);
        Log.e(TAG,"observable==null ?? "+(observable==null));

        observable.subscribeOn(Schedulers.io())
                .subscribe(new Action1<CheckResponse>() {
                    @Override
                    public void call(CheckResponse checkResponse) {
                        Log.e(TAG,"----------call");
                        if(checkResponse!=null && checkResponse.getState()>=0){
                            String data = checkResponse.getData().toString();
                            Log.e(TAG, data);
                        }
                    }
                });

        /*ApiEngine apiEngine = ApiEngine.getInstance();
        Log.e(TAG,"apiEngine==null ?? "+(apiEngine==null));

        ApiPublishService impl = apiEngine.getApiService();
        Log.e(TAG,"impl==null  ? "+(impl==null));

        rx.Observable<CheckResponse>observable = impl.checkUpgrade(versionCode);
        Log.e(TAG,"observable==null ?? "+(observable==null));

        observable.subscribeOn(Schedulers.io())
                .subscribe(new Action1<CheckResponse>() {
            @Override
            public void call(CheckResponse checkResponse) {
                Log.e(TAG,"----------call");
                if(checkResponse!=null && checkResponse.getState()>=0){
                    String data = checkResponse.getData().toString();
                    Log.e(TAG, data);
                }
            }
        });*/

        /*Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.124:8080/ApkUpgradeServer/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        //apiService.getIpInfo("63.223.108.42")
        apiService.checkUpgrade(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CheckResponse>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG,"onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG,"onError=>"+e.getMessage());
                    }

                    @Override
                    public void onNext(CheckResponse ipResponse) {
                        Log.e(TAG,"onNext--------=======");
                    }
                });*/
    }

}
