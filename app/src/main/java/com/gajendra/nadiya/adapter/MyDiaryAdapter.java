package com.gajendra.nadiya.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gajendra.nadiya.R;
import com.gajendra.nadiya.classes.DiaryDataClass;

import java.util.List;

public class MyDiaryAdapter extends RecyclerView.Adapter<MyDiaryAdapter.TaskViewHolder>{
    Context context;
    List<DiaryDataClass> taskList;
   MyDiaryAdapter.OnItemClickListener listener;

    // Constructor
    public MyDiaryAdapter(Context context, List<DiaryDataClass> taskList, MyDiaryAdapter.OnItemClickListener listener) {
        this.context = context;
        this.taskList = taskList;
        this.listener = listener;
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView title, create_at;
        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.myDiaryText);
            create_at = itemView.findViewById(R.id.myDiaryDate);
        }
    }

    // Interface for click and long-click callbacks
    public interface OnItemClickListener {
        void onItemClick(DiaryDataClass diaryDataClass, int position);
       void onItemLongClick(DiaryDataClass diaryDataClass, int position);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_diary_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        DiaryDataClass diaryDataClass = taskList.get(position);
        holder.title.setText(diaryDataClass.getTitle());
        holder.create_at.setText(diaryDataClass.getCreated_at());

        holder.itemView.setOnClickListener(v -> {
            listener.onItemClick(diaryDataClass,position);
        });

        holder.itemView.setOnLongClickListener(v -> {
            listener.onItemLongClick(diaryDataClass, position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
}
