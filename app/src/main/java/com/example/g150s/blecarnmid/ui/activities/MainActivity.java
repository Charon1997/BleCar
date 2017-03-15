package com.example.g150s.blecarnmid.ui.activities;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.g150s.blecarnmid.R;
import com.example.g150s.blecarnmid.others.Car;
import com.example.g150s.blecarnmid.ui.adapter.ConnectedRLAdapter;
import com.example.g150s.blecarnmid.ui.adapter.SearchingRlAdapter;
import com.example.g150s.blecarnmid.ui.base.BaseActivity;
import com.example.g150s.blecarnmid.utils.TimeUtil;
import com.nightonke.jellytogglebutton.JellyToggleButton;
import com.nightonke.jellytogglebutton.JellyTypes.Jelly;
import com.nightonke.jellytogglebutton.State;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity {

    final int msg1 = 101;
    final int msg2 = 102;

    private Timer timer;
    private TimerTask timerTask;
    private Timer searchTimer;
    private TimerTask searchTask;

    private int timeenabled = 120;/** 允许检测到的时间*/
    private int searchTime = 15 ;/** 搜索蓝牙设备的时间*/

    private Toolbar toolbar;
    /*蓝牙开关*/
    private JellyToggleButton bleButton;
    /*开放检测的开关*/
    private JellyToggleButton scanEnabled;
    private TextView remarkText;

    private ContentLoadingProgressBar progressBar;

    private RecyclerView connectedRL;
    private RecyclerView.LayoutManager connectedLayoutManager;
    private ConnectedRLAdapter connectedRLAdapter;

    private RecyclerView searchingRL;
    private RecyclerView.LayoutManager searchingLayoutManager;
    private SearchingRlAdapter searchingRlAdapter;

    private ImageView searchIMG;
    private ImageView moreIMG;


    /**  1.在开机引导页判断用户手机是否支持蓝牙4.0
     * 2.加入蓝牙模块后 点击搜索设备时 应先检测蓝牙是否开启 如果没有开启蓝牙功能 提示用户开启蓝牙功能
     * 3.已配对设备列表有 添加 移除 已配对设备的方法
     * 4.可用设备搜索列表 有Item点击接口
     * */

    final Handler handle = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case msg1:
                    remarkText.setText("允许周围设备检测到("+ TimeUtil.MilltoMinute(timeenabled--) +")");
                    if (timeenabled == 0) {
                        timer.cancel();
                        timerTask.cancel();
                        timeenabled = 120;
                    }
                    break;
                case msg2:
                    searchTime--;
                    if (searchTime == 0)
                    {
                        searchTimer.cancel();
                        searchTask.cancel();
                        searchTime = 15;
                        progressBar.hide();
                    }
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar)findViewById(R.id.main_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        initViews();
    }
    private void initViews()
    {
        searchIMG = (ImageView) findViewById(R.id.searchIMG);
        moreIMG = (ImageView)findViewById(R.id.moreIMG);
        progressBar = (ContentLoadingProgressBar)findViewById(R.id.circle_loading);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this,R.color.black), PorterDuff.Mode.MULTIPLY);
        progressBar.hide();
        remarkText = (TextView)findViewById(R.id.remark);
        connectedRL = (RecyclerView)findViewById(R.id.connectedRL);
        connectedLayoutManager = new LinearLayoutManager(this);
        connectedRL.setLayoutManager(connectedLayoutManager);
        connectedRL.setHasFixedSize(true);
        connectedRLAdapter = new ConnectedRLAdapter(getApplicationContext());
        connectedRL.setAdapter(connectedRLAdapter);
        /*
        connectedRL.setItemAnimator(new DefaultItemAnimator());
        */

        searchingRL = (RecyclerView)findViewById(R.id.searchingRL);
        searchingLayoutManager = new LinearLayoutManager(this);
        searchingRL.setLayoutManager(searchingLayoutManager);
        searchingRL.setHasFixedSize(true);
        searchingRlAdapter = new SearchingRlAdapter(getApplicationContext());
        searchingRL.setAdapter(searchingRlAdapter);

        bleButton = (JellyToggleButton)findViewById(R.id.ble_turn);
        bleButton.setJelly(Jelly.ITSELF);
        bleButton.setLeftBackgroundColor(Color.parseColor("gray"));
        bleButton.setOnStateChangeListener(new JellyToggleButton.OnStateChangeListener() {
            @Override
            public void onStateChange(float process, State state, JellyToggleButton jtb) {
                if (state.equals(State.LEFT))
                {
                    /** 执行关闭蓝牙操作*/
                    Toast.makeText(MainActivity.this,"关闭蓝牙",Toast.LENGTH_SHORT).show();
                    if (timer!=null || timerTask != null)
                    {
                        timer.cancel();
                        timerTask.cancel();
                        timeenabled = 120;
                    }
                    if (searchTimer!= null || searchTask != null)
                    {
                        searchTimer.cancel();
                        searchTask.cancel();
                        searchTime = 15;
                        progressBar.hide();
                    }
                }
                if (state.equals(State.RIGHT))
                {
                    /** 执行打开蓝牙操作*/
                    Toast.makeText(MainActivity.this,"打开蓝牙",Toast.LENGTH_SHORT).show();
                }
            }
        });

        scanEnabled = (JellyToggleButton)findViewById(R.id.enable_btn);
        scanEnabled.setJelly(Jelly.ITSELF);
        scanEnabled.setLeftBackgroundColor(Color.parseColor("gray"));
        scanEnabled.setOnStateChangeListener(new JellyToggleButton.OnStateChangeListener() {
            @Override
            public void onStateChange(float process, State state, JellyToggleButton jtb) {
                if (state.equals(State.LEFT))
                {
                    /** 执行不允许开放检测操作*/
                    Toast.makeText(MainActivity.this,"不允许开放检测",Toast.LENGTH_SHORT).show();
                    if (timer!= null || timerTask != null)
                    {
                        timer.cancel();
                        timerTask.cancel();
                        timeenabled = 120;
                    }
                    remarkText.setText(R.string.open_remark);
                }
                if (state.equals(State.RIGHT))
                {
                    /** 执行允许开放检测*/
                    Toast.makeText(MainActivity.this,"允许开放检测",Toast.LENGTH_SHORT).show();
                    timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            Message message = new Message();
                            message.what = msg1;
                            message.obj = timeenabled;
                            handle.sendMessage(message);
                        }
                    };
                    timer = new Timer();
                    timer.schedule(timerTask,0,1000);
                }
            }
        });

        searchIMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Search!",Toast.LENGTH_SHORT).show();
                progressBar.show();
                if (searchTask!= null || searchTimer!= null)
                {
                    searchTimer.cancel();
                    searchTask.cancel();
                    searchTime = 15;
                }
                searchTask = new TimerTask() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        message.what = msg2;
                        message.obj = searchTime;
                        handle.sendMessage(message);
                    }
                };
                searchTimer = new Timer();
                searchTimer.schedule(searchTask,0,1000);
            }
        });

    }
}
