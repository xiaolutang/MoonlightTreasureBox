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
    private volatile Handler mHandler;
    private String logPerFix = "TTTTTTTTTTTTTTTTTTT";
    private boolean start = false;

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
    public synchronized void start() {
        if(!start){
            super.start();
            start = true;
        }

    }

    @Override
    public void run() {
        super.run();
        mHandler = new Handler(Looper.myLooper());
    }



    public void handleMessage(MessageInfo info){
        if (mHandler == null){
            mHandler = new Handler(Looper.getMainLooper());
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {

                if(boxInfoHandle != null){
                    switch (info.msgType){
                        case MessageInfo.MSG_TYPE_ANR:{
                            boxInfoHandle.handlerAnr(info.toString());
                        }
                        case MessageInfo.MSG_TYPE_GAP:{
                            boxInfoHandle.handlerNormal(info.toString());
                        }
                        case MessageInfo.MSG_TYPE_JANK:{
                            boxInfoHandle.handleJank(info.toString());
                        }
                        case MessageInfo.MSG_TYPE_WARN:{
                            boxInfoHandle.handlerWarn(info.toString());
                        }
                        default:{
                            boxInfoHandle.handlerNormal(info.toString());
                        }
                    }

                }
            }
        });
    }

    public static MoonLightHandleLogThread getInstance(){
        return MoonLightHandleLogThreadHolder.moonLightHandleLogThread;
    }

    private static class MoonLightHandleLogThreadHolder{
        private static MoonLightHandleLogThread moonLightHandleLogThread = new MoonLightHandleLogThread("MoonLightThread");
    }
}
