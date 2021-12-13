package com.txl.blockmoonlighttreasurebox.info;

import android.os.SystemClock;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MessageInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int MSG_TYPE_NONE = 0x00;
    public static final int MSG_TYPE_INFO = 0x01;
    public static final int MSG_TYPE_WARN = 0x02;
    public static final int MSG_TYPE_ANR = 0x04;
    /**
     * 掉帧
     * */
    public static final int MSG_TYPE_JANK = 0x08;
    /**
     * 连续两个消息之间的间隙
     * */
    public static final int MSG_TYPE_GAP = 0x10;
    /**
     * 通过ActivityThread$H handle 发送的消息
     * */
    public static final int MSG_TYPE_ACTIVITY_THREAD_H = 0x20;

    @IntDef({MSG_TYPE_NONE,MSG_TYPE_INFO,MSG_TYPE_WARN,MSG_TYPE_ANR,MSG_TYPE_JANK,MSG_TYPE_GAP,MSG_TYPE_ACTIVITY_THREAD_H})
    private @interface MsgType{}

    public @MsgType int msgType = MSG_TYPE_INFO;

    /**
     * 至少有一条消息
     * */
    public int count = 0;
    /**
     * 消息分发耗时
     * */
    public long wallTime = 0;
    /**
     * SystemClock.currentThreadTimeMillis()  是当前线程方法的执行时间，不包含线程休眠 或者锁竞争等待
     * cpu 时间是函数正真执行时间
     * */
    public long cpuTime = 0;
    public List<BoxMessage> boxMessages = new ArrayList<>();

    /**
     * 消息被创建的时间
     * */
    public long messageCreateTime = SystemClock.elapsedRealtime();

    @Override
    public String toString() {
        return "MessageInfo{" +
                "msgType=" + msgTypeToString(msgType) +
                ", count=" + count +
                ", wallTime=" + wallTime +
                ", cpuTime=" + cpuTime +
                ", boxMessages=" + boxMessages +
                '}';
    }

    public static String msgTypeToString(@MsgType int msgType){
        switch (msgType){
            case MSG_TYPE_NONE:
                return "MSG_TYPE_NONE";
            case MSG_TYPE_INFO:
                return "MSG_TYPE_INFO";
            case MSG_TYPE_WARN:
                return "MSG_TYPE_WARN";
            case MSG_TYPE_ANR:
                return "MSG_TYPE_ANR";
            case MSG_TYPE_JANK:
                return "MSG_TYPE_JANK";
            case MSG_TYPE_GAP:
                return "MSG_TYPE_GAP";
            case MSG_TYPE_ACTIVITY_THREAD_H:
                return "MSG_TYPE_ACTIVITY_THREAD_H";
        }
        return "";
    }
}
