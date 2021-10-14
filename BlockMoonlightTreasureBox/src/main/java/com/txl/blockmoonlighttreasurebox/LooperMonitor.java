package com.txl.blockmoonlighttreasurebox;

import android.os.Looper;
import android.os.SystemClock;
import android.util.Printer;
import android.view.Choreographer;

import com.txl.blockmoonlighttreasurebox.info.BoxMessage;
import com.txl.blockmoonlighttreasurebox.info.MessageInfo;
import com.txl.blockmoonlighttreasurebox.utils.BoxMessageUtils;
import com.txl.blockmoonlighttreasurebox.utils.ReflectUtils;

/**
 * 监控每个消息分发处理的时间
 * */
public class LooperMonitor implements Printer {
    /**
     * 超过这个时间输出警告 超过这个时间消息单独罗列出来
     * */
    private long warnTime = 300;
    //这个值暂定50ms
    private long gapTime = 50;
    /**
     * 超过这个时间可直接判定为anr
     * */
    private long anrtime = 3000;
    /**
     * 三大流程掉帧数 超过这个值判定为jank
     * */
    private int jankFrme = 30;
    /**
     * 每一帧的时间
     * */
    private final long mFrameIntervalNanos = ReflectUtils.reflectLongField(Choreographer.getInstance(),Choreographer.class,"mFrameIntervalNanos",16);
    private final long noInit = -1;
    private long startTime = noInit, tempStartTime = noInit, lastEnd = noInit;


    /**
     * 每次消息处理完成后需要置空
     * */
    private MessageInfo messageInfo;
    private BoxMessage currentMsg;

    MoonLightHandleLogThread moonLightHandleLogThread;

    private LooperMonitor(){
    }

    //调用println 是奇数次还是偶数  默认false 偶数  true 奇数
    boolean odd = false;
    @Override
    public void println(String x) {
        //原来是偶数次，那么这次进来就是奇数
        if(!odd){
            msgStart(x);
        }else {
            msgEnd(x);
        }
        odd = !odd;

    }

    private void msgStart(String msg){
        if(messageInfo == null){
            messageInfo = new MessageInfo();
        }
        currentMsg = BoxMessageUtils.parseLooperStart(msg);
        tempStartTime = SystemClock.elapsedRealtime();
        //两次消息时间差较大，单独处理消息
        if(lastEnd - tempStartTime > gapTime){
            if(messageInfo != null){
                handleMsg();
            }
            messageInfo = new MessageInfo();
            messageInfo.msgType = MessageInfo.MSG_TYPE_GAP;
            messageInfo.wallTime = lastEnd - tempStartTime;
            messageInfo.cpuTime = lastEnd - tempStartTime;
            handleMsg();
        }
        if(messageInfo == null){
            messageInfo = new MessageInfo();
            startTime = SystemClock.elapsedRealtime();
            tempStartTime = startTime;
        }
        //todo 开启anr监控  时间对齐方案
    }

    private void msgEnd(String msg){
        lastEnd = SystemClock.elapsedRealtime();
        long dealt = lastEnd - tempStartTime;
        //当前消息在更新ui  并且处理超时  jank
        if(BoxMessageUtils.isBoxMessageDoFrame(currentMsg) && dealt>mFrameIntervalNanos*jankFrme){
            MessageInfo temp = messageInfo;
            messageInfo = new MessageInfo();
            messageInfo.msgType = MessageInfo.MSG_TYPE_JANK;
            messageInfo.boxMessages.add(currentMsg);
            messageInfo.wallTime = lastEnd - tempStartTime;
            messageInfo.wallTime = lastEnd - tempStartTime;
            handleMsg();
            messageInfo = temp;
        }
        if (dealt > warnTime){
            if(messageInfo.count > 1){//先处理原来的信息
                messageInfo.msgType = MessageInfo.MSG_TYPE_INFO;
                handleMsg();
            }
            messageInfo = new MessageInfo();
            messageInfo.wallTime = lastEnd - tempStartTime;
            messageInfo.wallTime = lastEnd - tempStartTime;
            messageInfo.msgType = MessageInfo.MSG_TYPE_WARN;
            if(dealt > anrtime){
                messageInfo.msgType = MessageInfo.MSG_TYPE_ANR;
            }
            messageInfo.boxMessages.add(currentMsg);
            handleMsg();
        }else {
            //统计每一次消息分发耗时 他们的叠加就是总耗时
            messageInfo.wallTime += lastEnd-tempStartTime;
            messageInfo.cpuTime = lastEnd-startTime;//生成消息的时候，总耗时
            messageInfo.boxMessages.add(currentMsg);
            messageInfo.count++;
            if(messageInfo.wallTime > warnTime){
                handleMsg();
            }
        }

    }

    public void startMonitor(){
        moonLightHandleLogThread = MoonLightHandleLogThread.getInstance();
        moonLightHandleLogThread.start();
        //todo 增加检测机制检测主线程 的日志打印是不是被其它框架重置了  但是不循环设置
        Looper.getMainLooper().setMessageLogging(this);
    }

    private void handleMsg(){
        if(messageInfo != null){
            moonLightHandleLogThread.handleMessage(messageInfo);
        }
        messageInfo = null;
    }

    public static LooperMonitor getInstance(){
        return LooperMonitorHolder.looperMonitor;
    }



    private static class LooperMonitorHolder{
        static LooperMonitor looperMonitor = new LooperMonitor();
    }
}
