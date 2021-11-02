package com.txl.blockmoonlighttreasurebox.block;

/**
 * 系统anr
 * */
public interface ISystemAnrObserved {
    /**
     * @param info anr相关信息
     * @param params 其它信息，原封不动的展示
     * */
    void onSystemAnr(String info, Object... params);
}
