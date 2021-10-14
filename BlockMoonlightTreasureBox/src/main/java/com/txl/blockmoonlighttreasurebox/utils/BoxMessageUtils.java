package com.txl.blockmoonlighttreasurebox.utils;

import com.txl.blockmoonlighttreasurebox.info.BoxMessage;

public class BoxMessageUtils {
    /**
     * 处理Looper 发出的消息  消息样例： >>>>> Dispatching to " + msg.target + " " +
     *                         msg.callback + ": " + msg.what
     *                         >>>>> Dispatching to Handler (android.view.ViewRootImpl$ViewRootHandler) {3346d43} com.example.test.MainActivity$1@7250fab: 0
     * */
    public static BoxMessage parseLooperStart(String msg){
        BoxMessage boxMessage;
        try {
            msg = msg.trim();
            String[] msgA = msg.split(":");
            int what = Integer.parseInt(msgA[1].trim());
            //>>>>> Dispatching to Handler (android.view.ViewRootImpl$ViewRootHandler) {3346d43} com.example.test.MainActivity$1@7250fab
            msgA = msgA[0].split("\\{.*\\}");
            String callback = msgA[1];
            //>>>>> Dispatching to Handler (android.view.ViewRootImpl$ViewRootHandler)
            msgA = msgA[0].split("\\(");
            msgA = msgA[1].split("\\)");
            String handler = msgA[0];
            boxMessage = new BoxMessage(handler,callback,what);
        }catch (Exception e){
            e.printStackTrace();
            boxMessage = new BoxMessage();
        }
        return boxMessage;
    }

    /**
     * 判断某条消息是不是在更新ui
     * */
    public static boolean isBoxMessageDoFrame(BoxMessage message){
        return message != null && "android.view.Choreographer$FrameHandler".equals(message.getHandleName());
    }
}
