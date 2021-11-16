package com.txl.blockmoonlighttreasurebox.ui;

import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
import static android.content.pm.PackageManager.DONT_KILL_APP;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;

import com.txl.blockmoonlighttreasurebox.utils.AppExecutors;

public class DisplayUtils {
    public static void showAnalyzeActivityInLauncher(Context context, boolean show){
        ComponentName component = new ComponentName(context, DisplayActivity.class);
        PackageManager packageManager = context.getPackageManager();
        int newState = show ? COMPONENT_ENABLED_STATE_ENABLED : COMPONENT_ENABLED_STATE_DISABLED;
        // Blocks on IPC.
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                packageManager.setComponentEnabledSetting(component, newState, DONT_KILL_APP);
            }
        });
    }
}
