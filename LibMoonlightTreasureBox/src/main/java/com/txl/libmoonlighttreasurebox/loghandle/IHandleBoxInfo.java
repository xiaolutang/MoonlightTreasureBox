package com.txl.libmoonlighttreasurebox.loghandle;

/**
 * 处理采集到的主线程运行信息
 * */
public interface IHandleBoxInfo {
    /**
     * 处理正常消息
     * */
    void handlerNormal(String msg);
    /**
     * 某条消息比较耗时发出警告
     * */
    void handlerWarn(String msg);
    /**
     * 大于设定的阈值，极可能触发anr
     * */
    void handlerAnr(String msg);
}
