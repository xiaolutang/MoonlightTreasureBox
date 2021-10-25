package com.txl.blockmoonlighttreasurebox.block;

import android.content.Context;

public interface IBlock {
    Context getApplicationContext();
    IBlock startMonitor();
    IBlock stopMonitor();
    IBlock updateConfig(BlockBoxConfig config);
}
