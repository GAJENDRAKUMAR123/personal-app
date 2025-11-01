package com.gajendra.nadiya.adapter;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gajendra.nadiya.MyBroadcastReceiver;
import com.gajendra.nadiya.R;
import com.gajendra.nadiya.classes.SchedulerData;

import java.util.Calendar;
import java.util.List;

public class SchedulerAdapter extends RecyclerView.Adapter<SchedulerAdapter.TaskViewHolder> {

    Context context;
     List<SchedulerData> taskList;
    SchedulerAdapter.OnItemClickListener listener;


    // Constructor
    public SchedulerAdapter(Context context, List<SchedulerData> taskList, SchedulerAdapter.OnItemClickListener listener) {
        this.context = context;
        this.taskList = taskList;
        this.listener = listener;
    }

    // ViewHolder class
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewDesc,textViewGeneratedDte, textViewStartTime, textViewEndTime;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.schedulerTaskItemTitle);
            textViewDesc = itemView.findViewById(R.id.schedulerTaskItemDescription);
            textViewGeneratedDte = itemView.findViewById(R.id.schedulerTaskItemDate);
            textViewStartTime = itemView.findViewById(R.id.schedulerTaskItemStartDate);
            textViewEndTime = itemView.findViewById(R.id.schedulerTaskItemEndDate);
        }
    }

    // Interface for click and long-click callbacks
    public interface OnItemClickListener {
        void onItemClick(int position);
        void onItemLongClick(SchedulerData SchedulerData, int position);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.daily_scheduler_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        SchedulerData schedulerData = taskList.get(position);
        holder.textViewTitle.setText(schedulerData.getTitle());
        holder.textViewDesc.setText(schedulerData.getDesc());
        holder.textViewGeneratedDte.setText(schedulerData.getCurrentDate());
        holder.textViewStartTime.setText(schedulerData.getStartTime());
        holder.textViewEndTime.setText(schedulerData.getEndTime());

        holder.itemView.setOnClickListener(v -> listener.onItemClick(position));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onItemLongClick(schedulerData, position);
            return true;
        });
        String startTime = schedulerData.getEndTime();
        String[] parts = startTime.split(":");

        int hr = Integer.parseInt(parts[0]);
        int min = Integer.parseInt(parts[1]);

        try{
            scheduleAlarm(
                    hr,
                    min,
                    position,
                    schedulerData.getTitle(),
                    schedulerData.getEndTime()
            );
            //setAlarm();
        } catch (Exception e) {
            Toast.makeText(context, ""+e, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    @SuppressLint("ScheduleExactAlarm")
    private void scheduleAlarm(int hour, int minute, int requestCode, String title, String endTime) {
        try{
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, MyBroadcastReceiver.class);
            intent.putExtra("REQUEST_CODE", requestCode); // Optional SchedulerData
            intent.putExtra("title", title); // Optional SchedulerData
            intent.putExtra("endTime", endTime); // Optional SchedulerData
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE);

            // Set the alarm time
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute - 3);
            calendar.set(Calendar.SECOND, 55);

            if (calendar.getTimeInMillis() > System.currentTimeMillis()) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        } catch (Exception e) {
            Toast.makeText(context, ""+e, Toast.LENGTH_SHORT).show();
        }
    }
}
