package com.txl.blockmoonlighttreasurebox.block;

import android.content.Context;

public class BlockMonitorFace implements IBlock{
    private final Context mApplicationContext;
    private final IBlock blockMonitor;
    private static IBlock blockMonitorFace;

    private BlockMonitorFace(Context mApplicationContext) {
        blockMonitorFace = this;
        this.mApplicationContext = mApplicationContext.getApplicationContext();
        BlockMonitor.getInstance().setApplicationContext(this.mApplicationContext);
        blockMonitor = BlockMonitor.getInstance();

    }

    @Override
    public Context getApplicationContext() {
        return mApplicationContext;
    }

    @Override
    public IBlock startMonitor() {
        return blockMonitor.startMonitor();
    }

    @Override
    public IBlock stopMonitor() {
        return blockMonitor.stopMonitor();
    }

    @Override
    public IBlock updateConfig(BlockBoxConfig config) {
        return blockMonitor.updateConfig(config);
    }

    public static IBlock getBlockMonitorFace(Context context) {
        if(blockMonitorFace == null){//这里并不在意多线程产生了多个BlockMonitorFace 对象
            blockMonitorFace = new BlockMonitorFace(context);
        }
        return blockMonitorFace;
    }
}
