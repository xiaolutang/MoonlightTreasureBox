package com.txl.blockmoonlighttreasurebox.block;

import android.content.Context;

public interface IBlock extends ISystemAnrObserver{
    Context getApplicationContext();
    IBlock startMonitor();
    IBlock stopMonitor();
    IBlock updateConfig(BlockBoxConfig config);
}
