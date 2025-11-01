package com.gajendra.nadiya.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gajendra.nadiya.R;
import com.gajendra.nadiya.classes.PTrackerDataClass;

import java.util.List;

public class PeriodsTrackerAdapter extends RecyclerView.Adapter<PeriodsTrackerAdapter.TaskViewHolder>{
    Context context;
    List<PTrackerDataClass> taskList;
   PeriodsTrackerAdapter.OnItemClickListener listener;

    // Constructor
    public PeriodsTrackerAdapter(Context context, List<PTrackerDataClass> taskList, PeriodsTrackerAdapter.OnItemClickListener listener) {
    //public SpecialDaysAdapter(Context context, List<PTrackerDataClass> taskList) {
        this.context = context;
        this.taskList = taskList;
        this.listener = listener;
    }

    // ViewHolder class
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView date, month;
        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.periodTrackerItemDateTV);
            month = itemView.findViewById(R.id.periodTrackerItemMonthTV);
        }
    }

    // Interface for click and long-click callbacks
    public interface OnItemClickListener {
        void onItemClick(PTrackerDataClass sharedData, int position);
        void onItemLongClick(PTrackerDataClass sharedData, int position);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.periods_tracker_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        PTrackerDataClass pTrackerDataClass = taskList.get(position);
        holder.date.setText(pTrackerDataClass.getCreate_at());
        holder.month.setText(pTrackerDataClass.getMonth());

        holder.itemView.setOnClickListener(v -> {
            listener.onItemClick(pTrackerDataClass,position);
        });
        holder.itemView.setOnLongClickListener(v -> {
            listener.onItemLongClick(pTrackerDataClass, position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
}
