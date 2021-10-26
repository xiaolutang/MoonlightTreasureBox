package com.txl.blockmoonlighttreasurebox.sample.manager;

import com.txl.blockmoonlighttreasurebox.block.BlockBoxConfig;
import com.txl.blockmoonlighttreasurebox.info.MessageInfo;
import com.txl.blockmoonlighttreasurebox.sample.AbsSampler;

/**
 * Copyright (c) 2021, 唐小陆 All rights reserved.
 * author：txl
 * date：2021/10/23
 * description：
 */
public interface ISamplerManager extends BlockBoxConfig.IConfigChangeListener, IMainThreadSampleListener{
    /**
     * @param msgId current handle What's the news
     * @param baseTime current msg base time
     *                 Not thread safe
     * */
    void startAnrSample(String msgId, long baseTime);

    void start();

    void stop();

    /**
     * 采集其它的一些信息
     * */
    void addSample(AbsSampler sampler);

}
