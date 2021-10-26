package com.txl.blockmoonlighttreasurebox.sample;

import com.txl.blockmoonlighttreasurebox.sample.manager.ISamplerManager;

/**
 * Copyright (c) 2021, 唐小陆 All rights reserved.
 * author：txl
 * date：2021/10/23
 * description：
 */
public class SamplerFactory {
    private SamplerFactory() {
        throw new RuntimeException("SamplerFactory can not call this construction method");
    }

    public static ISamplerManager createSampleManager(){
        return SampleManagerImpl.getInstance();
    }
}
