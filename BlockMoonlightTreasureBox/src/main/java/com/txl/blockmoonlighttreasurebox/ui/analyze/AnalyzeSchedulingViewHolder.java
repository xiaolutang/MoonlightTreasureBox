package com.txl.blockmoonlighttreasurebox.ui.analyze;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.txl.blockmoonlighttreasurebox.R;
import com.txl.blockmoonlighttreasurebox.info.ScheduledInfo;
import com.txl.blockmoonlighttreasurebox.utils.DisplayUtil;

public class AnalyzeSchedulingViewHolder extends RecyclerView.ViewHolder {
    TextView tvSchedulingDealt ;
    TextView tvMsgId ;
    // 300ms 90dp  那么每 dp 对应0.9ms
    float dpMs = 0.9f;
    public AnalyzeSchedulingViewHolder(@NonNull View itemView) {
        super(itemView);
        tvSchedulingDealt = itemView.findViewById(R.id.tvSchedulingDealt);
        tvMsgId = itemView.findViewById(R.id.tvMsgId);
    }

    public void pares(ScheduledInfo info){
        int widthPx = Math.max(itemView.getResources().getDimensionPixelSize(R.dimen.dp_40),(int) (info.getDealt()*dpMs));
        tvSchedulingDealt.setText(info.getDealt()+"ms");
        ViewGroup.LayoutParams params = tvSchedulingDealt.getLayoutParams();

        params.width = widthPx;
        tvSchedulingDealt.setLayoutParams(params);
//        tvMsgId.setText("msgId："+info.getMsgId());
    }

}
