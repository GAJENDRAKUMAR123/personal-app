package com.gajendra.nadiya.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gajendra.nadiya.R;
import com.gajendra.nadiya.SharedData;

import java.util.List;

public class OurStoryAdapter extends RecyclerView.Adapter<OurStoryAdapter.TaskViewHolder>{
    Context context;
    List<SharedData> taskList;

    public OurStoryAdapter(Context context, List<SharedData> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    // ViewHolder class
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        ImageView id;
        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.ourStoryIV);
        }
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.our_story_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        SharedData sharedData = taskList.get(position);
        holder.id.setImageResource(sharedData.getId());
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
}
