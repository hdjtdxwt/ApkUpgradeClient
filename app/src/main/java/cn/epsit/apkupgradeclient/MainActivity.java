package cn.epsit.apkupgradeclient;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
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


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import cn.epsit.apkupgradeclient.bean.CheckResponse;
import cn.epsit.apkupgradeclient.net.ApiService;
import cn.epsit.apkupgradeclient.util.BatchUtil;
import cn.epsit.apkupgradeclient.util.VersionUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {
    TextView tv;
    Button checkBtn,upgradeBtn;
    String TAG = "main";
    String downloadFile ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.sample_text);

        checkBtn = (Button) findViewById(R.id.check);

        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG,"onCkick------");
                if(android.os.Build.VERSION.SDK_INT >= 21){ //高版本
                    Log.e(TAG,"onCkick------0");
                    if(ContextCompat.checkSelfPermission(MainActivity.this,  Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED){
                        if(ContextCompat.checkSelfPermission(MainActivity.this,  Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ){
                            Log.e(TAG,"onCkick------1---有些sd卡的权限--和上网的权限");
                            checkAndDownload();
                        }else{
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                        }
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
        if(requestCode==2 && grantResults[0]==PermissionChecker.PERMISSION_GRANTED){
            checkAndDownload();
        }else{
            Toast.makeText(this, "权限申请失败！", Toast.LENGTH_SHORT).show();
        }
    }

    public void checkAndDownload(){
        Log.e(TAG,"onCkick------4----------checkAndDownload");
        //网络请求是否有新版本要下载
        int versionCode = VersionUtil.getAppVersionCode(getApplicationContext());
        final String cVersionName = VersionUtil.getAppVersionName(getApplicationContext());
        Log.e(TAG,"versionCode="+versionCode+"  thread="+Thread.currentThread().getName());

        //发现个问题，本地的服务器，为何baseUrl必须以/结尾，然后请求的地址头部不能以/开头，改成以/结尾不会错
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        final ApiService apiService = retrofit.create(ApiService.class);
        Log.e(TAG,"------------apiService------");

        /*apiService.checkUpgrade(versionCode) //请求是否有更新的apk差分包
                .enqueue(new Callback<CheckResponse>() {

                               @Override
                               public void onResponse(Call<CheckResponse> call, Response<CheckResponse> response) {
                                   CheckResponse checkResponse = response.body();
                                   Log.e(TAG, "--->" + checkResponse.toString());
                                   if (checkResponse.getState() >= 0) { //有更新
                                       String newVersionName = checkResponse.getData().getVersion();
                                       StringBuffer stringBuffer = new StringBuffer();
                                       stringBuffer.append("diff_v");
                                       stringBuffer.append(cVersionName).append("_v").append(newVersionName).append(".patch");
                                       downloadFile = stringBuffer.toString();
                                       Log.e(TAG, "downloadFileName=====>" + stringBuffer); //得到了需要下载的名字
                                   }
                               }

                               @Override
                               public void onFailure(Call<CheckResponse> call, Throwable t) {
                                   Log.e(TAG, "onFailure----------->" + t.getMessage());
                                   t.printStackTrace();
                               }//enqueue 是发起一个异步请求，有回调方法传入     execute发起一个同步请求，等着结果回来
                           });*/
        apiService.checkUpgrade(1) //请求是否有更新的apk差分包
                .map(new Func1<CheckResponse, Call<ResponseBody>>() { //将CheckResponse转换成Observable<String>
                    @Override
                    public Call<ResponseBody> call(CheckResponse response) {
                        Log.e(TAG,"--->"+response.toString());
                        if(response.getState()>=0){ //有更新
                            String newVersionName = response.getData().getVersion();
                            StringBuffer stringBuffer = new StringBuffer();
                            stringBuffer.append("diff_v");
                            stringBuffer.append(cVersionName).append("_v").append(newVersionName).append(".patch");
                            downloadFile = stringBuffer.toString();
                            Log.e(TAG,"downloadFileName=====>"+stringBuffer); //得到了需要下载的名字
                            return apiService.downloadFile(stringBuffer.toString());
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Action1<Call<ResponseBody>>() {
                               @Override
                               public void call(Call<ResponseBody> responseBodyCall) {
                                   responseBodyCall.enqueue(new Callback<ResponseBody>() {
                                       @Override
                                       public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                                           Log.e(TAG, response.message() + "  length  " + response.body().contentLength() + "  type " + response.body().contentType());
                                           File file = new File(Environment.getExternalStorageDirectory()+"/batch/", downloadFile);
                                           Log.e(TAG,"saveFile===>"+file);
                                           try {
                                               file.getParentFile().mkdirs();
                                               file.createNewFile();
                                           } catch (IOException e) {
                                               e.printStackTrace();
                                           }
                                           InputStream is = response.body().byteStream();
                                           FileOutputStream os = null;
                                           try {
                                               os = new FileOutputStream(file);
                                               int len;
                                               byte[] buff = new byte[1024 * 4];
                                               while ((len = is.read(buff)) != -1) {
                                                   os.write(buff, 0, len);
                                               }
                                               Log.e(TAG,"-----下载差分文件成功，可以合并了");
                                               //下载成功就可以合并了
                                               batchFile(file);
                                           } catch (Exception e) {
                                               Log.e(TAG,e.getMessage());
                                               e.printStackTrace();
                                           } finally {
                                               if (os != null) {
                                                   try {
                                                       os.close();
                                                   } catch (IOException e) {
                                                       e.printStackTrace();
                                                   }
                                               }
                                               if (is != null) {
                                                   try {
                                                       is.close();
                                                   } catch (IOException e) {
                                                       e.printStackTrace();
                                                   }
                                               }
                                           }
                                       }

                                       @Override
                                       public void onFailure(Call<ResponseBody> call, Throwable t) {
                                           Log.e(TAG, "downloadFail---->" + t.getMessage());
                                       }
                                   });
                               }
                           });
       //请求ip地址的会报错，解析提示有错
       /* Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://ip.taobao.com/")
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        apiService.getIpInfo("140.205.220.96")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<IpResponse>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG,"onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG,"onError=>"+e.getMessage());
                    }

                    @Override
                    public void onNext(IpResponse ipResponse) {
                        Log.e(TAG,"onNext--------======="+ipResponse);
                    }
                });*/
        //提示解析报错
        /*Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://ip.taobao.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        Call<IpResponse> call = apiService.getIpInfoWithCallback("63.223.108.42");
        call.enqueue(new Callback<IpResponse>() {//enqueue 是发起一个异步请求，有回调方法传入     execute发起一个同步请求，等着结果回来
            @Override
            public void onResponse(Call<IpResponse> call, Response<IpResponse> response) {
                Log.e(TAG,"==========onResponse->"+response);
                Log.e(TAG, response.body().toString());//从response.body()获取返回值

            }

            @Override
            public void onFailure(Call<IpResponse> call, Throwable t) {
                Log.e(TAG,"---------onFailure-->"+t.getMessage());
                t.printStackTrace();
            }
        });*/
    }

    //
    public void batchFile(File file){
        //合并文件生成新的apk
        PackageManager pm = getPackageManager();
        String currentPackageName = getApplicationContext().getPackageName();
        List<PackageInfo> pakageinfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        boolean flag = false;
        for (PackageInfo pi : pakageinfos) {
            String pi_packageName = pi.packageName;
            if(currentPackageName.equals(pi_packageName)){
                //已经安装过了
                flag = true;
                break;
            }
        }
        if(flag){ //已经安装了
            String[]names = file.getName().split("_");
            String oldFileName ="base.apk";
            String targetApkName = "new.apk";
            if(names!=null && names.length>=2 ){
                int index = names[2].lastIndexOf(".");
                String currentVersion = names[2].substring(1,index);
                System.out.println(currentVersion);
                Log.e(TAG,"targetVersion====>"+currentVersion);
                targetApkName = "app-release-"+currentVersion+".apk";
                Log.e(TAG,"targetApkName==>"+targetApkName);
                String oldVersion =  names[1].substring(1);
                if(!TextUtils.isEmpty(oldVersion)){
                    oldFileName = "app-release-"+oldVersion+".apk";
                    Log.e(TAG,"oldFileName==>"+oldFileName);
                }
            }
            String path = getApplicationContext().getPackageResourcePath();
            Log.e(TAG,"----一开始安装的apk的路径："+path);
            File oldFile = new File(Environment.getExternalStorageDirectory()+"/batch/"+oldFileName);

            if(oldFile.exists()){
                path = oldFile.getAbsolutePath();
            }
            if(!TextUtils.isEmpty(path)){
                Log.e(TAG,"-----有这么一个apk文件存在："+path);
                String newFilePath = Environment.getExternalStorageDirectory()+"/batch/"+targetApkName;
                Log.e(TAG,"newFilePath=>"+newFilePath);
                Log.e(TAG,"差分文件："+file.getAbsolutePath()+"    file.exists()=>"+file.exists()+"   size:"+file.getTotalSpace());
                BatchUtil.batch(path,newFilePath,file.getAbsolutePath());
                File newFile = new File(newFilePath);
                Log.e(TAG,"------------合并完成了");
                if(newFile.exists()){
                    Log.e(TAG,"------------合并成功后安装");
                    installApk(newFile);
                }else{
                    Log.e(TAG,"-----合并差分包失败了，没有生成那么一个新的apk");
                }
            }else{
                Log.e(TAG,"-----apk文件删除了："+path);
            }
        }
    }
    /**
     * 安装APK
     */
    private void installApk(File file) {
        Log.e(TAG,"目标文件："+file.getAbsolutePath());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);
    }

}
