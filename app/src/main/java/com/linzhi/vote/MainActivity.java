package com.linzhi.vote;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "vote";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View btn){
        if (btn.getId()==R.id.btn1){
            new VoteTask().execute("赞成");
        }else if (btn.getId()==R.id.btn2){
            new VoteTask().execute("反对");
        }else{
            new VoteTask().execute("弃权");
        }
    }
    private String doVote(String voteStr){
        String retStr="";
        Log.i("vote","doVote() voteStr:"+voteStr);
        try {
            StringBuffer stringBuffer=new StringBuffer();
            stringBuffer.append("r=").append(URLEncoder.encode(voteStr,"utf-8"));

            byte[] data=stringBuffer.toString().getBytes();
            String urlPath="http://10.64.151.9:8080/vote/GetVote";
            URL url=new URL(urlPath);

            HttpURLConnection httpURLConnection=(HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(3000);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setUseCaches(false);
            //设置请求体的类型是文本类型
            httpURLConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            //设置请求体的长度
            httpURLConnection.setRequestProperty("Content-Length",String.valueOf(data.length));
            //获取输出流，向服务器写入数据
            OutputStream outputStream=httpURLConnection.getOutputStream();
            outputStream.write(data);

            int response=httpURLConnection.getResponseCode();
            if (response==HttpURLConnection.HTTP_OK){
                InputStream inputStream=httpURLConnection.getInputStream();
                retStr=inputStreamToString(inputStream);
                Log.i("vote", "retStr: "+retStr);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return retStr;
    }
    public static String inputStreamToString(InputStream inputStream){
        String resultData=null;//存储处理结果
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        byte[] data=new byte[1024];
        int len=0;
        try {
            while((len=inputStream.read(data))!=-1){
                byteArrayOutputStream.write(data,0,len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultData=new String(byteArrayOutputStream.toByteArray());
        return resultData;
    }
    private class VoteTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... params) {
            for (String p:params){
                Log.i(TAG,"doInBackground:"+p);
            }
            String ret=doVote(params[0]);
            return ret;
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(MainActivity.this,s,Toast.LENGTH_SHORT).show();
        }
    }
}
