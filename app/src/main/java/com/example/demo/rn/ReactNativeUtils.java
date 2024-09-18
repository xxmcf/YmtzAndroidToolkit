package com.example.demo.rn;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.example.demo.utils.SavePathUtil;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactInstanceManagerBuilder;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.common.build.ReactBuildConfig;
import com.facebook.react.shell.MainReactPackage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ReactNativeUtils {

    public static final String TAG = ReactNativeUtils.class.getSimpleName();

    public synchronized static ReactInstanceManager prepareRN(Context context, String code, boolean isDebug) {
        if (context == null) return null;
        String JS_BUNDLE_LOCAL_PATH;

        ReactInstanceManagerBuilder builder = ReactInstanceManager.builder()
                .setApplication(((Activity)context).getApplication())
                .setJSMainModulePath("index.android")
                .addPackage(new MainReactPackage()) //一定要添加，不然新版本react native会报"RCTView" was not found in the UIManager
                .addPackage(new MyModulePackage())
                .addPackage(new VolumePackage())

                //.setNativeModuleCallExceptionHandler(new AutoerNativeModuleCallExceptionHandler(context.getApplicationContext()))
                .setUseDeveloperSupport(ReactBuildConfig.DEBUG)
                .setInitialLifecycleState(LifecycleState.BEFORE_CREATE);
        if (!isDebug) {
            Log.i(TAG, " -----prepareRN----isDebug=false");

        } else {
            JS_BUNDLE_LOCAL_PATH = SavePathUtil.getCacheDirectory(context.getApplicationContext(), "test").getPath() + File.separator + "index.android.bundle";
            Log.i(TAG, " -----prepareRN---- JS_BUNDLE_LOCAL_PATH2="+JS_BUNDLE_LOCAL_PATH);
            //builder.setUseDeveloperSupport(true);
            File file = new File(JS_BUNDLE_LOCAL_PATH);
            if (file != null || file.exists()) {
                Log.i(TAG, " -----prepareRN----file exist");
                String externalPath = context.getApplicationContext().getFilesDir().getAbsolutePath() + "/index.android.bundle";
                Log.i(TAG, " -----prepareRN----externalPath=" + externalPath);
                readToWriteFile(JS_BUNDLE_LOCAL_PATH, externalPath);
                builder.setJSBundleFile(JS_BUNDLE_LOCAL_PATH);
            } else {
                Log.i(TAG, " -----prepareRN----file not exist");
                builder.setBundleAssetName("index.android.bundle");
            }
        }
        ReactInstanceManager mReactInstanceManager = builder.build();

        if (!isDebug) {

        }
        return mReactInstanceManager;
    }

    protected static void readToWriteFile(String filePath1, String filePath2) {
        try {
            FileReader fileReader = new FileReader(filePath1);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            FileWriter fileWriter = new FileWriter(filePath2);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                //Log.i(TAG, " -----prepareRN----readToWriteFile line=" + line);
                bufferedWriter.write(line);
            }
            bufferedReader.close();
            fileReader.close();
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            Log.i(TAG, " -----prepareRN----readToWriteFile IOException=" + e.toString());
        }

    }

}
