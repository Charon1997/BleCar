package com.example.g150s.blecarnmid.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;

import com.example.g150s.blecarnmid.R;
import com.example.g150s.blecarnmid.ui.base.BaseActivity;



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
        },1500);
    }
    /** 权限处理  初始化*/
    private void init()
    {
        relativeLayout = (RelativeLayout) findViewById(R.id.welcome);
        AlphaAnimation animation = new AlphaAnimation(0,1);
        animation.setDuration(1200);
        relativeLayout.setAnimation(animation);
    }
}
