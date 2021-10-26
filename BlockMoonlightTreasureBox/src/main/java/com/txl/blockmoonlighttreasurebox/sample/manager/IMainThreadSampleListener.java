package com.txl.blockmoonlighttreasurebox.sample.manager;

import com.txl.blockmoonlighttreasurebox.info.MessageInfo;

/**
 * 这些方法调用都在主线程
 * 注意不要搞耗时操作
 */
public interface IMainThreadSampleListener {
    /**
     * 当前主线程的调度能力
     *
     * @param start true 发起本次调度  false结束
     */
    boolean onScheduledSample(boolean start, long baseTime, String msgId, long dealt);

    /**
     * 采集消息队列每次处理的消息  当消息类型是anr的时候，调用者不是主线程
     */
    boolean onMsgSample(long baseTime, String msgId, MessageInfo msg);

    boolean onJankSample(String msgId, MessageInfo msg);

    /**
     * 消息队列中发生anr的消息已经处理完毕
     * */
    void messageQueueDispatchAnrFinish();
}
