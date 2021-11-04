package com.example.test;

import android.app.Application;
import com.txl.blockmoonlighttreasurebox.block.BlockBoxConfig;
import com.txl.blockmoonlighttreasurebox.block.BlockMonitorFace;
import com.txl.blockmoonlighttreasurebox.block.SystemAnrMonitor;

public class TestApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        BlockMonitorFace.init(this)
                .updateConfig(new BlockBoxConfig.Builder().build())
                .startMonitor();
        SystemAnrMonitor.init(BlockMonitorFace.getBlockMonitorFace());
//        BlockCanary.install(TestApplication.this, new BlockCanaryContext()).start();

    }
}
