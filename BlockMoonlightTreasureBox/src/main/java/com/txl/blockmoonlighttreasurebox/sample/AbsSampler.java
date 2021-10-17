package com.txl.blockmoonlighttreasurebox.sample;

import android.os.HandlerThread;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Copyright (c) 2021, 唐小陆 All rights reserved.
 * author：txl
 * date：2021/10/15
 * description：采样
 */
public abstract class AbsSampler {
    //采样后的结果处理，放在其它位置
    protected HandlerThread fileThread = new HandlerThread( "" );
    /**
     * 是否可以进行采样
     * */
    protected AtomicBoolean mShouldSample = new AtomicBoolean(true);

    /**
     * 进行采样
     * */
    protected abstract void doSample();
}
