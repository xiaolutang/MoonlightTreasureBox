package com.example.test;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.github.moduth.blockcanary.BlockCanary;
import com.github.moduth.blockcanary.BlockCanaryContext;
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
        BlockMonitorFace.init(this)
                .updateConfig(new BlockBoxConfig.Builder().build())
                .startMonitor();
//        BlockCanary.install(TestApplication.this, new BlockCanaryContext()).start();

    }
}
