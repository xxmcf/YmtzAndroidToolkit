package com.example.demo.rn;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.DownloadProgressCallBack;
import com.zhouyou.http.exception.ApiException;

import java.io.File;

public class NewInitRN {

    private static final String TAG = NewInitRN.class.getSimpleName();

    public static String mSavePath;
    private Context mContext;
    public String type;
    public Boolean isDebug;
    public Bundle bundle;
    public Handler handler;

    public NewInitRN() {

    }

    public NewInitRN(Activity context, String savePath, String type, Boolean isDebug, Handler handler) {
        this.mContext = context;
        this.mSavePath = savePath;
        this.type = type;
        this.isDebug = isDebug;
        this.handler = handler;
    }

    public void downloadBundle(String url, String path, String fileName) {
        Log.i(TAG, "-------downloadBundle---url=" + url);
        Log.i(TAG, "-------downloadBundle---path=" + path);
        Log.i(TAG, "-------downloadBundle---fileName=" + fileName);
        if(TextUtils.isEmpty(url)){
            Message message = new Message();
            message.what = 4;
            message.obj = "url为空";
            handler.sendMessage(message);
            return;
        }
        EasyHttp.downLoad(url)
                .retryCount(3) //本次请求重试次数
                .retryDelay(1000) //本次请求重试延迟时间
                .savePath(path)
                .saveName(fileName) //不设置默认名字是时间戳生成的
                .execute(new DownloadProgressCallBack<String>() {
                    @Override
                    public void update(long bytesRead, long contentLength, boolean done) {
                        Log.i(TAG, "----downloadBundle-----update bytesRead="+bytesRead);
                        Log.i(TAG, "----downloadBundle-----update contentLength="+contentLength);
                        if (contentLength != -1) {
                            int progress = Math.round((float) bytesRead / contentLength * 100);
                            Message message = new Message();
                            message.arg1 = (int)progress;
                            message.what = 9;
                            handler.sendMessage(message);
                        }
                    }

                    @Override
                    public void onComplete(String downPath) {
                        Log.i(TAG, "--- downloadBundle----onComplete---downPath="+downPath);
                        handler.sendEmptyMessage(2); //debug
                    }

                    @Override
                    public void onStart() {
                        Log.i(TAG, "----downloadBundle-----start");
                        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                            File filePath = new File(path);

                            if (!filePath.exists()) {
                                Log.i(TAG, "----downloadBundle-----start----filePath mkdir=" + path);
                                filePath.mkdirs();
                            }
                            File file = new File(filePath, fileName);
                            if (file.exists()) {
                                Log.i(TAG, "----downloadBundle-----start----fileName existed=" + fileName);
                                file.delete();
                            }
                        }
                    }

                    @Override
                    public void onError(ApiException e) {
                        Message message = new Message();
                        message.what = 1;
                        message.obj = type;
                        handler.sendMessage(message);
                        //下载失败
                        Log.i(TAG, "---- downloadBundle-----onError---ApiException e="+e.toString());
                    }
                });
    }

    public void preDownloadBundle(String url, String path, String fileName) {
        Log.i(TAG, "---- preDownLoadBundle----");
        try {
            if (TextUtils.isEmpty(url)) {
                Message message = new Message();
                message.what = 4;
                message.obj = "url为空";
                handler.sendMessage(message);
                return;
            }
            EasyHttp.downLoad(url)
                    .retryCount(3)//本次请求重试次数
                    .retryDelay(1000)//本次请求重试延迟时间500ms
                    .savePath(path)
                    .saveName(fileName)//不设置默认名字是时间戳生成的
                    .execute(new DownloadProgressCallBack<String>() {
                        @Override
                        public void update(long bytesRead, long contentLength, boolean done) {

                        }

                        @Override
                        public void onStart() {
                            Log.i(TAG, "----preDownLoadBundle-----start");
                            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                                File filePath = new File(path);

                                if (!filePath.exists()) {
                                    Log.i(TAG, "----preDownLoadBundle-----start----filePath mkdir=" + path);
                                    filePath.mkdirs();
                                }
                                File file = new File(filePath, fileName);
                                if (file.exists()) {
                                    Log.i(TAG, "----preDownLoadBundle-----start----fileName existed=" + fileName);
                                    file.delete();
                                }
                            }
                        }

                        @Override
                        public void onComplete(String downPath) {
                            Log.i(TAG, "--- preDownLoadBundle----onComplete---downPath="+downPath);
                        }

                        @Override
                        public void onError(ApiException e) {
                            //下载失败
                            Log.i(TAG, "---- preDownLoadBundle-----onError ApiException e=" + e.toString());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
