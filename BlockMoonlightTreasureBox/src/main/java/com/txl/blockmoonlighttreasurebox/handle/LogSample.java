package com.txl.blockmoonlighttreasurebox.handle;

import android.util.Log;

import com.txl.blockmoonlighttreasurebox.info.MessageInfo;
import com.txl.blockmoonlighttreasurebox.sample.ISamplerManager;

/**
 * Copyright (c) 2021, 唐小陆 All rights reserved.
 * author：txl
 * date：2021/10/23
 * description：
 */
public final class LogSample implements ISamplerManager.ISampleListener, ISamplerManager.IAnrSamplerListener {
    private final String TAG = LogSample.class.getSimpleName();
    @Override
    public boolean onMessageQueueSample(long baseTime, String msgId, String msg) {
        StringBuilder builder = new StringBuilder();
        builder.append( "onMessageQueueSample" )
                .append( "  baseTime : " )
                .append( baseTime )
                .append( " msgId : " )
                .append( msgId )
                .append( "  msg : " )
                .append( msg );
        Log.d( TAG,new String(builder) );
        return false;
    }

    @Override
    public boolean onCpuSample(long baseTime, String msgId, String msg) {
        StringBuilder builder = new StringBuilder();
        builder.append( "onCpuSample" )
                .append( "  baseTime : " )
                .append( baseTime )
                .append( " msgId : " )
                .append( msgId )
                .append( "  msg : " )
                .append( msg );
        Log.d( TAG,new String(builder) );
        return false;
    }

    @Override
    public boolean onMemorySample(long baseTime, String msgId, String msg) {
        StringBuilder builder = new StringBuilder();
        builder.append( "onMemorySample" )
                .append( "  baseTime : " )
                .append( baseTime )
                .append( " msgId : " )
                .append( msgId )
                .append( "  msg : " )
                .append( msg );
        Log.d( TAG,new String(builder) );
        return false;
    }

    @Override
    public boolean onMainThreadStackSample(long baseTime, String msgId, String msg) {
        StringBuilder builder = new StringBuilder();
        builder.append( "onMainThreadStackSample" )
                .append( "  baseTime : " )
                .append( baseTime )
                .append( " msgId : " )
                .append( msgId )
                .append( "  msg : " )
                .append( msg );
        Log.d( TAG,new String(builder) );
        return false;
    }

    @Override
    public boolean onAllAnrMessageSampleEnd() {
        return false;
    }

    @Override
    public boolean onScheduledSample(boolean start,long baseTime, String msgId, long dealt) {
        StringBuilder builder = new StringBuilder();
        builder.append( "onScheduledSample" )
                .append( "  baseTime : " )
                .append( baseTime )
                .append( " msgId : " )
                .append( msgId )
                .append( "  dealt : " )
                .append( dealt );
        Log.d( TAG,new String(builder) );
        return false;
    }

    @Override
    public boolean onMsgSample(long baseTime, String msgId, MessageInfo msg) {
        StringBuilder builder = new StringBuilder();
        builder.append( "onMsgSample" )
                .append( "  baseTime : " )
                .append( baseTime )
                .append( " msgId : " )
                .append( msgId )
                .append( "  msg : " )
                .append( msg );
        Log.d( TAG,new String(builder) );
        return false;
    }

    @Override
    public boolean onJankSample(String msgId, MessageInfo msg) {
        StringBuilder builder = new StringBuilder();
        builder.append( "onJankSample" )
                .append( " msgId : " )
                .append( msgId )
                .append( "  msg : " )
                .append( msg );
        Log.d( TAG,new String(builder) );
        return false;
    }
}
