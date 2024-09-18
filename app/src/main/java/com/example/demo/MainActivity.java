package com.example.demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.example.demo.adapter.MainCardAdapter;
import com.example.demo.utils.CardInfo;
import com.example.demo.utils.CardType;
import com.ymtz.commonlib.utils.ScreenUtil;
import com.yanzhenjie.recyclerview.OnItemClickListener;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.cache.converter.SerializableDiskConverter;
import com.zhouyou.http.cache.model.CacheMode;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = MainActivity.class.getSimpleName();

    private List<CardInfo> cardList = new ArrayList<>();

    SwipeMenuCreator swipeMenuCreator = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView(){

        initCards();
        SwipeRecyclerView recyclerView = (SwipeRecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        Log.i(TAG, "cardList.size=" + cardList.size());

        swipeMenuCreator = new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int position) {

                Log.e(TAG, "onCreateMenu position=" + position);

                int width = ScreenUtil.dip2px(MainActivity.this, 71);
                int height = ViewGroup.LayoutParams.MATCH_PARENT;

                SwipeMenuItem deleteItem = new SwipeMenuItem(MainActivity.this)
                        .setBackground(R.color.blue_top)
                        .setText("说明")
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                rightMenu.addMenuItem(deleteItem);// 添加菜单到右侧。


                SwipeMenuItem addItem = new SwipeMenuItem(MainActivity.this)
                        .setBackground(R.color.red_delete)
                        .setText("删除")
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                rightMenu.addMenuItem(addItem); // 添加菜单到右侧。

            }
        };
        recyclerView.setSwipeMenuCreator(swipeMenuCreator);
        recyclerView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int adapterPosition) {
                CardInfo card = cardList.get(adapterPosition);
                switch (card.getCardType()) {
                    case CARD_TYPE_MICPHONE: {
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, MicrophoneWaveActivity.class);
                        startActivity(intent);
                        ((Activity) MainActivity.this).overridePendingTransition(R.anim.push_left_in, R.anim.budong);
                    }
                    break;
                    case CARD_TYPE_CHAT: {
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, ConversationActivity.class);
                        startActivity(intent);
                        ((Activity) MainActivity.this).overridePendingTransition(R.anim.push_left_in, R.anim.budong);
                    }
                    break;
                    case CARD_TYPE_REACT_NATIVE: {
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, ReactNativeTestActivity.class);
                        startActivity(intent);
                        ((Activity) MainActivity.this).overridePendingTransition(R.anim.push_left_in, R.anim.budong);
                    }
                    break;
                    default:
                        break;
                }

            }
        });


        recyclerView.setOnItemMenuClickListener(new OnItemMenuClickListener() {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge, int adapterPosition) {
                menuBridge.closeMenu();
                int menuPosition = menuBridge.getPosition();
                CardInfo card = cardList.get(adapterPosition);
                switch (card.getCardType()) {
                    case CARD_TYPE_MICPHONE:
                        if (menuPosition == 0) {
                            Intent intent = new Intent();
                            intent.putExtra("title", getResources().getString(R.string.card_name_micphone_wave));
                            intent.putExtra("desc", getResources().getString(R.string.card_desc_micphone_wave));
                            intent.setClass(MainActivity.this, DescActivity.class);

                            startActivity(intent);
                            ((Activity) MainActivity.this).overridePendingTransition(R.anim.push_down_in, R.anim.budong);
                        }
                        break;
                    case CARD_TYPE_CHAT:
                        if (menuPosition == 0) {
                            Intent intent = new Intent();
                            intent.putExtra("title", getResources().getString(R.string.card_name_chat));
                            intent.putExtra("desc", getResources().getString(R.string.card_desc_chat));
                            intent.setClass(MainActivity.this, DescActivity.class);

                            startActivity(intent);
                            ((Activity) MainActivity.this).overridePendingTransition(R.anim.push_down_in, R.anim.budong);
                        }
                        break;
                    case CARD_TYPE_REACT_NATIVE:
                        if (menuPosition == 0) {
                            Intent intent = new Intent();
                            intent.putExtra("title", getResources().getString(R.string.card_name_react_native));
                            intent.putExtra("desc", getResources().getString(R.string.card_desc_react_native));
                            intent.setClass(MainActivity.this, DescActivity.class);

                            startActivity(intent);
                            ((Activity) MainActivity.this).overridePendingTransition(R.anim.push_down_in, R.anim.budong);
                        }
                        break;
                    default:
                        break;
                }
                if (card.getCardType() == CardType.CARD_TYPE_MICPHONE) {

                }
            }
        });
        MainCardAdapter adapter = new MainCardAdapter(cardList);
        recyclerView.setAdapter(adapter);



    }

    private void initData() {
        EasyHttp.init(getApplication());//默认初始化
        setEasyHttp();
    }

    public void setEasyHttp() {
        //全局设置请求头

        //headers.put("User-Agent", SystemInfoUtils.getUserAgent(this, AppConstant.APPID));
        //全局设置请求参数
        //HttpParams params = new HttpParams();
        //params.put("appId", AppConstant.APPID);

        //以下设置的所有参数是全局参数,同样的参数可以在请求的时候再设置一遍,那么对于该请求来讲,请求中的参数会覆盖全局参数
        EasyHttp.getInstance()

                //可以全局统一设置全局URL
                .setBaseUrl("https://live.corpautohome.com/")//设置全局URL  url只能是域名 或者域名+端口号

                // 打开该调试开关并设置TAG,不需要就不要加入该行
                // 最后的true表示是否打印内部异常，一般打开方便调试错误
                .debug("EasyHttp", true)

                //如果使用默认的60秒,以下三行也不需要设置
                .setReadTimeOut(6 * 1000)
                .setWriteTimeOut(6 * 100)
                .setConnectTimeout(6 * 100)

                //可以全局统一设置超时重连次数,默认为3次,那么最差的情况会请求4次(一次原始请求,三次重连请求),
                //不需要可以设置为0
                .setRetryCount(3)//网络不好自动重试3次
                //可以全局统一设置超时重试间隔时间,默认为500ms,不需要可以设置为0
                .setRetryDelay(500)//每次延时500ms重试
                //可以全局统一设置超时重试间隔叠加时间,默认为0ms不叠加
                .setRetryIncreaseDelay(500)//每次延时叠加500ms

                //可以全局统一设置缓存模式,默认是不使用缓存,可以不传,具体请看CacheMode
                .setCacheMode(CacheMode.NO_CACHE)
                //可以全局统一设置缓存时间,默认永不过期
                .setCacheTime(-1)//-1表示永久缓存,单位:秒 ，Okhttp和自定义RxCache缓存都起作用
                //全局设置自定义缓存保存转换器，主要针对自定义RxCache缓存
                .setCacheDiskConverter(new SerializableDiskConverter())//默认缓存使用序列化转化
                //全局设置自定义缓存大小，默认50M
                .setCacheMaxSize(100 * 1024 * 1024)//设置缓存大小为100M
                //设置缓存版本，如果缓存有变化，修改版本后，缓存就不会被加载。特别是用于版本重大升级时缓存不能使用的情况
                .setCacheVersion(1)//缓存版本为1
                //.setHttpCache(new Cache())//设置Okhttp缓存，在缓存模式为DEFAULT才起作用

                //可以设置https的证书,以下几种方案根据需要自己设置
                .setCertificates();                                  //方法一：信任所有证书,不安全有风险
    }

    private void initCards() {
        CardInfo card1 = new CardInfo(
                CardType.CARD_TYPE_MICPHONE,
                getResources().getString(R.string.card_name_micphone_wave),
                getResources().getString(R.string.card_desc_micphone_wave),
                R.drawable.start_play);
        cardList.add(card1);
        CardInfo card2 = new CardInfo(
                CardType.CARD_TYPE_CHAT,
                getResources().getString(R.string.card_name_chat),
                getResources().getString(R.string.card_desc_chat),
                R.drawable.start_play);
        cardList.add(card2);
        CardInfo card3 = new CardInfo(
                CardType.CARD_TYPE_REACT_NATIVE,
                getResources().getString(R.string.card_name_react_native),
                getResources().getString(R.string.card_desc_react_native),
                R.drawable.start_play);
        cardList.add(card3);
        CardInfo card4 = new CardInfo(getResources().getString(R.string.card4_name),
                getResources().getString(R.string.card4_desc),
                R.drawable.start_play);
        cardList.add(card4);
        CardInfo card5 = new CardInfo(getResources().getString(R.string.card5_name),
                getResources().getString(R.string.card5_desc),
                R.drawable.start_play);
        cardList.add(card5);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}