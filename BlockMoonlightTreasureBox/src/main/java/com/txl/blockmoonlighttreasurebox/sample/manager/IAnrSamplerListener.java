package com.txl.blockmoonlighttreasurebox.sample.manager;

/**
 * 区别于ISampleListener 专门采集anr 信息的回调
 */
public interface IAnrSamplerListener extends IMainThreadSampleListener {
    /**
     * 收集消息队列中未处理的消息
     */
    void onMessageQueueSample(long baseTime, String msgId, String msg);

    void onCpuSample(long baseTime, String msgId, String msg);

    void onMemorySample(long baseTime, String msgId, String msg);

    void onMainThreadStackSample(long baseTime, String msgId, String msg);

    /**
     * 系统负载
     * @param baseTime
     * @param msgId
     * @param msg
     */
    void onSystemLoadSample(long baseTime, String msgId, String msg);
}
