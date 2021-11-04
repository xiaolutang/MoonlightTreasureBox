package com.txl.blockmoonlighttreasurebox.block;

public class SystemAnrMonitor {
    static {
        System.loadLibrary("block_signal");
    }
    private native void hookSignalCatcher(ISystemAnrObserver observed);
    private native void unHookSignalCatcher();

    public static void init(ISystemAnrObserver systemAnrObserver){
        new SystemAnrMonitor().hookSignalCatcher(systemAnrObserver);
    }
}
