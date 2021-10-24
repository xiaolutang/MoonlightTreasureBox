package com.txl.blockmoonlighttreasurebox.sample;

import com.txl.blockmoonlighttreasurebox.BlockBoxConfig;
import com.txl.blockmoonlighttreasurebox.info.BoxMessage;
import com.txl.blockmoonlighttreasurebox.info.MessageInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) 2021, 唐小陆 All rights reserved.
 * author：txl
 * date：2021/10/23
 * description：
 */
public class SamplerListenerChain implements ISamplerManager.IAnrSamplerListener,ISamplerManager.ISampleListener, BlockBoxConfig.IConfigChangeListener {
    private List<ISamplerManager.IAnrSamplerListener> anrSamplerListeners = new ArrayList<>();
    private List<ISamplerManager.ISampleListener> sampleListeners = new ArrayList<>();


    @Override
    public boolean onMessageQueueSample(long baseTime, String msgId, String msg) {
        for (ISamplerManager.IAnrSamplerListener listener: anrSamplerListeners){
            if(listener.onMessageQueueSample( baseTime, msgId, msg )){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onCpuSample(long baseTime, String msgId, String msg) {
        for (ISamplerManager.IAnrSamplerListener listener: anrSamplerListeners){
            if(listener.onCpuSample( baseTime, msgId, msg )){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onMemorySample(long baseTime, String msgId, String msg) {
        for (ISamplerManager.IAnrSamplerListener listener: anrSamplerListeners){
            if(listener.onMemorySample( baseTime, msgId, msg )){
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean onMainThreadStackSample(long baseTime, String msgId, String msg) {
        for (ISamplerManager.IAnrSamplerListener listener: anrSamplerListeners){
            if(listener.onMainThreadStackSample( baseTime, msgId, msg )){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onAllAnrMessageSampleEnd() {
        for (ISamplerManager.IAnrSamplerListener listener: anrSamplerListeners){
            if(listener.onAllAnrMessageSampleEnd( )){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onScheduledSample(long baseTime, String msgId, long dealt) {
        for (ISamplerManager.ISampleListener listener: sampleListeners){
            if(listener.onScheduledSample( baseTime, msgId, dealt )){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onMsgSample(long baseTime, String msgId, MessageInfo msg) {
        for (ISamplerManager.ISampleListener listener: sampleListeners){
            if(listener.onMsgSample( baseTime, msgId, msg )){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onJankSample(String msgId, MessageInfo msg) {
        for (ISamplerManager.ISampleListener listener: sampleListeners){
            if(listener.onJankSample( msgId, msg )){
                return true;
            }
        }
        return false;
    }

    @Override
    public void onConfigChange(BlockBoxConfig config) {
        anrSamplerListeners = config.getAnrSamplerListeners();
        sampleListeners = config.getSampleListeners();
        if(anrSamplerListeners == null || sampleListeners == null){
            throw new RuntimeException("not allow anrSamplerListeners or sampleListeners null");
        }
    }
}
