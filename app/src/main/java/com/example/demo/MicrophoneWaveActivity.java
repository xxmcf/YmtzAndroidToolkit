package com.example.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.demo.utils.AudioSurfaceView;
import com.example.demo.utils.Constant;
import com.example.demo.utils.MicManager;
import com.example.demo.utils.TitleView;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.XXPermissions;

import java.util.List;
//import com.hjq.permissions.OnPermissionCallback;

public class MicrophoneWaveActivity extends BaseActivity implements View.OnClickListener {
    private String TAG = MicrophoneWaveActivity.class.getSimpleName();
    private TitleView title;
    private ImageView playView;

    private AudioSurfaceView audioView;

    private boolean playStatus = false;

    private static final int micRecordMessage = 0x101;
    MicManager micManager = new MicManager();
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case micRecordMessage:
//                    Log.e(TAG, "micRecordMessaage---size=" + msg.arg1);
                    audioView.addAudioData((byte[])msg.obj,msg.arg1);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void initLayout() {
        setContentView(R.layout.activity_micphone_wave);
    }

    protected void initView() {
        title = (TitleView)findViewById(R.id.title_view);
        title.setTitleText(getString(R.string.demo1_name));
        title.setBackOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity) MicrophoneWaveActivity.this).overridePendingTransition(R.anim.budong, R.anim.push_right_out);
            }
        });
        playView = (ImageView) findViewById(R.id.play);
        audioView = (AudioSurfaceView)findViewById(R.id.audioView);

        playView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!playStatus) {
                    XXPermissions.with(MicrophoneWaveActivity.this)
                            // 申请多个权限
                            .permission(android.Manifest.permission.RECORD_AUDIO)
                            // 设置权限请求拦截器（局部设置）
                            //.interceptor(new PermissionInterceptor())
                            .request(new OnPermission() {
                                @Override
                                public void hasPermission(List<String> granted, boolean isAll) {
                                    if (isAll) {
                                        playStatus = true;
                                        micManager.startRecord();
                                        playView.setImageDrawable(getResources().getDrawable(R.drawable.stop_play));
                                    }

                                }

                                @Override
                                public void noPermission(List<String> denied, boolean quick) {
                                    if (quick) {
                                        Toast.makeText(MicrophoneWaveActivity.this, getResources().getString(R.string.str_deny_audio_permissions), Toast.LENGTH_SHORT).show();
                                        // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                        XXPermissions.gotoPermissionSettings(MicrophoneWaveActivity.this);
                                    } else {
                                        Toast.makeText(MicrophoneWaveActivity.this, getResources().getString(R.string.str_manual_open_audio_permission), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    playStatus = false;
                    micManager.stopRecord();
                    playView.setImageDrawable(getResources().getDrawable(R.drawable.start_play));
                }
            }
        });


    }

    @Override
    protected void initListener() {

    }

    protected void initData() {
        micManager.setHandler(handler, micRecordMessage);
        audioView.setAudioParam(Constant.AUDIO_SAMPLES, Constant.AUDIO_BITS, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:

                //bsv_singleFile.stopDrawsThread();
                micManager.destroy();
                this.finish();
                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        micManager.destroy();
        Log.e(TAG, "onDestroy");

    }
}