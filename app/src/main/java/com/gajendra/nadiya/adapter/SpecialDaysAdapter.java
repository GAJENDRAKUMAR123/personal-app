package com.gajendra.nadiya.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gajendra.nadiya.R;
import com.gajendra.nadiya.SharedData;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class SpecialDaysAdapter extends RecyclerView.Adapter<SpecialDaysAdapter.TaskViewHolder>{
    Context context;
    List<SharedData> taskList;
   SpecialDaysAdapter.OnItemClickListener listener;

    // Constructor
    public SpecialDaysAdapter(Context context, List<SharedData> taskList, SpecialDaysAdapter.OnItemClickListener listener) {
    //public SpecialDaysAdapter(Context context, List<SharedData> taskList) {
        this.context = context;
        this.taskList = taskList;
        this.listener = listener;
    }

    // ViewHolder class
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView title, remainingDays;
        ImageView specialDaysItemTitleIcon;
        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.specialDaysItemTitle);
            remainingDays = itemView.findViewById(R.id.specialDaysItemRemainingDays);
            specialDaysItemTitleIcon = itemView.findViewById(R.id.specialDaysItemTitleIcon);
        }
    }

    // Interface for click and long-click callbacks
    public interface OnItemClickListener {
        void onItemClick(int position);
       //void onItemLongClick(SchedulerData schedulerData, int position);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.special_days_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        SharedData sharedData = taskList.get(position);
        holder.title.setText(sharedData.getTitle());
        holder.remainingDays.setText(calculateDate(sharedData.getCreate_at()));


        if((sharedData.getTitle()).contains("Anniversary")){
            holder.specialDaysItemTitleIcon.setImageResource(R.mipmap.heart);
        }else if((sharedData.getTitle()).contains("Birthday")){
            holder.specialDaysItemTitleIcon.setImageResource(R.drawable.cake);
        }else{
            holder.specialDaysItemTitleIcon.setImageResource(R.drawable.s);
        }

        holder.itemView.setOnClickListener(v -> {
            listener.onItemClick(position);
        });
        /*
        holder.itemView.setOnLongClickListener(v -> {
            listener.onItemLongClick(schedulerData, position);
            return true;
        });
        */
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }


    static String calculateDate(String createAt) {
        String[] myArray = createAt.split("/");
        long daysBetween;

        String day = myArray[0];
        String month = myArray[1];
        int year = LocalDate.now().getYear();

        LocalDate createdDate = LocalDate.of(
                year,
                Integer.parseInt(month),
                Integer.parseInt(day));

        LocalDate currentDate = LocalDate.now();

        if (createdDate.isEqual(currentDate)) {
            daysBetween = 0;

            return String.valueOf(daysBetween);
        } else if (createdDate.isBefore(currentDate)) {
            LocalDate anniversary = LocalDate.of(
                    year + 1,
                    Integer.parseInt(month),
                    Integer.parseInt(day));

            daysBetween = ChronoUnit.DAYS.between(LocalDate.now(),anniversary);

            return String.valueOf(daysBetween);
        } else {
            LocalDate anniversary = LocalDate.of(
                    year,
                    Integer.parseInt(month),
                    Integer.parseInt(day));

            daysBetween = ChronoUnit.DAYS.between(LocalDate.now(),anniversary);

            return String.valueOf(daysBetween);
        }
    }

}
