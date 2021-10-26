package com.txl.blockmoonlighttreasurebox.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.txl.blockmoonlighttreasurebox.R;
import com.txl.blockmoonlighttreasurebox.handle.FileSample;
import com.txl.blockmoonlighttreasurebox.info.AnrInfo;
import com.txl.blockmoonlighttreasurebox.ui.analyze.AnalyzeActivity;
import com.txl.blockmoonlighttreasurebox.utils.AppExecutors;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class DisplayActivity extends AppCompatActivity {
    private final AtomicBoolean refresh = new AtomicBoolean(false);
    private final FileAdapter adapter = new FileAdapter();
    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        initView();
    }

    private void initView() {
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshAnrData();
            }
        });
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        refreshAnrData();
    }

    private void refreshAnrData(){
        if(refresh.get()){
            return;
        }
        refresh.set(true);
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                refresh.set(false);
                List<AnrInfo> anrInfoList = FileSample.fileCache.restoreData();;
                Collections.sort(anrInfoList, new Comparator<AnrInfo>() {
                    @Override
                    public int compare(AnrInfo o1, AnrInfo o2) {
                        return o2.fileName.compareTo(o1.fileName);
                    }
                });
                adapter.anrInfoList = anrInfoList;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });

    }

    private static class FileAdapter extends RecyclerView.Adapter<FileViewHolder>{
        List<AnrInfo> anrInfoList ;

        public void setAnrInfoList(List<AnrInfo> anrInfoList) {
            this.anrInfoList = anrInfoList;
        }

        @NonNull
        @Override
        public FileViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new FileViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_anr, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull FileViewHolder fileViewHolder, int i) {
            final int index = i;
            fileViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AnalyzeProtocol.anrInfo = anrInfoList.get(index);
                    Context context = fileViewHolder.itemView.getContext();
                    context.startActivity(new Intent(context, AnalyzeActivity.class));
                }
            });
            fileViewHolder.textView.setText(anrInfoList.get(i).fileName);
        }

        @Override
        public int getItemCount() {
            return anrInfoList==null?0:anrInfoList.size();
        }
    }

    private static class FileViewHolder extends RecyclerView.ViewHolder{
        private final TextView textView;
        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tvFileName);
        }
    }
}