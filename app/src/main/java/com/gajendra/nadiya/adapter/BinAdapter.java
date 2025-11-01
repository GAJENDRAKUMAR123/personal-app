package com.gajendra.nadiya.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.gajendra.nadiya.R;
import com.gajendra.nadiya.classes.BinClass;
import com.gajendra.nadiya.classes.DBNames;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class BinAdapter extends RecyclerView.Adapter<BinAdapter.TaskViewHolder>{
    Context context;
    List<BinClass> taskList;
    boolean dropStatus = true;

    DBNames name = new DBNames();
    BinAdapter.OnItemClickListener listener;

    // Constructor
    public BinAdapter(Context context, List<BinClass> taskList, BinAdapter.OnItemClickListener listener) {
        this.context = context;
        this.taskList = taskList;
        this.listener = listener;
    }

    public BinAdapter() {
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView title, deletedAt,user,token;
        LinearLayout firstBinLayout,secondBinLayout;
        ImageView status;
        CardView restore;
        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleBin);
            deletedAt = itemView.findViewById(R.id.dateBin);
            user = itemView.findViewById(R.id.userBin);
            token = itemView.findViewById(R.id.tokenBin);

            status = itemView.findViewById(R.id.dropBin);
            restore = itemView.findViewById(R.id.restoreCard);

            firstBinLayout = itemView.findViewById(R.id.firstBinLayout);
            secondBinLayout = itemView.findViewById(R.id.secondBinLayout);
        }
    }

    // Interface for click and long-click callbacks
    public interface OnItemClickListener {
        void onItemClick(BinClass diaryDataClass, int position);
        void onItemLongClick(BinClass diaryDataClass, int position);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.bin_items, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        BinClass binClass = taskList.get(position);
        holder.title.setText(binClass.getTitle());
        holder.deletedAt.setText(binClass.getCreated_at());
        holder.token.setText(binClass.getToken());
        holder.user.setText(binClass.getUser());

        holder.itemView.setOnClickListener(v -> {
            listener.onItemClick(binClass,position);
        });

        holder.itemView.setOnLongClickListener(v -> {
            listener.onItemLongClick(binClass, position);
            return true;
        });

        holder.restore.setOnClickListener(v -> {
            if ((binClass.getToken()).contains(name.getMemoriesDB())){
                copyData(binClass.getKey(), name.getMemoriesDB());
                deleteItemFromFirebase(binClass.getKey(),position);
            }

            if ((binClass.getToken()).contains(name.getPtDB())){
                copyData(binClass.getKey(), name.getPtDB());
                deleteItemFromFirebase(binClass.getKey(),position);
            }

            if ((binClass.getToken()).contains(name.getDiaryDB())){
                copyData(binClass.getKey(), name.getDiaryDB());
                deleteItemFromFirebase(binClass.getKey(),position);
            }

            if ((binClass.getToken()).contains(name.getSchedulerDB())){
                copyData(binClass.getKey(), name.getSchedulerDB());
                deleteItemFromFirebase(binClass.getKey(),position);
            }

            if ((binClass.getToken()).contains(name.getTodoDB())){
                copyData(binClass.getKey(), name.getTodoDB());
                deleteItemFromFirebase(binClass.getKey(),position);
            }
        });

        holder.firstBinLayout.setOnClickListener(v -> {
            if (dropStatus){
                holder.secondBinLayout.setVisibility(View.VISIBLE);
                holder.status.setImageResource(R.drawable.up);
                dropStatus = false;
            }else{
                holder.secondBinLayout.setVisibility(View.GONE);
                holder.status.setImageResource(R.drawable.drop);
                dropStatus = true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
    void copyData(String key,String targetToken) {
        DatabaseReference targetRef = FirebaseDatabase.getInstance().getReference(targetToken).child(key);
        DatabaseReference sourceRef = FirebaseDatabase.getInstance().getReference("binDB").child(key);

        // Read data from the source node
        sourceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    Object data = dataSnapshot.getValue();

                    targetRef.setValue(data)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(context, "Data copied successfully.", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(aVoid -> {
                                Toast.makeText(context, "Failure.", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    System.out.println("Source data not found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.err.println("Error: " + databaseError.getMessage());
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    void deleteItemFromFirebase(String key, int position) {
        try{
            if (!key.isBlank() && !key.isEmpty()){
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("binDB");
                databaseReference.child(key).removeValue()
                        .addOnSuccessListener(aVoid -> {
                            if (position >= 0 && position < taskList.size()) {
                                taskList.remove(position);
                                new BinAdapter().notifyDataSetChanged();
                                Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e("RecyclerViewError", "Invalid position: " + position);
                            }
                        })
                        .addOnFailureListener(e -> {
                            // Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            System.err.println("Failed to delete data: " + e.getMessage());
                        });
            }else {
                Toast.makeText(context, "key is empty!", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(context, "de"+e, Toast.LENGTH_SHORT).show();
        }
    }


}
