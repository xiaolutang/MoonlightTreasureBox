package com.example.test;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.txl.blockmoonlighttreasurebox.block.BlockBoxConfig;
import com.txl.blockmoonlighttreasurebox.block.BlockMonitorFace;

import java.util.List;

public class TestApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        BlockMonitorFace.getBlockMonitorFace(this)
                .updateConfig(new BlockBoxConfig.Builder().build())
                .startMonitor();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
//                BlockCanary.install(TestApplication.this, new BlockCanaryContext()).start();
            }
        },1*1000);

    }
}
