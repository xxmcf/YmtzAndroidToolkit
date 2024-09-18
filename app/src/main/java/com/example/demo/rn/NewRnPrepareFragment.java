package com.example.demo.rn;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.demo.BaseActivity;
import com.example.demo.R;
import com.example.demo.utils.SavePathUtil;
import com.facebook.react.ReactInstanceManager;
import com.wang.avi.AVLoadingIndicatorView;

public class NewRnPrepareFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = NewRnPrepareFragment.class.getSimpleName();

    public String url, code, androidBundleURL;
    private Bundle bundle;
    public Context context;
    public TextView infotext;
    public TextView titlebar;
    private AVLoadingIndicatorView gif;
    private ImageView back;
    private boolean loadfailed;
    private boolean hideTitle; //是否显示标题栏

    private ImageView nodata;
    public TextView nodata_hint;
    private LinearLayout refresh;
    private RelativeLayout titleLayout;

    public NewRnPrepareFragment() {

    }

    public NewRnPrepareFragment(Context context, Bundle bundle, String url, String code) {
        this.context = context;
        this.bundle = bundle;
        this.url = url;
        this.code = code;
        this.androidBundleURL = !url.endsWith("/") ? url + "/" : url;
        this.androidBundleURL += "index.android.bundle?platform=android&dev=true&minify=false";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG,"onCreateView------");
        View view = inflater.inflate(R.layout.activity_new_rn_prepare, container, false);
        initView(view);
        initData();

        return view;
    }

    protected void initView(View view) {
        titlebar = (TextView) view.findViewById(R.id.title_bar);
        infotext = (TextView) view.findViewById(R.id.infotext);
        gif = (AVLoadingIndicatorView) view.findViewById(R.id.gif1);
        gif.show();
        back = (ImageView) view.findViewById(R.id.backicon);
//        progressBar = (ProgressBar) view.findViewById(R.id.content_view_progress);
        refresh = (LinearLayout) view.findViewById(R.id.refresh);
        nodata = (ImageView) view.findViewById(R.id.loadfailed);
        nodata_hint = (TextView) view.findViewById(R.id.loadfailed_hint);
        titleLayout = (RelativeLayout) view.findViewById(R.id.titleLayout);
        back.setOnClickListener(this);
        refresh.setOnClickListener(this);
        if(hideTitle){
            titleLayout.setVisibility(View.GONE);
        }

    }

    protected void initData() {
        checkRN(url, code, (BaseActivity) context, handler);

    }

    @Override
    public void onClick(View view) {

    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1: {
                    nodata.setVisibility(View.GONE);
                    nodata_hint.setVisibility(View.GONE);
                    refresh.setVisibility(View.GONE);
                    gif.setVisibility(View.VISIBLE);
                    prepareRnView(false);
                    handler.sendEmptyMessageDelayed(3, 100);
                } break;
                case 2:
                {
                    prepareRnView(true);
                    handler.sendEmptyMessage(3);
                } break;
                case 3:
                {
                    if(prepareRN != null){
                        prepareRN.hideFragment(true);
                    }
                } break;
                case 4:
                {
                    gif.setVisibility(View.GONE);
                    titleLayout.setVisibility(View.VISIBLE);
                    nodata.setVisibility(View.VISIBLE);
                    nodata_hint.setVisibility(View.VISIBLE);
                    refresh.setVisibility(View.VISIBLE);
                    if(msg.obj!=null)infotext.setText(msg.obj.toString() + " ");
                    loadfailed = true;
                } break;
                case 9:
                {
                    infotext.setVisibility(View.VISIBLE);
                    infotext.setText(msg.arg1 + "%");
                } break;
            }
        }
    };

    public void checkRN(String url, final String type, BaseActivity context, final Handler handler) {
        final BaseActivity bcontext;
        if (context != null) {
            bcontext = context;
        } else {
            bcontext = (BaseActivity) getActivity();
        }
        if (bcontext == null) {
            return;
        }
        String savePath = SavePathUtil.getCacheDirectory(bcontext, "test").getPath();
        Log.i(TAG, "-----checkRN------savePath="+savePath);
        Log.i(TAG, "-----checkRN------androidBundleURL=" + this.androidBundleURL);
        NewInitRN downRn = new NewInitRN(bcontext, savePath, type, true, handler);
        downRn.downloadBundle(this.androidBundleURL, savePath, "index.android.bundle");
    }

    public interface PrepareRN{
        void hideFragment(Boolean flag);
        void prepareReactNative(ReactInstanceManager mReactInstanceManager);
    }

    public PrepareRN prepareRN;

    public void setPrepareRN(PrepareRN prepareRN) {
        this.prepareRN = prepareRN;
    }

    public void prepareRnView(Boolean isDebug) {
        Log.i(TAG, "--------prepareRnView----isDebug=" + isDebug);
        if (prepareRN != null) {
            ReactInstanceManager mReactInstanceManager = ReactNativeUtils.prepareRN(getActivity(), code, true);
            if (mReactInstanceManager != null) {
                prepareRN.prepareReactNative(mReactInstanceManager);
            } else {
                handler.sendEmptyMessage(4);
            }
        }
    }
}
