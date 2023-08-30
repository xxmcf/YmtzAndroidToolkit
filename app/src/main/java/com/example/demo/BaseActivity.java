package com.example.demo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.demo.utils.TitleView;

public abstract class BaseActivity extends AppCompatActivity {

    private TitleView title_view;
    private TextView desc_view;
    private String title;
    private String desc;

    public BaseActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initLayout();
        initView();
        initListener();
        initData();
    }

    protected abstract void initLayout();

    protected abstract void initView();

    protected abstract void initListener();

    protected abstract void initData();

}