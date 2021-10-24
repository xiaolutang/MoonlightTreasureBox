package com.txl.blockmoonlighttreasurebox;

import com.txl.blockmoonlighttreasurebox.handle.FileSample;
import com.txl.blockmoonlighttreasurebox.handle.LogSample;
import com.txl.blockmoonlighttreasurebox.sample.ISamplerManager;

import java.util.ArrayList;
import java.util.List;

public class BlockBoxConfig {
    /**
     * 超过这个时间输出警告 超过这个时间消息单独罗列出来
     */
    private long warnTime = 300;
    //这个值暂定50ms
    private long gapTime = 50;
    /**
     * 超过这个时间可直接判定为anr
     */
    private long anrTime = 3000;
    /**
     * 三大流程掉帧数 超过这个值判定为jank
     */
    private int jankFrame = 30;

    private List<ISamplerManager.ISampleListener> sampleListeners = new ArrayList<>();

    private List<ISamplerManager.IAnrSamplerListener> anrSamplerListeners = new ArrayList<>();


    public List<ISamplerManager.ISampleListener> getSampleListeners() {
        return sampleListeners;
    }

    public List<ISamplerManager.IAnrSamplerListener> getAnrSamplerListeners() {
        return anrSamplerListeners;
    }

    public long getWarnTime() {
        return warnTime;
    }

    public long getGapTime() {
        return gapTime;
    }

    public long getAnrTime() {
        return anrTime;
    }

    public int getJankFrame() {
        return jankFrame;
    }

    private BlockBoxConfig() {
    }



    public static class Builder{
        private final BlockBoxConfig config;
        public Builder(){
            config = new BlockBoxConfig();
            LogSample logSample = new LogSample();
            config.anrSamplerListeners.add( logSample );
            config.sampleListeners.add( logSample );
            FileSample fileSample = new FileSample();
            config.anrSamplerListeners.add( fileSample );
            config.sampleListeners.add( fileSample );
        }

        public Builder setWarnTime(long warnTime) {
            config.warnTime = warnTime;
            return this;
        }

        public Builder setGapTime(long gapTime) {
            config.gapTime = gapTime;
            return this;
        }

        public Builder setAnrTime(long anrTime) {
            config.anrTime = anrTime;
            return this;
        }

        public Builder setJankFrame(int jankFrme) {
            config.jankFrame = jankFrme;
            return this;
        }

        public Builder addSampleListener(ISamplerManager.ISampleListener sampleListener) {
            config.sampleListeners.add( sampleListener );
            return this;
        }

        public Builder addFirstSampleListener(ISamplerManager.ISampleListener sampleListener) {
            config.sampleListeners.add( sampleListener );
            return this;
        }

        public Builder addAnrSampleListener(ISamplerManager.IAnrSamplerListener anrSamplerListener) {
            config.anrSamplerListeners.add( anrSamplerListener );
            return this;
        }

        public Builder addFirstAnrSampleListener(ISamplerManager.IAnrSamplerListener anrSamplerListener) {
            config.anrSamplerListeners.add( anrSamplerListener );
            return this;
        }

        public BlockBoxConfig build(){
            return config;
        }
    }

    public interface IConfigChangeListener{
        void onConfigChange(BlockBoxConfig config);
    }
}
