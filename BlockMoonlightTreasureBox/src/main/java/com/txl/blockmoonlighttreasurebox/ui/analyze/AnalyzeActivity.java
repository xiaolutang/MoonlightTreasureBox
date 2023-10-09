package com.txl.blockmoonlighttreasurebox.ui.analyze;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.txl.blockmoonlighttreasurebox.R;
import com.txl.blockmoonlighttreasurebox.info.AnrInfo;
import com.txl.blockmoonlighttreasurebox.info.MessageInfo;
import com.txl.blockmoonlighttreasurebox.ui.AnalyzeProtocol;

/**
 * 分析每一个anr消息
 * */
public class AnalyzeActivity extends Activity {
    private final AnalyzeSchedulingAdapter analyzeSchedulingAdapter = new AnalyzeSchedulingAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);
        initView();
    }

    private void initView(){
        RecyclerView recyclerMainThreadScheduling = findViewById(R.id.recyclerMainThreadScheduling);
        recyclerMainThreadScheduling.setAdapter(analyzeSchedulingAdapter);
        RecyclerView recyclerViewMessageQueue = findViewById(R.id.recyclerViewMessageQueue);
        AnalyzeMessageDispatchAdapter analyzeMessageDispatchAdapter = new AnalyzeMessageDispatchAdapter();
        recyclerViewMessageQueue.setAdapter(analyzeMessageDispatchAdapter);
        TextView tvNameMessageQueueDispatchItemInfo = findViewById(R.id.tvNameMessageQueueDispatchItemInfo);
        TextView tvNameMessageQueueInfo = findViewById(R.id.tvNameMessageQueueInfo);
        TextView tvNameMainThreadStackInfo = findViewById(R.id.tvNameMainThreadStackInfo);
        TextView tvNameCpuInfo = findViewById(R.id.tvNameCpuInfo);
        TextView tvNameLoadInfo = findViewById(R.id.tvNameLoadInfo);
        AnrInfo anrInfo = AnalyzeProtocol.anrInfo;
        analyzeMessageDispatchAdapter.setOnItemClickListener(new AnalyzeMessageDispatchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MessageInfo messageInfo) {
                tvNameMessageQueueDispatchItemInfo.setText(messageInfo.toString());
            }
        });

        tvNameMessageQueueInfo.setText(new String(anrInfo.messageQueueSample));
        tvNameMainThreadStackInfo.setText(anrInfo.mainThreadStack);
        tvNameCpuInfo.setText(anrInfo.cpuInfo);
        tvNameLoadInfo.setText(anrInfo.systemLoad);
        analyzeSchedulingAdapter.scheduledInfos = anrInfo.scheduledSamplerCache.getAll();
        analyzeSchedulingAdapter.notifyDataSetChanged();
        analyzeMessageDispatchAdapter.messageInfos = anrInfo.messageSamplerCache.getAll();
        analyzeMessageDispatchAdapter.notifyDataSetChanged();
    }
}