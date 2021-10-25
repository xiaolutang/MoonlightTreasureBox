package com.txl.blockmoonlighttreasurebox.info;

import java.io.Serializable;

/**
 * Copyright (c) 2021, 唐小陆 All rights reserved.
 * author：txl
 * date：2021/10/24
 * description：
 */
public class ScheduledInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private long dealt;
    private String msgId;

    public ScheduledInfo(long dealt, String msgId) {
        this.dealt = dealt;
        this.msgId = msgId;
    }

    public long getDealt() {
        return dealt;
    }

    public String getMsgId() {
        return msgId;
    }
}
