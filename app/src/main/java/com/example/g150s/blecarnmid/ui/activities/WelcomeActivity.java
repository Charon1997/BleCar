package com.example.g150s.blecarnmid.ui.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;

import com.example.g150s.blecarnmid.R;
import com.example.g150s.blecarnmid.ui.base.BaseActivity;

/**
 * Created by G150S on 2017/3/14.
 */

public class WelcomeActivity extends BaseActivity{
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weclome);

        init();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(WelcomeActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        },2500);
    }
    /** 权限处理  初始化*/
    private void init()
    {
        relativeLayout = (RelativeLayout) findViewById(R.id.welcome);
        AlphaAnimation animation = new AlphaAnimation(0,1);
        animation.setDuration(2000);
        relativeLayout.setAnimation(animation);
    }
}
