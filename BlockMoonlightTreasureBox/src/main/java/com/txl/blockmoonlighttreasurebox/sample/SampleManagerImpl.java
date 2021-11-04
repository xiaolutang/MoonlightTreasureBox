package com.txl.blockmoonlighttreasurebox.sample;

import android.os.Looper;
import android.util.Log;
import android.util.Printer;

import com.txl.blockmoonlighttreasurebox.block.BlockBoxConfig;
import com.txl.blockmoonlighttreasurebox.info.MessageInfo;
import com.txl.blockmoonlighttreasurebox.sample.manager.ISamplerManager;
import com.txl.blockmoonlighttreasurebox.utils.AppExecutors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Copyright (c) 2021, 唐小陆 All rights reserved.
 * author：txl
 * date：2021/10/23
 * description：专门负责采样
 */
public class SampleManagerImpl implements ISamplerManager {
    private static final String TAG = SampleManagerImpl.class.getSimpleName();
    private final List<AbsSampler> anrSample = new ArrayList<>();
    private final AtomicBoolean inSample = new AtomicBoolean(false);
    private volatile CountDownLatch latch;
    //MessageQueue 每一个元素的处理  和 消息队列后续情况
    private int latchCount = 2;
    private long baseTime;
    private String msgId;
    private final SamplerListenerChain samplerListenerChain  = new SamplerListenerChain();
    private SampleManagerImpl() {
        AbsSampler cpuSample = new CpuSample();
        cpuSample.setSampleListener( new AbsSampler.SampleListener() {
            @Override
            public void onSampleEnd(String msgId, String msg) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        samplerListenerChain.onCpuSample( baseTime,msgId,msg );
                        Log.d(TAG,"cpu sample end "+latch.getCount());
                        latch.countDown();
                    }
                });

            }
        } );
        anrSample.add( cpuSample );

        latchCount ++;
        AbsSampler stackSample = new StackSampler( Thread.currentThread() );
        stackSample.setSampleListener( new AbsSampler.SampleListener() {
            @Override
            public void onSampleEnd(String msgId, String msg) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        samplerListenerChain.onMainThreadStackSample( baseTime,msgId,msg );
                        Log.d(TAG,"main thread stack sample end "+latch.getCount());
                        latch.countDown();
                    }
                });
            }
        } );
        anrSample.add( stackSample );
        latchCount ++;
    }

    @Override
    public void startAnrSample(String msgId, long baseTime) {
        if(inSample.get()){
            Log.d(TAG,"startAnrSample return ");
            return;
        }
        Log.d(TAG,"startAnrSample ");
        inSample.set(true);
        createLatch();
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                SampleManagerImpl.this.baseTime = baseTime;
                SampleManagerImpl.this.msgId = msgId;
                for (AbsSampler sampler : anrSample){
                    sampler.startSample( msgId,true );
                }
                //获取当前消息队列的情况
                Looper.getMainLooper().dump( new MessageQueuePrint() ,"" );
                Log.d(TAG,"MessageQueue sample end "+latch.getCount());
                latch.countDown();
            }

        });
    }

    private void dispatchMessage() {
        Log.d(TAG,"dispatchMessage");
        latch = null;
        samplerListenerChain.messageQueueDispatchAnrFinish();
        inSample.set(false);
    }

    private void createLatch(){
        if(latch == null){
            synchronized (SampleManagerImpl.class){
                if(latch == null){
                    latch = new CountDownLatch(latchCount);
                    AppExecutors.getInstance().networkIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                latch.await();
                                Log.d(TAG,"createLatch  dispatchMessage");
                                dispatchMessage();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    public void messageQueueDispatchAnrFinish() {
        if(latch != null){
            Log.d(TAG,"looper handle end "+latch.getCount());
            latch.countDown();
        }else {
            Log.e(TAG,"messageQueueDispatchAnrFinish but latch is null inSample is "+inSample.get());
            dispatchMessage();
        }
    }

    @Override
    public void onSampleAnrMsg() {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void addSample(AbsSampler sampler) {
        anrSample.add(sampler);
    }

    public static SampleManagerImpl getInstance(){
        return SampleManagerImplHolder.impl;
    }

    @Override
    public void onConfigChange(BlockBoxConfig config) {
        samplerListenerChain.clearListener();
        samplerListenerChain.addSampleListener( config.getAnrSamplerListeners() );
    }

    @Override
    public void onScheduledSample(boolean start, long baseTime, String msgId, long dealt) {
        samplerListenerChain.onScheduledSample( start, baseTime, msgId, dealt );
    }

    @Override
    public void onMsgSample(long baseTime, String msgId, MessageInfo msg) {
        samplerListenerChain.onMsgSample( baseTime, msgId, msg );
    }

    @Override
    public void onJankSample(String msgId, MessageInfo msg) {
        samplerListenerChain.onJankSample( msgId,msg );
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
