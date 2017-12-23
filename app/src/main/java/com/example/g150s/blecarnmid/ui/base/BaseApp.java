package com.example.g150s.blecarnmid.ui.base;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.example.g150s.blecarnmid.others.SendTread;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import static com.example.g150s.blecarnmid.others.Constant.IP;
import static com.example.g150s.blecarnmid.others.Constant.OPEN_BOX;

/**
 * 项目名称：BleCar
 * 类描述：
 * 创建人：Charon
 * 创建时间：2017/12/14 13:07
 * 修改人：Charon
 * 修改时间：2017/12/14 13:07
 * 修改备注：
 */

public class BaseApp extends Application {
    private static final String TAG = BaseApp.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        ZXingLibrary.initDisplayOpinion(this);
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String s) {
                Log.d("Tag", "deviceToken:" + s);
            }

            @Override
            public void onFailure(String s, String s1) {
                Log.d("tag", "onFailure: " + s + "   s1:   " + s1);
            }
        });

        UmengMessageHandler handler = new UmengMessageHandler() {
            @Override
            public void dealWithNotificationMessage(Context context, UMessage uMessage) {
                super.dealWithNotificationMessage(context, uMessage);
                String text = uMessage.text;
                if ("取药".equals(text)) {
                    new SendTread(OPEN_BOX, IP)
                            .setSendResultListener(new SendTread.SendResultListener() {
                                                       @Override
                                                       public void onSuccess() {
                                                           Log.d(TAG, "onSuccess");
                                                       }

                                                       @Override
                                                       public void onError(String msg) {
                                                           Log.d(TAG, "onError: " + msg);
                                                       }
                                                   }
                            ).start();
                }
            }
        };

        mPushAgent.setMessageHandler(handler);
    }
}
