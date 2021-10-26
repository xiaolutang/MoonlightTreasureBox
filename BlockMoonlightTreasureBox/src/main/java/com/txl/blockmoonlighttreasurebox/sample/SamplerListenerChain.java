package com.txl.blockmoonlighttreasurebox.sample;

import android.util.Log;

import com.txl.blockmoonlighttreasurebox.block.BlockBoxConfig;
import com.txl.blockmoonlighttreasurebox.info.AnrInfo;
import com.txl.blockmoonlighttreasurebox.info.MessageInfo;
import com.txl.blockmoonlighttreasurebox.info.ScheduledInfo;
import com.txl.blockmoonlighttreasurebox.sample.manager.IAnrSamplerListener;
import com.txl.blockmoonlighttreasurebox.sample.manager.IMainThreadSampleListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) 2021, 唐小陆 All rights reserved.
 * author：txl
 * date：2021/10/23
 * description：todo 重构链式调用逻辑  并且重构  数据采集的逻辑  全部放在一个位置进行管理
 */
public class SamplerListenerChain implements IAnrSamplerListener {
    private final List<IAnrSamplerListener> anrSamplerListeners = new ArrayList<>();
    private AnrInfo anrInfo = new AnrInfo();

    public void addSampleListener(IAnrSamplerListener listener){
        anrSamplerListeners.add( listener );
    }

    public void addSampleListener(List<IAnrSamplerListener> listeners){
        anrSamplerListeners.addAll( listeners );
    }

    public void clearListener(){
        anrSamplerListeners.clear();
    }

//    public SamplerListenerChain(List<IAnrSamplerListener> anrSamplerListeners) {
//        this.anrSamplerListeners = anrSamplerListeners;
//        if(anrSamplerListeners == null){
//            throw new RuntimeException("not allow anrSamplerListeners null");
//        }
//    }

    @Override
    public boolean onMessageQueueSample(long baseTime, String msgId, String msg) {
        anrInfo.messageQueueSample.append( msg );
        for (IAnrSamplerListener listener: anrSamplerListeners){
            if(listener.onMessageQueueSample( baseTime, msgId, msg )){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onCpuSample(long baseTime, String msgId, String msg) {
        for (IAnrSamplerListener listener: anrSamplerListeners){
            if(listener.onCpuSample( baseTime, msgId, msg )){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onMemorySample(long baseTime, String msgId, String msg) {
        for (IAnrSamplerListener listener: anrSamplerListeners){
            if(listener.onMemorySample( baseTime, msgId, msg )){
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean onMainThreadStackSample(long baseTime, String msgId, String msg) {
        anrInfo.mainThreadStack = msg;
        for (IAnrSamplerListener listener: anrSamplerListeners){
            if(listener.onMainThreadStackSample( baseTime, msgId, msg )){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onAllAnrMessageSampleEnd() {
        AnrInfo temp = anrInfo;
        anrInfo = new AnrInfo();
        for (IAnrSamplerListener listener: anrSamplerListeners){
            if(listener.onAllAnrMessageSampleEnd( )){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onScheduledSample(boolean start,long baseTime, String msgId, long dealt) {
        anrInfo.scheduledSamplerCache.put( baseTime,new ScheduledInfo( dealt,msgId ,start) );
        for (IMainThreadSampleListener listener: anrSamplerListeners){
            if(listener.onScheduledSample(start, baseTime, msgId, dealt )){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onMsgSample(long baseTime, String msgId, MessageInfo msg) {
        if(msg.msgType == MessageInfo.MSG_TYPE_ANR){
            //接收到本次调度的anr完成
        }
        anrInfo.messageSamplerCache.put( baseTime,msg );
        for (IMainThreadSampleListener listener: anrSamplerListeners){
            if(listener.onMsgSample( baseTime, msgId, msg )){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onJankSample(String msgId, MessageInfo msg) {
        for (IMainThreadSampleListener listener: anrSamplerListeners){
            if(listener.onJankSample( msgId, msg )){
                return true;
            }
        }
        return false;
    }

    @Override
    public void messageQueueDispatchAnrFinish() {

    }
}
