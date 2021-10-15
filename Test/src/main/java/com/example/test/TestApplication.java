package com.example.test;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.github.moduth.blockcanary.BlockCanary;
import com.github.moduth.blockcanary.BlockCanaryContext;
import com.txl.blockmoonlighttreasurebox.LooperMonitor;

public class TestApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LooperMonitor.getInstance().startMonitor();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
//                BlockCanary.install(TestApplication.this, new BlockCanaryContext()).start();
            }
        },1*1000);

    }
}
