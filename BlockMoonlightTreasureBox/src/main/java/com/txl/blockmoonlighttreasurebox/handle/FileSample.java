package com.txl.blockmoonlighttreasurebox.handle;

import com.txl.blockmoonlighttreasurebox.cache.TimeLruCache;
import com.txl.blockmoonlighttreasurebox.info.MessageInfo;
import com.txl.blockmoonlighttreasurebox.info.ScheduledInfo;
import com.txl.blockmoonlighttreasurebox.sample.ISamplerManager;

/**
 * Copyright (c) 2021, 唐小陆 All rights reserved.
 * author：txl
 * date：2021/10/23
 * description：
 */
public class FileSample implements ISamplerManager.ISampleListener, ISamplerManager.IAnrSamplerListener {

    private TimeLruCache<MessageInfo> messageSamplerCache = new TimeLruCache<>();
    private TimeLruCache<ScheduledInfo> scheduledSamplerCache = new TimeLruCache<>();
    private StringBuilder messageQueueSample = new StringBuilder();
    private String mainThreadStack;

    @Override
    public boolean onMessageQueueSample(long baseTime, String msgId, String msg) {
        messageQueueSample.append( msg );
        return false;
    }

    @Override
    public boolean onCpuSample(long baseTime, String msgId, String msg) {
        return false;
    }

    @Override
    public boolean onMemorySample(long baseTime, String msgId, String msg) {
        return false;
    }

    @Override
    public boolean onMainThreadStackSample(long baseTime, String msgId, String msg) {
        mainThreadStack = msg;
        return false;
    }

    @Override
    public boolean onAllAnrMessageSampleEnd() {
        return false;
    }

    @Override
    public boolean onScheduledSample(long baseTime, String msgId, long dealt) {
        scheduledSamplerCache.put( baseTime,new ScheduledInfo( dealt,msgId ) );
        return false;
    }

    @Override
    public boolean onMsgSample(long baseTime, String msgId, MessageInfo msg) {
        messageSamplerCache.put( baseTime,msg );
        return false;
    }

    @Override
    public boolean onJankSample(String msgId, MessageInfo msg) {
        return false;
    }
}
