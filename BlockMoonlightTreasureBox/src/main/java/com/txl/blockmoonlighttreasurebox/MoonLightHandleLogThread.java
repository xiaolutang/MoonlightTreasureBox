package com.txl.blockmoonlighttreasurebox;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import com.txl.blockmoonlighttreasurebox.info.MessageInfo;
import com.txl.blockmoonlighttreasurebox.loghandle.IBoxInfoHandle;

/**
 * 该线程用于串行处理产生的日志信息
 * */
public class MoonLightHandleLogThread extends HandlerThread {
    private static final String TAG = MoonLightHandleLogThread.class.getSimpleName();
    private volatile Handler mHandler;
    private String logPerFix = "TTTTTTTTTTTTTTTTTTT";

    private final IBoxInfoHandle defaultBoxIfo = new IBoxInfoHandle() {
        @Override
        public void handlerNormal(String msg) {
            Log.i(logPerFix,msg);
        }

        @Override
        public void handlerWarn(String msg) {
            Log.w(logPerFix,msg);
        }

        @Override
        public void handlerAnr(String msg) {
            Log.e(logPerFix,msg);
        }

        @Override
        public void handleJank(String msg) {
            Log.e(logPerFix,msg);
        }
    };

    private class HandleMessageRunnable implements Runnable{
        MessageInfo info;

        public HandleMessageRunnable(MessageInfo info) {
            this.info = info;
        }

        @Override
        public void run() {
            if(boxInfoHandle != null){
                switch (info.msgType) {
                    case MessageInfo.MSG_TYPE_ANR: {
                        boxInfoHandle.handlerAnr( info.toString() );
                        return;
                    }
                    case MessageInfo.MSG_TYPE_GAP: {
                        boxInfoHandle.handlerNormal( info.toString() );
                        return;
                    }
                    case MessageInfo.MSG_TYPE_JANK: {
                        boxInfoHandle.handleJank( info.toString() );
                        return;
                    }
                    case MessageInfo.MSG_TYPE_WARN: {
                        boxInfoHandle.handlerWarn( info.toString() );
                        return;
                    }
                    default: {
                        boxInfoHandle.handlerNormal( info.toString() );
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

    }

    @Override
    public void run() {
        super.run();

    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        Log.d( TAG,Thread.currentThread().getName()+" onLooperPrepared" );
        mHandler = new Handler(Looper.myLooper());
    }

    public void handleMessage(MessageInfo info){
        if (mHandler == null){
            mHandler = new Handler(Looper.getMainLooper());
        }
        mHandler.post(new HandleMessageRunnable(info));
    }

    public static MoonLightHandleLogThread getInstance(){
        return MoonLightHandleLogThreadHolder.moonLightHandleLogThread;
    }

    private static class MoonLightHandleLogThreadHolder{
        private static MoonLightHandleLogThread moonLightHandleLogThread = new MoonLightHandleLogThread("MoonLightThread");
    }
}
