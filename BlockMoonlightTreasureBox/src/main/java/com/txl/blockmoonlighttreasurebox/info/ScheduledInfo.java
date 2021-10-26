package com.txl.blockmoonlighttreasurebox.info;

import java.io.Serializable;

/**
 * Copyright (c) 2021, 唐小陆 All rights reserved.
 * author：txl
 * date：2021/10/24
 * description：
 */
public class ScheduledInfo implements Serializable {
    public static final long NO_DEALT = -1;
    private static final long serialVersionUID = 1L;
    private long dealt = NO_DEALT;
    private String msgId;
    /**
     * 当前调度是否接收到了结束的信息，如果没有接收到说明主线程很久都没有处理对应的回调
     * */
    private boolean start = true;

    public ScheduledInfo(long dealt, String msgId, boolean start) {
        this.dealt = dealt;
        this.msgId = msgId;
        this.start = start;
    }

    public long getDealt() {
        return dealt;
    }

    public void setDealt(long dealt) {
        this.dealt = dealt;
    }

    public String getMsgId() {
        return msgId;
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }
}
