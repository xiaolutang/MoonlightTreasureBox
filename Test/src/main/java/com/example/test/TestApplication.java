package com.example.test;

import android.app.Application;

import com.github.moduth.blockcanary.BlockCanary;
import com.github.moduth.blockcanary.BlockCanaryContext;
import com.txl.blockmoonlighttreasurebox.LooperMonitor;

public class TestApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LooperMonitor.getInstance().startMonitor();
//        BlockCanary.install(this, new BlockCanaryContext()).start();
    }
}
