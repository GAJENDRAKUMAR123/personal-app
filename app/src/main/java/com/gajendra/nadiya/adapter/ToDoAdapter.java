package com.gajendra.nadiya.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gajendra.nadiya.R;
import com.gajendra.nadiya.classes.TODOTasks;

import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.TaskViewHolder> {

    Context context;
    List<TODOTasks> taskList;
    ToDoAdapter.OnItemClickListener listener;


    // Constructor
    public ToDoAdapter(Context context, List<TODOTasks> taskList, ToDoAdapter.OnItemClickListener listener) {
        this.context = context;
        this.taskList = taskList;
        this.listener = listener;
    }

    // ViewHolder class
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView task, date;
        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            task = itemView.findViewById(R.id.taskText);
            date = itemView.findViewById(R.id.taskDate);
        }
    }

    // Interface for click and long-click callbacks
    public interface OnItemClickListener {
        void onItemClick(int position);
        void onItemLongClick(TODOTasks todoTasks, int position);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.todo_list_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TODOTasks todoTasks = taskList.get(position);
        holder.task.setText(todoTasks.getTask());
        holder.date.setText(todoTasks.getDate());

        holder.itemView.setOnClickListener(v -> listener.onItemClick(position));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onItemLongClick(todoTasks, position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
}
