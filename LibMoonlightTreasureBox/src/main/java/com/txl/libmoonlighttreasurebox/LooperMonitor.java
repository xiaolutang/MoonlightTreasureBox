package com.txl.libmoonlighttreasurebox;

import android.os.SystemClock;
import android.util.Printer;

/**
 * 监控每个消息分发处理的时间
 * */
public class LooperMonitor implements Printer {
    private final long noInit = -1;
    private long startTime = noInit, tempStartTime = noInit;
    //调用println 是奇数次还是偶数  默认false 偶数  true 奇数
    boolean odd = false;
    @Override
    public void println(String x) {
        //原来是偶数次，那么这次进来就是奇数
        if(odd){
            tempStartTime = SystemClock.uptimeMillis();
        }else {

        }
        odd = !odd;
    }
}
