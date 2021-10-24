package com.txl.blockmoonlighttreasurebox.sample;

import android.os.Looper;
import android.util.Printer;

import com.txl.blockmoonlighttreasurebox.BlockBoxConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) 2021, 唐小陆 All rights reserved.
 * author：txl
 * date：2021/10/23
 * description：专门负责采样
 */
public class SampleManagerImpl implements ISamplerManager{
    private final List<AbsSampler> anrSample = new ArrayList<>();
    private long baseTime;
    private String msgId;
    private final SamplerListenerChain samplerListenerChain = new SamplerListenerChain();
    private SampleManagerImpl() {
        AbsSampler cpuSample = new CpuSample();
        cpuSample.setSampleListener( new AbsSampler.SampleListener() {
            @Override
            public void onSampleEnd(String msgId, String msg) {
                samplerListenerChain.onCpuSample( baseTime,msgId,msg );
            }
        } );
        anrSample.add( cpuSample );
        AbsSampler stackSample = new StackSampler( Thread.currentThread() );
        stackSample.setSampleListener( new AbsSampler.SampleListener() {
            @Override
            public void onSampleEnd(String msgId, String msg) {
                samplerListenerChain.onMainThreadStackSample( baseTime,msgId,msg );
            }
        } );
        anrSample.add( stackSample );
    }

    @Override
    public void startAnrSample(String msgId, long baseTime) {
        this.baseTime = baseTime;
        this.msgId = msgId;
        for (AbsSampler sampler : anrSample){
            sampler.startSample( msgId,true );
        }
        //获取当前消息队列的情况
        Looper.getMainLooper().dump( new MessageQueuePrint() ,"" );
        samplerListenerChain.onAllAnrMessageSampleEnd();
        //todo 采集内存信息
    }

    public static SampleManagerImpl getInstance(){
        return SampleManagerImplHolder.impl;
    }

    @Override
    public void onConfigChange(BlockBoxConfig config) {
        samplerListenerChain.onConfigChange( config );
    }

    private static class SampleManagerImplHolder{
        private final static SampleManagerImpl impl = new SampleManagerImpl();
    }

    private class MessageQueuePrint implements Printer{
        @Override
        public void println(String x) {
            samplerListenerChain.onMessageQueueSample( baseTime,msgId,x );
        }
    }
}
