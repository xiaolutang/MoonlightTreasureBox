package com.txl.blockmoonlighttreasurebox.sample;

import com.txl.blockmoonlighttreasurebox.block.BlockBoxConfig;
import com.txl.blockmoonlighttreasurebox.info.MessageInfo;

/**
 * Copyright (c) 2021, 唐小陆 All rights reserved.
 * author：txl
 * date：2021/10/23
 * description：
 */
public interface ISamplerManager extends BlockBoxConfig.IConfigChangeListener{
    /**
     * @param msgId current handle What's the news
     * @param baseTime current msg base time
     *                 Not thread safe
     * */
    void startAnrSample(String msgId, long baseTime);

    /**
     * 采集其它的一些信息
     * */
    void addSample(AbsSampler sampler);

    /**
     * 区别于ISampleListener 专门采集anr 信息的回调
     * */
    interface IAnrSamplerListener {
        /**
         * 收集消息队列中未处理的消息
         * */
        boolean onMessageQueueSample(long baseTime,String msgId,String msg);
        boolean onCpuSample(long baseTime,String msgId,String msg);
        boolean onMemorySample(long baseTime,String msgId,String msg);

        boolean onMainThreadStackSample(long baseTime,String msgId,String msg);
        boolean onAllAnrMessageSampleEnd();
    }

    /**
     * 这些方法调用都在主线程
     * 注意不要搞耗时操作
     * */
    interface ISampleListener{
        /**
         * 当前主线程的调度能力
         * @param start true 发起本次调度  false结束
         * */
        boolean onScheduledSample(boolean start,long baseTime,String msgId,long dealt);
        /**
         * 采集消息队列每次处理的消息
         * */
        boolean onMsgSample(long baseTime,String msgId,MessageInfo msg);
        boolean onJankSample(String msgId, MessageInfo msg);
    }
}
