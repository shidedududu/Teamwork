package com.example.asd.netutils;

import android.os.Handler;
import android.os.Message;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;



/**
 * Created by asd on 2017/11/20.
 */

public class HttpUtil {
    private static String result="";
    private static String urlPath="http://47.93.5.44:8080/test/";

    public static String sendRequestWithHttpURLConnection(final Handler handler, final String path, final String data, final int operation){

        //开启线程来发起网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                try {
                    url = new URL(urlPath+path);
                    //url = new URL("http://www.baidu.com");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                HttpURLConnection httpUrlConnection = null;
                try {
                    httpUrlConnection = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    httpUrlConnection.setRequestMethod("POST"); //设置请求方法
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
                httpUrlConnection.setConnectTimeout(8000); //设置链接超时的时间
//将读超时设置为指定的超时值，以毫秒为单位。用一个非零值指定在建立到资源的连接后从input流读入时的超时时间。
//如果在数据可读取之前超时期满，则会引发一个 java.net.sockettimeoutexception。超时时间为零表示无穷大超时。
                httpUrlConnection.setReadTimeout(8000);
                httpUrlConnection.setDoInput(true); //允许输入流，即允许下载
                httpUrlConnection.setDoOutput(true); //允许输出流，即允许上传,在Android中get方法设置为true会导致异常
                httpUrlConnection.setUseCaches(false); //设置是否使用缓存
                httpUrlConnection.setInstanceFollowRedirects(true);
                //httpUrlConnection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
                httpUrlConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                //httpUrlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                //httpUrlConnection.setRequestProperty("Content-type", "application/x-java-serialized-object");
                //httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
                //httpUrlConnection.setRequestProperty("Charset", "UTF-8");
                // 设置文件类型:
                //httpUrlConnection.setRequestProperty("Content-Type","application/json; charset=UTF-8");

                /*
                Gson gson=new Gson();
                UserEntity user=new UserEntity();
                user.setUserName("huangyz");
                user.setUserPassword("1234567");
                String str=gson.toJson(user);
                System.out.println(str);
                //str="userName=feofj&userPassword=fefkam";
                */



                //建立连接，上面对urlConn的所有配置必须要在connect之前完，这里需要注意的是
                //connect这个方法，在getInputStream()方法中会隐式的被调用，所以这里不写也没有问题
                try {
                    httpUrlConnection.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                PrintWriter writer= null;
                try {
                    writer = new PrintWriter(httpUrlConnection.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                writer.write(data);

                writer.flush();


                writer.close();


                InputStream inputStream = null;
                try {
                    inputStream = httpUrlConnection.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                InputStreamReader in = new InputStreamReader(inputStream);
                BufferedReader bf = new BufferedReader(in);
                StringBuffer sb = new StringBuffer();
                String inputLine = null;


                try {
                    while ((inputLine = bf.readLine()) != null) {
                        sb.append(inputLine);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


                //System.out.println(sb.toString());
                result=sb.toString();
                Message message = new Message();
                message.what = operation;
                message.obj =result;
                handler.sendMessage(message);

                if (httpUrlConnection != null)
                    httpUrlConnection.disconnect();
            }
        }).start();
        return result;
    }
}
