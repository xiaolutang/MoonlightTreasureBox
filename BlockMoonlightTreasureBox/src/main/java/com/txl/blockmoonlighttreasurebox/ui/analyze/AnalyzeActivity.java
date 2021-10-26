package com.txl.blockmoonlighttreasurebox.ui.analyze;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.txl.blockmoonlighttreasurebox.R;
import com.txl.blockmoonlighttreasurebox.info.AnrInfo;
import com.txl.blockmoonlighttreasurebox.info.MessageInfo;
import com.txl.blockmoonlighttreasurebox.ui.AnalyzeProtocol;

/**
 * 分析每一个anr消息
 * */
public class AnalyzeActivity extends AppCompatActivity {
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
        AnrInfo anrInfo = AnalyzeProtocol.anrInfo;
        analyzeMessageDispatchAdapter.setOnItemClickListener(new AnalyzeMessageDispatchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MessageInfo messageInfo) {
                tvNameMessageQueueDispatchItemInfo.setText(messageInfo.toString());
            }
        });

        tvNameMessageQueueInfo.setText(new String(anrInfo.messageQueueSample));
        tvNameMainThreadStackInfo.setText(anrInfo.mainThreadStack);
        analyzeSchedulingAdapter.scheduledInfos = anrInfo.scheduledSamplerCache.getAll();
        analyzeSchedulingAdapter.notifyDataSetChanged();
        analyzeMessageDispatchAdapter.messageInfos = anrInfo.messageSamplerCache.getAll();
        analyzeMessageDispatchAdapter.notifyDataSetChanged();
    }
}