package com.txl.blockmoonlighttreasurebox.ui.analyze;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.txl.blockmoonlighttreasurebox.R;
import com.txl.blockmoonlighttreasurebox.info.MessageInfo;

public class AnalyzeMessageQueueDispatchViewHolder extends RecyclerView.ViewHolder {
    private TextView tvMsgType, tvMsgId,tvWallTime, tvCpuTime, tvMsgCount;

    public AnalyzeMessageQueueDispatchViewHolder(@NonNull View itemView) {
        super(itemView);
        tvMsgType = itemView.findViewById(R.id.tvMsgType);
        tvMsgId = itemView.findViewById(R.id.tvMsgId);
        tvWallTime = itemView.findViewById(R.id.tvWallTime);
        tvCpuTime = itemView.findViewById(R.id.tvCpuTime);
        tvMsgCount = itemView.findViewById(R.id.tvMsgCount);
    }

    public void parse(MessageInfo messageInfo) {
        itemView.setBackgroundResource(getItemBg(messageInfo));
        tvMsgId.setText("msgId: ");
        if(messageInfo.boxMessages != null && messageInfo.boxMessages.size() != 0){
            tvMsgId.setText("msgId: "+messageInfo.boxMessages.get(0).getMsgId());
        }
        tvMsgType.setText("消息类型："+MessageInfo.msgTypeToString(messageInfo.msgType));
        tvWallTime.setText("wall: "+messageInfo.wallTime);
        tvCpuTime.setText("cpu: "+messageInfo.cpuTime);
        tvMsgCount.setText("msgCount: "+messageInfo.count);
    }

    private int getItemBg(MessageInfo messageInfo) {
        switch (messageInfo.msgType) {
            case MessageInfo.MSG_TYPE_GAP:
                return R.drawable.icon_msg_gap_bg;
            case MessageInfo.MSG_TYPE_ANR:
                return R.drawable.icon_msg_anr_bg;
            case MessageInfo.MSG_TYPE_WARN:
                return R.drawable.icon_msg_warn_bg;
            case MessageInfo.MSG_TYPE_ACTIVITY_THREAD_H:
                return R.drawable.icon_msg_activity_thread_h_bg;
        }
        return R.drawable.icon_msg_info_bg;
    }
}
