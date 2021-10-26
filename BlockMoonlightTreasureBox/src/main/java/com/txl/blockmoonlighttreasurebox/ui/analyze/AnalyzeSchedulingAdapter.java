package com.txl.blockmoonlighttreasurebox.ui.analyze;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.txl.blockmoonlighttreasurebox.R;
import com.txl.blockmoonlighttreasurebox.info.ScheduledInfo;

import java.util.List;

public class AnalyzeSchedulingAdapter extends RecyclerView.Adapter<AnalyzeSchedulingViewHolder> {
    List<ScheduledInfo> scheduledInfos;

    @NonNull
    @Override
    public AnalyzeSchedulingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new AnalyzeSchedulingViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_analyze_scheduling,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull AnalyzeSchedulingViewHolder analyzeSchedulingViewHolder, int i) {
        analyzeSchedulingViewHolder.pares(scheduledInfos.get(i));
    }

    @Override
    public int getItemCount() {
        return scheduledInfos==null?0:scheduledInfos.size();
    }
}
