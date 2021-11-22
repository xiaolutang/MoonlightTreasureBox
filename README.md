# BlockMoonlightTreasureBox  卡顿月光宝盒
在前面我们总结了

[app卡顿系列一 ：Handler同步屏障](https://juejin.cn/post/7028215325627793439)

[app卡顿系列二 ：屏幕刷新机制学习](https://juejin.cn/post/7028580465388814349)

[app卡顿系列三 ：ANR原理](https://juejin.cn/post/7029332222540775437)

[app卡顿系列四 ：今日头条卡顿监控方案落地](https://juejin.cn/post/7031834640034103304)

终于迎来了最后一期版本发布

# 使用

引入：



## 初始化：

月光宝盒的初始化分为两个部分

1. BlcokMonitor初始化
2. native监听初始化

```java
//BlcokMonitor 初始化最主要是提供一个  BlockBoxConfig
BlockMonitorFace.init(this)
                    .updateConfig(new BlockBoxConfig.Builder()
                            .useAnalyze(true)
                            .build())
                    .startMonitor();
//注册native监听 SIGNAL QUIT 信号
SystemAnrMonitor.init(BlockMonitorFace.getBlockMonitorFace());
```

### BlockBoxConfig#Builder

![](\picture\BlockConfigBuilder.png)

**setWarnTime:**主线程超过这个时间就会发出警告信息，并且将主线程的消息独立出来进行聚合

**setGapTime:** 当主线程处于idel状态超过这个时间后，将生成一条gap信息。如果处于idel状态小于该值则不做单独处理

**setAnrTime:**当主线程消息分发超过这个时间，则判定发生anr

**setJankFrame:** 可以简单的理解成当View绘制的三大流程耗时超过多少帧就发出警告。 详细逻辑参考：[app卡顿系列四 ：今日头条卡顿监控方案落地](https://juejin.cn/post/7031834640034103304#heading-8)

**useAnalyze:**是否在桌面生成分析的应用图标。典型的场景是测试版本可以生成，release版本不需要

**addAnrSampleListener:** 添加监听anr信息。很多ANR的文件都发生在线上，如果只有离线的anr日志，明显不利于我们分析ANR,因此提供了这个接口。

改接口接收的是一个 IAnrSamplerListener

```java
/**
 * 区别于ISampleListener 专门采集anr 信息的回调
 */
public interface IAnrSamplerListener extends IMainThreadSampleListener {
    /**
     * 收集消息队列中未处理的消息
     */
    void onMessageQueueSample(long baseTime, String msgId, String msg);

    void onCpuSample(long baseTime, String msgId, String msg);

    void onMemorySample(long baseTime, String msgId, String msg);

    void onMainThreadStackSample(long baseTime, String msgId, String msg);
}


/**
 * 这些方法调用都在主线程
 * 注意不要搞耗时操作
 */
public interface IMainThreadSampleListener {
    /**
     * 当前主线程的调度能力
     *
     * @param start true 发起本次调度  false结束
     */
    void onScheduledSample(boolean start, long baseTime, String msgId, long dealt);

    /**
     * 采集消息队列每次处理的消息  当消息类型是anr的时候，调用者不是主线程
     */
    void onMsgSample(long baseTime, String msgId, MessageInfo msg);

    void onJankSample(String msgId, MessageInfo msg);

    /**
     * 消息队列中发生anr的消息已经处理完毕
     * */
    void messageQueueDispatchAnrFinish();


    void onSampleAnrMsg();
}
```

其中需要特别注意的是onSampleAnrMsg和messageQueueDispatchAnrFinish 。 onSampleAnrMsg  意味着接收到了ANR消息，此时可以将相关的信息保存起来（如果后面没有messageQueueDispatchAnrFinish消息，可以用这个作为分析的依据）。当接收到messageQueueDispatchAnrFinish意味着本次ANR消息采集结束。

### native初始化

系统ANR的是通过SIGNAL QUIT信号来进行监听，而这里我们通过自己的注册来代替系统的监听，在消息处理完成后又将信号返还给系统。

但是月光宝盒的定位是对于ANR的消息的补足，为了和其它的一些通过监听SIGNAL QUIT信号来处理ANR的框架兼容，这里通过

SystemAnrMonitor.init(BlockMonitorFace.getBlockMonitorFace())来注册通知BlockMonitor接收到了ANR消息。

```java


public class SystemAnrMonitor {
    static {
        System.loadLibrary("block_signal");
    }
    private native void hookSignalCatcher(ISystemAnrObserver observed);
    private native void unHookSignalCatcher();

    public static void init(ISystemAnrObserver systemAnrObserver){
        new SystemAnrMonitor().hookSignalCatcher(systemAnrObserver);
    }
}

public interface ISystemAnrObserver {
    void onSystemAnr();
}
```

如果使用其它的框架，只需要在监听到系统ANR的时候调用onSystemAnr即可。

# 说明

虽然IAnrSamplerListener有CPU和Memory信息的收集回调，但是月光宝盒并没有做对应的实现，因为月光宝盒做的是对系统ANR信息的补足，而不是对应的替代。当系统发生ANR的时候配合系统的信息和月光宝盒收集的信息更加方便的分析ANR。

# 案例：

参考项目Test模块，主要测试了三个内容

## jank掉帧

我们在View的draw方法中休眠500ms来观察日志

```java
@Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        long start = SystemClock.elapsedRealtime();
        if(jank){
            SystemClock.sleep(500);
        }
        long end = SystemClock.elapsedRealtime();
        Log.d("JankView","BlockMonitor start "+start + "  end "+end + "   "+(end - start));
    }
```

日志信息：

![image-20211122141737373](\picture\jank日志.png)

## 单个消息耗时，ANR

我们先给消息队列发送一个长耗时的，runnable，然后发送一个前台广播进行测试。最终我们发现产生了两条ANR记录

![](\picture\两条记录.png)

原因是消息队列的处理某个消息的时候，我们自己先发现ANR  接着、月光宝盒或者是系统触发了ANR结束消息。因为这个结束有两次触发，因此产生了两条消息。

![](\picture\单个消息耗时分析.jpg)

可以看到，发生anr是当前处理消息耗时 wallTime = 10727ms  通过android.os.Hander的实例对象进行消息发送，其中callback为MainActivity中的内部类，消息队列中还有两个消息等待处理，耗时堆栈命中Thread.sleep  

第二个日志就不在这个位置进行分析了，因为第一个日志将收集到的信息使用了，第二个没有太多的可用信息，但是通过主线程Looper一旦发现某个主线程任务耗时超过指定ANR时间(默认3s),那么这个方法一定时应该优先被处理的。

## 消息队列消息频繁，ANR

我们通过给主线程发送较多的比较耗时的任务，以此来模拟主线程消息队列繁忙

```java
findViewById(R.id.tvTestAnr2).setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        //发送多个不是非常严重耗时消息，模拟消息队列繁忙
        for (int i=25;i>0;i--){
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    //500ms
                    SystemClock.sleep(500);
                }
            });
        }
        AnrTestBroadcast.sentBroadcast(MainActivity.this);
    }
});
```

结果分析：

![](\picture\单个不耗时多个耗时.jpg)

这个也就反应了，很多时候可能命名我们没有进行耗时操作，但是从系统侧却发生了ANR。

## 其它线程或者进程抢占CPU,导致主线程调度能力不足

这个不是太好模拟

## 如何扩展进行线上日志采集

参考BlockBoxConfig#Builder

# 技术交流群：

![](\picture\MoonlightTreasureBox技术交流群二维码.png)



# 代码地址：

https://github.com/xiaolutang/MoonlightTreasureBox

