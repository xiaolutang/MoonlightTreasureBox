package com.txl.blockmoonlighttreasurebox.block;

public class SystemAnrMonitor {
    static {
        System.loadLibrary("block_signal");
    }
    private native void hookSignalCatcher();

    public static void init(){
        new SystemAnrMonitor().hookSignalCatcher();
    }
}
