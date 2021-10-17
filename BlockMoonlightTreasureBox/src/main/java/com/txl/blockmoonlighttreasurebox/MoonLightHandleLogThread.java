package com.txl.blockmoonlighttreasurebox;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import com.txl.blockmoonlighttreasurebox.info.MessageInfo;
import com.txl.blockmoonlighttreasurebox.handle.IBoxInfoHandle;

/**
 * 该线程用于串行处理产生的日志信息
 * */
public class MoonLightHandleLogThread extends HandlerThread {
    private static final String TAG = MoonLightHandleLogThread.class.getSimpleName();
    private volatile Handler mHandler;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private BlockBoxConfig config;

    private final IBoxInfoHandle defaultBoxIfo = new IBoxInfoHandle() {
        @Override
        public boolean handleNormal(String msg) {
            Log.i(TAG,msg);
            return false;
        }

        @Override
        public boolean handleJank(String msg) {
            Log.e(TAG,msg);
            return false;
        }

        @Override
        public boolean handleCheckThread(String msg) {
            Log.e(TAG,msg);
            return false;
        }
    };

    Runnable checkThreadRunnable = new Runnable() {
        long dealtTime = SystemClock.elapsedRealtime();
        @Override
        public void run() {
            //时间偏差 差值越大说明调度能力越差
            long offset = SystemClock.elapsedRealtime() - dealtTime;

            dealtTime = SystemClock.elapsedRealtime();
            startCheckTime();
        }
    };

    public void updateConfig(BlockBoxConfig config) {
        this.config = config;
        if(config.getBoxInfoHandle() != null){
            boxInfoHandle = config.getBoxInfoHandle();
        }else {
            boxInfoHandle = defaultBoxIfo;
        }
    }

    private class HandleMessageRunnable implements Runnable{
        MessageInfo info;

        public HandleMessageRunnable(MessageInfo info) {
            this.info = info;
        }

        @Override
        public void run() {
            if(boxInfoHandle != null){
                switch (info.msgType) {
                    case MessageInfo.MSG_TYPE_JANK: {
                        boxInfoHandle.handleJank( info.toString() );
                        return;
                    }
                    default: {
                        boxInfoHandle.handleNormal( info.toString() );
                        return;
                    }
                }

            }
        }
    }

    private IBoxInfoHandle boxInfoHandle = defaultBoxIfo;

    private MoonLightHandleLogThread(String name) {
        super(name);
        init();
    }

    private MoonLightHandleLogThread(String name, int priority) {
        super(name, priority);
        init();
    }

    private void init(){
//        startCheckTime();
    }

    @Override
    public synchronized void start() {
        super.start();
        startCheckTime();
    }

    private void startCheckTime() {
        if(config == null){
            throw new RuntimeException("before start please set config");
        }
        mainHandler.postDelayed(checkThreadRunnable, config.getWarnTime());
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        Log.d( TAG,Thread.currentThread().getName()+" onLooperPrepared" );
        mHandler = new Handler(Looper.myLooper());
    }

    public void handleMessage(MessageInfo info){
        if (mHandler == null){
            mHandler = mainHandler;
        }
        mHandler.post(new HandleMessageRunnable(info));
    }

    public static MoonLightHandleLogThread getInstance(){
        return MoonLightHandleLogThreadHolder.moonLightHandleLogThread;
    }

    private static class MoonLightHandleLogThreadHolder{
        private static final MoonLightHandleLogThread moonLightHandleLogThread = new MoonLightHandleLogThread("MoonLightThread");
    }
}
