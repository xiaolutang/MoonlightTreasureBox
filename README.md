# BlockMoonlightTreasureBox  卡顿月光宝盒
如何获取ANR信息  即当前正在处理那个消息？  根据头条博客  非耗时消息 不抓取堆栈
如何避开因系统dump anr消息暂停线程的时间？  CPU累计耗时  应该可以避免
如何抓取主线程堆栈  参考下BlockCanary 的实现
如何统计CPU累计耗时  需要C++ 配合jni 代码来实现？  看看 SystemClock 能不能满足需求  测试下SystemClock 线程时间是不是包含cpu等待
是否可以检测卡顿？  Choreographer$FrameDisplayEventReceiver doFrame 回调执行完成就是View三大流程能否在16ms 内完成   检测掉帧  可以配置掉多少帧才发出警告
app 上可视化ui
这个时间计算规则需要重新来处理
处理ActivityThread H handler 回调的系统消息
梳理各个测试场景  模拟测试



