package com.example.demo;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.demo.rn.NewReactNativeActivity;
import com.example.demo.utils.TitleView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.ymtz.commonlib.utils.ToastUtils;

public class ReactNativeTestActivity extends BaseActivity {

    private EditText codeText;
    private EditText serverText;
    private Button openBtn;
    private Button testBtn;

    @Override
    protected void initLayout() {
        setContentView(R.layout.activity_react_native_test);
    }

    @Override
    protected void initView() {
        TitleView titleView = (TitleView) findViewById(R.id.title_view);
        titleView.setTitleText(getResources().getString(R.string.card_name_react_native));
        titleView.setBackOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity) ReactNativeTestActivity.this).overridePendingTransition(R.anim.budong, R.anim.push_right_out);
            }
        });
        serverText = (EditText) findViewById(R.id.server_info_text);
        codeText = (EditText) findViewById(R.id.rn_code);
        openBtn = (Button) findViewById(R.id.open_rn);
        testBtn = (Button) findViewById(R.id.btn_test);

    }

    @Override
    protected void initListener() {
        openBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = codeText.getText().toString().trim();
                if (code.isEmpty()) {
                    Toast.makeText(ReactNativeTestActivity.this, "code不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                String text = serverText.getText().toString().trim();
                if (text.isEmpty()) {
                    Toast.makeText(ReactNativeTestActivity.this, "RN服务信息不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                //TODO: 打开小程序
                //校验ip和端口
                Matcher matcher = Pattern.compile("^(https://){0,}\\d+\\.\\d+\\.\\d+\\.\\d+:\\d+[/]*").matcher(text);
                if (!matcher.find()) {
                    Toast.makeText(ReactNativeTestActivity.this, "RN服务信息格式不正确", Toast.LENGTH_SHORT).show();
                    return;
                }
                String url;
                if (text.startsWith("http://") || text.startsWith("https://")) {
                    url = text;
                } else {
                    url = "http://" + text;
                }
                Intent intent = new Intent(ReactNativeTestActivity.this, NewReactNativeActivity.class);
                intent.putExtra("url", url);
                intent.putExtra("code", code);
                startActivity(intent);
                ((Activity) ReactNativeTestActivity.this).overridePendingTransition(R.anim.push_left_in, R.anim.budong);
            }
        });

        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtils.show(ReactNativeTestActivity.this, "引用 aar");
            }
        });
    }

    @Override
    protected void initData() {

    }
}