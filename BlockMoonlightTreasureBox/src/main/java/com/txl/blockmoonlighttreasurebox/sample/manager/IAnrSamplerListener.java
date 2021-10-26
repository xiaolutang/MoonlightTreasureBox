package com.txl.blockmoonlighttreasurebox.sample.manager;

/**
 * 区别于ISampleListener 专门采集anr 信息的回调
 */
public interface IAnrSamplerListener extends IMainThreadSampleListener {
    /**
     * 收集消息队列中未处理的消息
     */
    boolean onMessageQueueSample(long baseTime, String msgId, String msg);

    boolean onCpuSample(long baseTime, String msgId, String msg);

    boolean onMemorySample(long baseTime, String msgId, String msg);

    boolean onMainThreadStackSample(long baseTime, String msgId, String msg);

    boolean onAllAnrMessageSampleEnd();
}
