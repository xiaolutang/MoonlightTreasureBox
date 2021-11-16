package com.example.test;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Process;
import android.util.Log;

import com.github.moduth.blockcanary.BlockCanary;
import com.github.moduth.blockcanary.BlockCanaryContext;
import com.txl.blockmoonlighttreasurebox.block.BlockBoxConfig;
import com.txl.blockmoonlighttreasurebox.block.BlockMonitorFace;
import com.txl.blockmoonlighttreasurebox.block.SystemAnrMonitor;

public class TestApplication extends Application {
    private final String TAG = TestApplication.class.getSimpleName();
    @Override
    public void onCreate() {
        super.onCreate();
        int pid = Process.myPid();
        String processName = "";
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo process: manager.getRunningAppProcesses()) {
            if(process.pid == pid)
            {
                processName = process.processName;
            }
        }
        if(processName.equals(getPackageName())){
            Log.d(TAG,"init BlockMonitor");
            BlockMonitorFace.init(this)
                    .updateConfig(new BlockBoxConfig.Builder()
                            .useAnalyze(true)
                            .build())
                    .startMonitor();
            SystemAnrMonitor.init(BlockMonitorFace.getBlockMonitorFace());
        }


//        BlockCanary.install(TestApplication.this, new BlockCanaryContext()).start();

    }
}
