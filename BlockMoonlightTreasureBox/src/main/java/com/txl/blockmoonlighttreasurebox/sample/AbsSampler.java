package com.txl.blockmoonlighttreasurebox.sample;

import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Copyright (c) 2021, 唐小陆 All rights reserved.
 * author：txl
 * date：2021/10/15
 * description：采样
 */
public abstract class AbsSampler {
    protected final String TAG = getClass().getSimpleName();
    protected SampleListener mSampleListener;
    /**
     * 是否可以进行采样
     */
    protected AtomicBoolean mShouldSample = new AtomicBoolean(true);

    /**
     * 进行采样
     */
    protected abstract void doSample(String msgId, boolean needListener);

    public void setSampleListener(SampleListener mSampleListener) {
        this.mSampleListener = mSampleListener;
    }

    public void startSample(String msgId, boolean needListener) {
        if (!mShouldSample.get()) {
            Log.d(TAG, "Abandon this sampling, it is already sampling");
            return;
        }
        mShouldSample.set(false);
        doSample(msgId, needListener);
        mShouldSample.set(true);
    }

    public interface SampleListener {
        void onSampleEnd(String msgId, String msg);
    }
}
