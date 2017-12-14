package com.example.g150s.blecarnmid.ui.base;

import android.app.Application;

import com.uuzuche.lib_zxing.activity.ZXingLibrary;

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
    @Override
    public void onCreate() {
        super.onCreate();
        ZXingLibrary.initDisplayOpinion(this);
    }
}
