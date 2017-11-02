package cn.androidstudy.course08;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private boolean isGranted = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        grantedAndRequest();
    }

    //判断是否授权，如未授权，则申请授权
    private void grantedAndRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permission  = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            //和下面语句等效
            //int permission  = checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE");
            //如果已授权
            if (permission == PackageManager.PERMISSION_GRANTED) {
                isGranted = true;
            }else{
                //未授权，弹对话框，申请授权
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }
        }else{
            isGranted = true;
        }
    }
    /**
     * 处理权限请求结果
     *
     * @param requestCode
     *          请求权限时传入的请求码，用于区别是哪一次请求的
     *
     * @param permissions
     *          所请求的所有权限的数组
     *
     * @param grantResults
     *          权限授予结果，和 permissions 数组参数中的权限一一对应，元素值为两种情况，如下:
     *          授予: PackageManager.PERMISSION_GRANTED
     *          拒绝: PackageManager.PERMISSION_DENIED
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                isGranted = true;
            }
        }
    }

    //同步调用，需要使用线程
    public void getTb(View view){
        new Thread(){
            @Override
            public void run() {
                getBaidu();
            }
        }.start();
    }
    private void getBaidu(){
        String url = "http://10.66.23.5:8080";
        //1.定义OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //2.定义Request对象
        Request request = new Request.Builder()
                .url(url)
                .build();
        /*
        如果你需要在request的的header添加参数。例如Cookie，User-Agent什么的，就是
        Request request = new Request.Builder()
            .url(url)
            .header("键", "值")
            .header("键", "值")
            ...
            .build();
         */
        //3.定义Call对象
        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();//同步执行
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //异步调用Get请求
    public void getYb(View view){
        String url = "http://10.66.23.5:8080";
        //1.定义OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //2.定义Request对象
        Request request = new Request.Builder()
                .url(url)
                .build();
        //3.定义Call对象
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {//注意：此处的变化
            @Override
            public void onFailure(Call call, IOException e) {
                //网络访问失败
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //访问成功，输出
                System.out.println(response.body().string());
            }
        });
    }
    //post同步调用
    public void postTb(View view){
        new Thread(){
            @Override
            public void run() {
                postLogin();
            }
        }.start();
    }

    private void postLogin() {
        String url = "http://10.66.23.5:8080/AndroidTest/login.jsp";
        OkHttpClient okHttpClient = new OkHttpClient();
        //post调用需要使用RequestBody将参数传递过去
        RequestBody body = new FormBody.Builder()
                .add("username", "admin")
                .add("password","123456")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)//这是和Get的区别
                .build();

        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //post异步调用
    public void postYb(View view){
        String url = "http://10.66.23.5:8080/AndroidTest/login.jsp";
        OkHttpClient okHttpClient = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("username", "admin")
                .add("password","123456")
                .build();

        final Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println(response.body().string());
            }
        });
    }

    //异步下载文件
    public void downAsynFile(View view) {
        if(!isGranted){
            Toast.makeText(this, "未获得权限，请授权！", Toast.LENGTH_SHORT).show();
            grantedAndRequest();
            return;
        }
        OkHttpClient mOkHttpClient = new OkHttpClient();
        String url = "http://info.zzuli.edu.cn/picture/article/112/c5/d4/c06282924bcaaa3695c930df89b8/25015014-0fcf-4c1c-83d5-3afd7870872b.jpg";
        Request request = new Request.Builder().url(url).build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) {
                InputStream inputStream = response.body().byteStream();
                FileOutputStream fileOutputStream = null;
                try {
                    fileOutputStream = new FileOutputStream(new File(Environment.getExternalStorageDirectory()+"/kexue.jpg"));
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, len);
                    }
                    fileOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("文件下载成功");
            }
        });
    }

}
