package cn.androidstudy.course08;

import android.os.Environment;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
