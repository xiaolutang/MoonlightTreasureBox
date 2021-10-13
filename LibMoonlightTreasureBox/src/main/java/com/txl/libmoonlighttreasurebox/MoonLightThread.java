package com.txl.libmoonlighttreasurebox;

import android.os.HandlerThread;

/**
 * Loop detection of the main thread's work
 * */
public class MoonLightThread extends HandlerThread {
    public MoonLightThread(String name) {
        super(name);
    }

    public MoonLightThread(String name, int priority) {
        super(name, priority);
    }


    @Override
    public void run() {
        super.run();
    }
}
