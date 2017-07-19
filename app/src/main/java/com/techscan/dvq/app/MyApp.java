package com.techscan.dvq.app;

import android.app.Application;

import com.techscan.dvq.common.SoundHelper;

/**
 * Created by cloverss on 2017/7/18.
 */

public class MyApp extends Application {

    SoundHelper soundHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        soundHelper = SoundHelper.getInstance(getApplicationContext());
    }
}
