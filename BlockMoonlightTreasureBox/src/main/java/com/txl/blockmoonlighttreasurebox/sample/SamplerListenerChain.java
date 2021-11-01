package com.txl.blockmoonlighttreasurebox.sample;

import android.util.Log;

import com.txl.blockmoonlighttreasurebox.info.MessageInfo;
import com.txl.blockmoonlighttreasurebox.sample.manager.IAnrSamplerListener;
import com.txl.blockmoonlighttreasurebox.sample.manager.IMainThreadSampleListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) 2021, 唐小陆 All rights reserved.
 * author：txl
 * date：2021/10/23
 * description：
 */
public class SamplerListenerChain implements IAnrSamplerListener {
    private final List<IAnrSamplerListener> anrSamplerListeners = new ArrayList<>();
    private final String TAG = SamplerListenerChain.class.getSimpleName();

    public void addSampleListener(IAnrSamplerListener listener){
        anrSamplerListeners.add( listener );
    }

    public void addSampleListener(List<IAnrSamplerListener> listeners){
        anrSamplerListeners.addAll( listeners );
    }

    public void clearListener(){
        anrSamplerListeners.clear();
    }

    @Override
    public void onMessageQueueSample(long baseTime, String msgId, String msg) {
        for (IAnrSamplerListener listener: anrSamplerListeners){
            listener.onMessageQueueSample( baseTime, msgId, msg );
        }
    }

    @Override
    public void onCpuSample(long baseTime, String msgId, String msg) {
        for (IAnrSamplerListener listener: anrSamplerListeners){
            listener.onCpuSample( baseTime, msgId, msg );
        }
    }

    @Override
    public void onMemorySample(long baseTime, String msgId, String msg) {
        for (IAnrSamplerListener listener: anrSamplerListeners){
            listener.onMemorySample( baseTime, msgId, msg );
        }
    }


    @Override
    public void onMainThreadStackSample(long baseTime, String msgId, String msg) {
        for (IAnrSamplerListener listener: anrSamplerListeners){
            listener.onMainThreadStackSample( baseTime, msgId, msg );
        }
    }

    @Override
    public void onSampleAnrMsg() {
        for (IAnrSamplerListener listener: anrSamplerListeners){
            listener.onSampleAnrMsg( );
        }
    }

    @Override
    public void onScheduledSample(boolean start,long baseTime, String msgId, long dealt) {
        for (IMainThreadSampleListener listener: anrSamplerListeners){
            listener.onScheduledSample(start, baseTime, msgId, dealt );
        }
    }

    @Override
    public void onMsgSample(long baseTime, String msgId, MessageInfo msg) {
        for (IMainThreadSampleListener listener: anrSamplerListeners){
            listener.onMsgSample( baseTime, msgId, msg );
        }
        if(msg.msgType == MessageInfo.MSG_TYPE_ANR){
            onSampleAnrMsg();//通知采集到anr
        }
    }

    @Override
    public void onJankSample(String msgId, MessageInfo msg) {
        for (IMainThreadSampleListener listener: anrSamplerListeners){
            listener.onJankSample( msgId, msg );
        }
    }

    @Override
    public void messageQueueDispatchAnrFinish() {
        Log.d(TAG,"messageQueueDispatchAnrFinish");
        for (IMainThreadSampleListener listener: anrSamplerListeners){
            listener.messageQueueDispatchAnrFinish(  );
        }
    }
}
