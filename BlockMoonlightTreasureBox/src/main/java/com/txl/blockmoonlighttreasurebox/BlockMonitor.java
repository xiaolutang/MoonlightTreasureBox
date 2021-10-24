package com.txl.blockmoonlighttreasurebox;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.util.Printer;
import android.view.Choreographer;

import com.txl.blockmoonlighttreasurebox.info.BoxMessage;
import com.txl.blockmoonlighttreasurebox.info.MessageInfo;
import com.txl.blockmoonlighttreasurebox.sample.ISamplerManager;
import com.txl.blockmoonlighttreasurebox.sample.SamplerFactory;
import com.txl.blockmoonlighttreasurebox.sample.SamplerListenerChain;
import com.txl.blockmoonlighttreasurebox.utils.BoxMessageUtils;
import com.txl.blockmoonlighttreasurebox.utils.ReflectUtils;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 监控卡顿消息
 */
public class BlockMonitor implements Printer {
    private final String TAG = BlockMonitor.class.getSimpleName();
    private boolean start = false;

    /**
     * 每一帧的时间
     */
    private final long mFrameIntervalNanos = ReflectUtils.reflectLongField( Choreographer.getInstance(), Choreographer.class, "mFrameIntervalNanos", 16 );
    private final long noInit = -1;
    private long startTime = noInit, tempStartTime = noInit, lastEnd = noInit;
    private long cupStartTime = noInit, cpuTempStartTime = noInit, lastCpuEnd = noInit;
    /**
     * 超过这个时间 就发生anr
     * */
    private long monitorAnrTime = noInit,monitorMsgId = noInit;


    /**
     * 每次消息处理完成后需要置空
     */
    private MessageInfo messageInfo;
    private BoxMessage currentMsg;
    private AnrMonitorThread anrMonitorThread;

    private BlockBoxConfig config;

    /**
     * 采集anr时的相关信息
     * */
    private ISamplerManager samplerManager;
    /**
     * 正常采集
     * */
    private SamplerListenerChain sampleListener = new SamplerListenerChain();

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    Runnable checkThreadRunnable = new Runnable() {
        long dealtTime = SystemClock.elapsedRealtime();
        @Override
        public void run() {
            //时间偏差 差值越大说明调度能力越差
            long offset = SystemClock.elapsedRealtime() - dealtTime;
            sampleListener.onScheduledSample( dealtTime,"",offset );
            dealtTime = SystemClock.elapsedRealtime();
            startCheckTime();
        }
    };

    /**
     * 监控主线程调度能力
     * */
    private void startCheckTime() {
        if(config == null){
            throw new RuntimeException("before start please set config");
        }
        if(start)
        mainHandler.postDelayed(checkThreadRunnable, config.getWarnTime());
    }

    public void updateConfig(BlockBoxConfig config) {
        this.config = config;
        samplerManager.onConfigChange( config );
        sampleListener.onConfigChange( config );
    }

    private BlockMonitor() {
        samplerManager = SamplerFactory.createSampleManager();
        updateConfig( new BlockBoxConfig.Builder().build() );
    }

    //调用println 是奇数次还是偶数  默认false 偶数  true 奇数
    private final AtomicBoolean odd = new AtomicBoolean(false);

    @Override
    public void println(String x) {
        if(x.contains("<<<<< Finished to") && !odd.get()){
            return;
        }
        //原来是偶数次，那么这次进来就是奇数
        if (!odd.get()) {
            msgStart( x );
        } else {
            msgEnd( x );
        }
        odd.set(!odd.get());

    }

    private void msgStart(String msg) {
        tempStartTime = SystemClock.elapsedRealtime();
        monitorAnrTime = tempStartTime + config.getAnrTime();
        monitorMsgId++;
        currentMsg = BoxMessageUtils.parseLooperStart( msg );
        currentMsg.setMsgId( monitorMsgId );
        cpuTempStartTime = SystemClock.currentThreadTimeMillis();
        //两次消息时间差较大，单独处理消息且增加一个gap消息
        if (tempStartTime - lastEnd > config.getGapTime() && lastEnd != noInit) {
            if (messageInfo != null) {
                handleMsg();
            }
            messageInfo = new MessageInfo();
            messageInfo.msgType = MessageInfo.MSG_TYPE_GAP;
            messageInfo.wallTime = tempStartTime - lastEnd;
            messageInfo.cpuTime = cpuTempStartTime - lastCpuEnd;
            handleMsg();
        }
        if (messageInfo == null) {
            messageInfo = new MessageInfo();
            startTime = SystemClock.elapsedRealtime();
            tempStartTime = startTime;
            cupStartTime = SystemClock.currentThreadTimeMillis();
            cpuTempStartTime = cupStartTime;
        }
    }

    private void msgEnd(String msg) {
        lastEnd = SystemClock.elapsedRealtime();
        lastCpuEnd = SystemClock.currentThreadTimeMillis();
        long dealt = lastEnd - tempStartTime;
        handleJank( dealt );
        boolean msgActivityThread = BoxMessageUtils.isBoxMessageActivityThread(currentMsg);
        if (dealt > config.getWarnTime() || msgActivityThread) {
            if (messageInfo.count > 1) {//先处理原来的信息
                messageInfo.msgType = MessageInfo.MSG_TYPE_INFO;
                handleMsg();
            }
            messageInfo = new MessageInfo();
            messageInfo.wallTime = lastEnd - tempStartTime;
            messageInfo.cpuTime = lastCpuEnd - cpuTempStartTime;
            messageInfo.msgType = MessageInfo.MSG_TYPE_WARN;
            if (dealt > config.getAnrTime()) {
                messageInfo.msgType = MessageInfo.MSG_TYPE_ANR;
            }else if(msgActivityThread){
                messageInfo.msgType = MessageInfo.MSG_TYPE_ACTIVITY_THREAD_H;
            }
            messageInfo.boxMessages.add( currentMsg );
            handleMsg();
        } else {
            //统计每一次消息分发耗时 他们的叠加就是总耗时
            messageInfo.wallTime = lastEnd - startTime;
            //生成消息的时候，当前线程总的执行时间
            messageInfo.cpuTime += lastCpuEnd - cpuTempStartTime;
            messageInfo.boxMessages.add( currentMsg );
            messageInfo.count++;
            if (messageInfo.wallTime > config.getWarnTime()) {
                handleMsg();
            }
        }

    }

    private void handleJank(long dealt) {

        if (BoxMessageUtils.isBoxMessageDoFrame( currentMsg ) && dealt > mFrameIntervalNanos * config.getJankFrame()) {
            MessageInfo temp = messageInfo;
            messageInfo = new MessageInfo();
            messageInfo.msgType = MessageInfo.MSG_TYPE_JANK;
            messageInfo.boxMessages.add( currentMsg );
            messageInfo.wallTime = lastEnd - tempStartTime;
            messageInfo.cpuTime = lastCpuEnd - cpuTempStartTime;
            sampleListener.onJankSample( monitorMsgId+"",messageInfo );
//            handleMsg();
            messageInfo = temp;
        }
    }

    public synchronized void startMonitor() {
        if(start){
            Log.e( TAG,"already start" );
            return;
        }
        start = true;
        anrMonitorThread = new AnrMonitorThread("anrMonitorThread");
        anrMonitorThread.start();
        Looper.getMainLooper().setMessageLogging( this );
        startCheckTime();
    }

    /**
     * 停止监控
     * */
    public synchronized void stopMonitor(){
        Looper.getMainLooper().setMessageLogging( null );
        mainHandler.removeCallbacksAndMessages( null );
        anrMonitorThread = null;
        start = false;
    }

    private void handleMsg() {
        if (messageInfo != null) {
            sampleListener.onMsgSample( startTime,monitorMsgId+"",messageInfo );
        }
        messageInfo = null;
    }

    public static BlockMonitor getInstance() {
        return BlockMonitorHolder.blockMonitor;
    }


    private static class BlockMonitorHolder {
        static BlockMonitor blockMonitor = new BlockMonitor();
    }

    /**
     * 监控anr
     * */
    private class AnrMonitorThread extends Thread{
        private long msgId = noInit;
        private long anrTime;

        public AnrMonitorThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            super.run();
            while (start){
                //以消息开始时间加上超时时长为目标超时时间，每次超时时间到了之后，检查当前时间是否大于或等于目标时间，
                // 如果满足，则说明目标时间没有更新，也就是说本次消息没结束，则抓取堆栈。如果每次超时之后，
                // 检查当前时间小于目标时间，则说明上次消息执行结束，新的消息开始执行并更新了目标超时时间，
                // 这时异步监控需对齐目标超时，再次设置超时监控，如此往复。
                long now  = SystemClock.elapsedRealtime();
                if(now >= anrTime){//时间到了  因为Main线程存在checkTime 机制 不会存在因为长时间 idle 发生anr
                    if(monitorMsgId == msgId){
                        anrTime = now + config.getAnrTime();//重置anr 发生时间
                        //发生anr
                        Object mLogging = ReflectUtils.reflectFiled(Looper.getMainLooper(),Looper.class,"mLogging");
                        if(mLogging != BlockMonitor.this){
                            startMonitor();
                            Log.e(TAG,"startMonitor MainLooper printer set by other : "+mLogging);
                            return;
                        }
                        Log.e(TAG,"occur anr start dump stack and other info ");
                        if(start)
                        samplerManager.startAnrSample(msgId+"",SystemClock.elapsedRealtime());
                    }else {
                        //消息已经被处理了  重置anr时间
                        msgId = monitorMsgId;
                        anrTime = monitorAnrTime;
                    }
                }
                long sleepTime = SystemClock.elapsedRealtime() - anrTime;
                if(sleepTime > 0){
                    SystemClock.sleep(sleepTime);
                }
            }
        }
    }
}
