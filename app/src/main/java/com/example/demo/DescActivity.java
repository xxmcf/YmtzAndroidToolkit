package com.example.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.example.demo.utils.TitleView;

public class DescActivity extends AppCompatActivity {

    private TitleView title_view;
    private TextView desc_view;
    private String title;
    private String desc;

    public DescActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.desc_layout);

        this.title = getIntent().getStringExtra("title");
        this.desc = getIntent().getStringExtra("desc");

        initView();
    }

    protected void initView() {
        title_view = (TitleView)findViewById(R.id.title_view);
        title_view.setTitleText(title + "DEMO说明");
        title_view.setBackgroundColor(Color.parseColor("#0088FF"));

        desc_view = (TextView)findViewById(R.id.desc);
        desc_view.setText(desc);

        title_view.setBackOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity) DescActivity.this).overridePendingTransition(R.anim.budong, R.anim.push_down_out);
            }
        });
    }
}