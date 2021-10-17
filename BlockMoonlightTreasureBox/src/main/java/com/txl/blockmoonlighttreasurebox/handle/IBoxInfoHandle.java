package com.txl.blockmoonlighttreasurebox.handle;

/**
 * 处理采集到的主线程运行信息
 * todo 增加ActivityThread的数据处理
 * */
public interface IBoxInfoHandle {
    /**
     * 处理正常消息
     * */
    boolean handleNormal(String msg);

    /**
     * 大于设置的丢帧数
     * */
    boolean handleJank(String msg);

    /**
     * 主线程checkTime
     * */
    boolean handleCheckThread(String msg);
}
