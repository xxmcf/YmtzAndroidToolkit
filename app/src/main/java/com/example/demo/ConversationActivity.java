package com.example.demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.demo.adapter.MsgAdapter;
import com.example.demo.utils.Msg;
import com.example.demo.utils.TitleView;

import java.util.ArrayList;
import java.util.List;

public class ConversationActivity extends BaseActivity {

    private static final String TAG = ConversationActivity.class.getSimpleName();
    private List<Msg> msgList = new ArrayList<>();
    private EditText inputText;
    private Button btnSend;
    private RecyclerView msgRecyclerView;
    private MsgAdapter msgAdapter;


    @Override
    protected void initLayout() {
        setContentView(R.layout.activity_conversation);
        initMsgs();
    }

    @Override
    protected void initView() {
        TitleView titleView = (TitleView) findViewById(R.id.title_view);
        titleView.setTitleText(getResources().getString(R.string.card_name_chat));
        titleView.setBackOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity) ConversationActivity.this).overridePendingTransition(R.anim.budong, R.anim.push_right_out);
            }
        });

        inputText = (EditText) findViewById(R.id.input_text);
        btnSend = (Button) findViewById(R.id.send);
        msgRecyclerView = (RecyclerView) findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        msgAdapter = new MsgAdapter(msgList);
        msgRecyclerView.setAdapter(msgAdapter);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = inputText.getText().toString().trim();
                if (!"".equals(content)) {
                    Msg msg = new Msg(content, Msg.TYPE_SENT);
                    msgList.add(msg);
                    msgAdapter.notifyItemInserted(msgList.size() - 1);
                    msgRecyclerView.scrollToPosition(msgList.size() - 1);
                    inputText.setText("");
                }
            }
        });
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {

    }

    private void initMsgs() {
        Msg msg = new Msg("你好", Msg.TYPE_RECEIVED);
        msgList.add(msg);
        Msg msg2 = new Msg("你好，你是谁？", Msg.TYPE_SENT);
        msgList.add(msg2);
        Msg msg3 = new Msg("我是一个过客，你好陌生人！", Msg.TYPE_RECEIVED);
        msgList.add(msg3);
    }
}