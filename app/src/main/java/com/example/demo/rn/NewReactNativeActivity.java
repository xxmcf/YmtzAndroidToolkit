package com.example.demo.rn;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.demo.BaseActivity;
import com.example.demo.R;
import com.example.demo.utils.StateBarUtils;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.soloader.SoLoader;

public class NewReactNativeActivity extends BaseActivity implements DefaultHardwareBackBtnHandler {

    public static String TAG = NewReactNativeActivity.class.getSimpleName();
    public ReactRootView mReactRootView;
    public ReactInstanceManager mReactInstanceManager;

    private RelativeLayout mainRelativeLayout;
    public FrameLayout frameLayout;
    public NewRnPrepareFragment newRnPrepareFragment;

    private String url;
    private String code;
    public Bundle bundle;

    @Override
    protected void initLayout() {
        setContentView(R.layout.activity_react_native);
    }

    @Override
    protected void initView() {
        mReactRootView = (ReactRootView) findViewById(R.id.react_root);
        mainRelativeLayout = (RelativeLayout) findViewById(R.id.mainLayout);
        frameLayout = (FrameLayout) findViewById(R.id.framelayout);
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {
        url = getIntent().getStringExtra("url");
        code = getIntent().getStringExtra("code");
        bundle = new Bundle();
        //"needVpn":"0","needPin":"0","tools":"","rnInitParams":"","useWebKit":"0","customTitle":"","supportedOrientations":"26"
        bundle.putString("needVpn", "0");
        bundle.putString("tools", "0");
        bundle.putString("rnInitParams", "");
        bundle.putString("useWebKit", "0");
        bundle.putString("customTitle", "");
        bundle.putString("supportedOrientations", "26");
        Log.i(TAG, "---initData---url=" + url);
        Log.i(TAG, "---initData---code=" + code);

        newRnPrepareFragment = new NewRnPrepareFragment(this,null, url,  code);
        newRnPrepareFragment.setPrepareRN(new NewRnPrepareFragment.PrepareRN() {
            @Override
            public void hideFragment(Boolean flag) {
                if (flag) {
                    frameLayout.setVisibility(View.GONE);
                } else {
                    frameLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void prepareReactNative(ReactInstanceManager mReactInstanceManager) {
                NewReactNativeActivity.this.prepareReactNative(mReactInstanceManager);
            }
        });
        initFragment();
    }

    public void initFragment() {
        Log.i(TAG, "---initFragment---- ");
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.framelayout, newRnPrepareFragment, "RnPrepare");
        ft.commit();
    }

    public void prepareReactNative(ReactInstanceManager mReactInstanceManager) {
        StateBarUtils.setYtfStatusBar(this, Color.parseColor("#ffffff"), true);
        this.mReactInstanceManager = mReactInstanceManager;
        Log.i(TAG, "------prepareReactNative---- code=" + code);
        if (mReactRootView != null) {
            Log.i(TAG, " -------prepareReactNative---- startReactApplication code=" + code);
            mReactRootView.startReactApplication(mReactInstanceManager, code, bundle);
        }

        mReactInstanceManager.onHostResume(this, this);
    }

    @Override
    public void invokeDefaultOnBackPressed() {
        super.onBackPressed();
    }
}
