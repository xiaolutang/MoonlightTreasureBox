package com.txl.blockmoonlighttreasurebox.info;

/**
 * 分发的Message  相关信息
 * */
public class BoxMessage {
    private String handleName;
    private String callbackName;
    private int messageWhat;

    public String getHandleName() {
        return handleName;
    }

    public String getCallbackName() {
        return callbackName;
    }

    public int getMessageWhat() {
        return messageWhat;
    }

    public BoxMessage() {
    }

    public BoxMessage(String handleName, String callbackName, int messageWhat) {
        this.handleName = handleName;
        this.callbackName = callbackName;
        this.messageWhat = messageWhat;
    }

    @Override
    public String toString() {
        return "BoxMessage{" +
                "handleName='" + handleName + '\'' +
                ", callbackName='" + callbackName + '\'' +
                ", messageWhat=" + messageWhat +
                '}';
    }
}
