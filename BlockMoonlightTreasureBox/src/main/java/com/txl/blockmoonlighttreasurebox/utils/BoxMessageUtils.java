package com.txl.blockmoonlighttreasurebox.utils;

import android.os.SystemClock;

import com.txl.blockmoonlighttreasurebox.info.BoxMessage;

public class BoxMessageUtils {
    /**
     * 处理Looper 发出的消息  消息样例： >>>>> Dispatching to " + msg.target + " " +
     * msg.callback + ": " + msg.what
     * >>>>> Dispatching to Handler (android.view.ViewRootImpl$ViewRootHandler) {3346d43} com.example.test.MainActivity$1@7250fab: 0
     */
    public static BoxMessage parseLooperStart(String msg) {
        BoxMessage boxMessage;
        try {
            msg = msg.trim();
//            long start = System.currentTimeMillis();
            String[] msgA = msg.split(":");
            int what = Integer.parseInt(msgA[1].trim());
            //>>>>> Dispatching to Handler (android.view.ViewRootImpl$ViewRootHandler) {3346d43} com.example.test.MainActivity$1@7250fab
            int callbackStart = msgA[0].indexOf("}");
            String callback = callbackStart != -1 ? msgA[0].substring(callbackStart + 1) : "none";
            int handlerEnd = msgA[0].indexOf(")");
            String handler = msgA[0].substring(30, handlerEnd);
            int addrEnd = msgA[0].indexOf("}");
            String handlerAddr = msgA[0].substring(handlerEnd + 3, addrEnd);
//            System.out.println("split () take " + (System.currentTimeMillis() - start));
            boxMessage = new BoxMessage(handler, callback, what, handlerAddr);
        } catch (Exception e) {
            e.printStackTrace();
            boxMessage = new BoxMessage();
        }
        return boxMessage;
    }

    /**
     * 判断某条消息是不是在更新ui
     */
    public static boolean isBoxMessageDoFrame(BoxMessage message) {
        return message != null && "android.view.Choreographer$FrameHandler".equals(message.getHandleName()) && message.getCallbackName().contains("android.view.Choreographer$FrameDisplayEventReceiver");
    }

    public static boolean isBoxMessageActivityThread(BoxMessage message) {
        return message != null && "android.app.ActivityThread$H".equals(message.getHandleName());
    }
}
