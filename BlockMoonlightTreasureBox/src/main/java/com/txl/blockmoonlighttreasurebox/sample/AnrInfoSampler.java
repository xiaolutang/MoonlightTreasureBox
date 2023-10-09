package com.txl.blockmoonlighttreasurebox.sample;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import com.txl.blockmoonlighttreasurebox.block.BlockMonitorFace;
import com.txl.blockmoonlighttreasurebox.info.BoxMessage;

import java.util.List;

/**
 * 获取系统负载
 * @author :     chuanguilin
 * @since :      2023/2/7
 */
public class AnrInfoSampler extends AbsSampler{

    @Override
    protected void doSample(String msgId, boolean needListener) {
        Log.d(TAG, "doSample: current Thread=" + Thread.currentThread().getName());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("msgId: ")
                .append(msgId)
                .append(BoxMessage.SEPARATOR);
        ActivityManager activityManager = (ActivityManager) BlockMonitorFace.getBlockMonitorFace().getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) {
            Log.d(TAG, "doSample: Activity Manager is null");
            return;
        }
        // 这里需要等待系统触发ANR之后才能拿到信息，参考bugly
        int count = 0;
        int times = 50;
        do {
            List<ActivityManager.ProcessErrorStateInfo> processErrorStateInfoList = activityManager.getProcessesInErrorState();
            Log.d(TAG, "doSample: list is null");
            if (processErrorStateInfoList != null) {
                for (ActivityManager.ProcessErrorStateInfo info : processErrorStateInfoList) {
                    Log.d(TAG, "doSample: process info-" + info.processName + "-" + info.condition);
                    if (info.condition == ActivityManager.ProcessErrorStateInfo.NOT_RESPONDING) {
                        // 获取Load信息
                        Log.d(TAG, "doSample: ANR Info. "+ info.processName +  ":" + info.longMsg);
                        if (needListener && mSampleListener != null) {
                            mSampleListener.onSampleEnd(msgId, info.longMsg);
                        }
                        return;
                    }
                }
            }
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        } while (count++ < times);
        if (needListener && mSampleListener != null) {
            mSampleListener.onSampleEnd(msgId, "");
        }
    }
}
