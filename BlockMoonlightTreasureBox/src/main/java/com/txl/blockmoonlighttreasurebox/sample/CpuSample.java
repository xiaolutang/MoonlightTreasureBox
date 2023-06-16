package com.txl.blockmoonlighttreasurebox.sample;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Copyright (c) 2021, 唐小陆 All rights reserved.
 * author：txl
 * date：2021/10/16
 * description：
 */
public class CpuSample extends AbsSampler {
    private static final int BUFFER_SIZE = 1024;

    private int mPid = 0;
    private long mUserLast = 0;
    private long mSystemLast = 0;
    private long mIdleLast = 0;
    private long mIoWaitLast = 0;
    private long mTotalLast = 0;
    private long mAppCpuTimeLast = 0;


    public CpuSample() {
//        BlockMonitorFace.getBlockMonitorFace().getApplicationContext().getSystemService("cpuinfo");
//        ServiceManager serviceManager = new ServiceManager();
    }

    private void reset() {
        mUserLast = 0;
        mSystemLast = 0;
        mIdleLast = 0;
        mIoWaitLast = 0;
        mTotalLast = 0;
        mAppCpuTimeLast = 0;
    }

    @Override
    protected void doSample(String msgId, boolean needListener) {
        reset();

        BufferedReader cpuReader = null;
        BufferedReader pidReader = null;
        Process process = null;
        String result = "";
        try {

            // 8.0以上使用top命令获取13669695036
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                process = Runtime.getRuntime().exec("top -n 1");
                cpuReader = new BufferedReader(new InputStreamReader(process.getInputStream()), BUFFER_SIZE);
            } else {
                cpuReader = new BufferedReader(new InputStreamReader(
                        new FileInputStream("/proc/stat")), BUFFER_SIZE);
            }

            String cpuRate = cpuReader.readLine();
            if (cpuRate == null) {
                cpuRate = "";
            }

            if (mPid == 0) {
                mPid = android.os.Process.myPid();
            }
            pidReader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/" + mPid + "/stat")), BUFFER_SIZE);
            String pidCpuRate = pidReader.readLine();
            if (pidCpuRate == null) {
                pidCpuRate = "";
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String line;
                StringBuilder stringBuilder = new StringBuilder();
                Log.d(TAG, "doSample: top line start-------------------");
                while ((line = cpuReader.readLine()) != null) {
                    if (TextUtils.isEmpty(line)) {
                        continue;
                    }
                    Log.d(TAG, line);
                    stringBuilder.append(line);
                    stringBuilder.append('\n');
                    if (line.contains("TOTAL")) {
                        // 只到包含TOTAL那行
                        break;
                    }
                }
                Log.d(TAG, "doSample: top line end-----------------");
                result = stringBuilder.toString();
            } else {
                result = parse(cpuRate, pidCpuRate);
            }

        } catch (Throwable throwable) {
            Log.e(TAG, "error in cpu sample's doSample: ", throwable);
        } finally {
            try {
                if (cpuReader != null) {
                    cpuReader.close();
                }
                if (pidReader != null) {
                    pidReader.close();
                }

                if (process != null) {
                    process.destroy();
                }
            } catch (IOException exception) {
                Log.e(TAG, "error in cpu sample's finally: ", exception);
            }
            if (needListener && mSampleListener != null) {
                mSampleListener.onSampleEnd(msgId, result);
            }
        }
    }

    private String parse(String cpuRate, String pidCpuRate) {
        String[] cpuInfoArray = cpuRate.split(" ");
        if (cpuInfoArray.length < 9) {
            return "";
        }

        long user = Long.parseLong(cpuInfoArray[2]);
        long nice = Long.parseLong(cpuInfoArray[3]);
        long system = Long.parseLong(cpuInfoArray[4]);
        long idle = Long.parseLong(cpuInfoArray[5]);
        long ioWait = Long.parseLong(cpuInfoArray[6]);
        long total = user + nice + system + idle + ioWait
                + Long.parseLong(cpuInfoArray[7])
                + Long.parseLong(cpuInfoArray[8]);

        String[] pidCpuInfoList = pidCpuRate.split(" ");
        if (pidCpuInfoList.length < 17) {
            return "";
        }

        long appCpuTime = Long.parseLong(pidCpuInfoList[13])
                + Long.parseLong(pidCpuInfoList[14])
                + Long.parseLong(pidCpuInfoList[15])
                + Long.parseLong(pidCpuInfoList[16]);
        StringBuilder stringBuilder = new StringBuilder();
        if (mTotalLast != 0) {

            long idleTime = idle - mIdleLast;
            long totalTime = total - mTotalLast;

            stringBuilder
                    .append("cpu:")
                    .append((totalTime - idleTime) * 100L / totalTime)
                    .append("% ")
                    .append("app:")
                    .append((appCpuTime - mAppCpuTimeLast) * 100L / totalTime)
                    .append("% ")
                    .append("[")
                    .append("user:").append((user - mUserLast) * 100L / totalTime)
                    .append("% ")
                    .append("system:").append((system - mSystemLast) * 100L / totalTime)
                    .append("% ")
                    .append("ioWait:").append((ioWait - mIoWaitLast) * 100L / totalTime)
                    .append("% ]");
        }
        mUserLast = user;
        mSystemLast = system;
        mIdleLast = idle;
        mIoWaitLast = ioWait;
        mTotalLast = total;

        mAppCpuTimeLast = appCpuTime;
        return new String(stringBuilder);
    }

//    private String parseO() {
//
//    }
}
