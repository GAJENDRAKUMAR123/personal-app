package com.gajendra.nadiya;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gajendra.nadiya.adapter.SchedulerAdapter;
import com.gajendra.nadiya.classes.DBNames;
import com.gajendra.nadiya.classes.SchedulerData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DailyScheduler extends AppCompatActivity implements SchedulerAdapter.OnItemClickListener{

    EditText scheduledTaskTitle,scheduledTaskDesc;
    TextView schedulerStartTime, schedulerEndTime,submitSchedulerTask;
    ImageView schedulerStartDateIcon,schedulerEndDateIcon;
    RecyclerView recyclerView;
    SchedulerAdapter adapter;
    List<SchedulerData> taskList;
   // DatabaseHelper databaseHelper;


    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_daily_schedular);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (!isBatteryOptimizationIgnored()) {
            requestIgnoreBatteryOptimizations();
        }

            scheduledTaskTitle = findViewById(R.id.scheduledTaskTitle);
            scheduledTaskDesc = findViewById(R.id.scheduledTaskDesc);
            schedulerStartTime = findViewById(R.id.schedulerStartDate);
            schedulerEndTime = findViewById(R.id.schedulerEndDate);
            submitSchedulerTask = findViewById(R.id.submitSchedulerTask);

            schedulerStartDateIcon = findViewById(R.id.schedulerStartDateIcon);
            schedulerEndDateIcon = findViewById(R.id.schedulerEndDateIcon);

            recyclerView = findViewById(R.id.dailySchedulerRV);
            taskList = new ArrayList<>();

            // Set up RecyclerView
            adapter = new SchedulerAdapter(this, taskList,this);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);

            submitSchedulerTask.setOnClickListener(v -> addItem());
            Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

        schedulerStartDateIcon.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    (view, hourOfDay, minute1) -> schedulerStartTime.setText(hourOfDay + ":" + minute1),
                    hour,
                    minute,
                    false);
            timePickerDialog.show();
        });

        schedulerEndDateIcon.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    (view, hourOfDay, minute2) -> schedulerEndTime.setText(hourOfDay + ":" + minute2),
                    hour,
                    minute,
                    false);
            timePickerDialog.show();

        });

        //fetch data
        fetchData();
    }

    @SuppressLint("NotifyDataSetChanged")
    void addItem(){
        String taskTitle = scheduledTaskTitle.getText().toString().trim();
        String taskDesc = scheduledTaskDesc.getText().toString().trim();
        if (!taskTitle.isEmpty() && !taskDesc.isEmpty()) {
            String currentDate = currentDate();
            //add data
            saveSharedData(
                    taskTitle,
                    taskDesc,
                    currentDate,
                    schedulerStartTime.getText().toString(),
                    schedulerEndTime.getText().toString()
            );
                adapter.notifyDataSetChanged();
                scheduledTaskTitle.setText("");
                scheduledTaskDesc.setText("");
                schedulerStartTime.setText(R.string.select_start_time);
                schedulerEndTime.setText(R.string.select_end_time);
                Toast.makeText(this, "Task added!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to insert task into database", Toast.LENGTH_SHORT).show();
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onItemLongClick(SchedulerData SchedulerData, int position) {
        try {
            copyData(SchedulerData.getKey());
            deleteItemFromFirebase(SchedulerData.getKey(), position);
        } catch (Exception e) {
            Toast.makeText(this, "dSys:" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    void saveSharedData(String title,String desc, String currentDate, String startDate, String endDate) {
        try {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            DatabaseReference database = FirebaseDatabase.getInstance().getReference(new DBNames().getSchedulerDB());

            if (mAuth.getCurrentUser() != null) {
                String userId = mAuth.getCurrentUser().getUid();
                String key = database.push().getKey(); // Generate a unique key for the data

                assert key != null;
                SchedulerData sharedData = new SchedulerData(title,desc,currentDate,startDate,endDate);
                database.child(key).setValue(sharedData)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Error saving data!", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        } catch (Exception e) {
            Toast.makeText(this, "m:"+e, Toast.LENGTH_LONG).show();
        }
    }
    boolean isBatteryOptimizationIgnored() {
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        return powerManager.isIgnoringBatteryOptimizations(getPackageName());
    }
    void requestIgnoreBatteryOptimizations() {
        @SuppressLint("BatteryLife") Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }
    String currentDate() {
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return localDate.format(formatter);

    }
    @SuppressLint("NotifyDataSetChanged")
    void deleteItemFromFirebase(String key, int position) {
        try{
            if (!key.isBlank() && !key.isEmpty()){
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(new DBNames().getSchedulerDB());
                databaseReference.child(key).removeValue()
                        .addOnSuccessListener(aVoid -> {
                            if (position >= 0 && position < taskList.size()) {
                                taskList.remove(position);
                                adapter.notifyDataSetChanged();
                                Toast.makeText(this, "Deleted!", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e("RecyclerViewError", "Invalid position: " + position);
                            }
                        })
                        .addOnFailureListener(e -> {
                            // Failure callback
                            Toast.makeText(DailyScheduler.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            System.err.println("Failed to delete data: " + e.getMessage());
                        });
            }else {
                Toast.makeText(this, "key is empty!", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(this, "de"+e, Toast.LENGTH_SHORT).show();
        }
    }
    void fetchData() {
        DatabaseReference targetDatesRef = FirebaseDatabase.getInstance().getReference(new DBNames().getSchedulerDB());
        targetDatesRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                taskList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    SchedulerData schedulerData = snapshot.getValue(SchedulerData.class);
                    assert schedulerData != null;
                    schedulerData.setKey(key);
                    taskList.add(schedulerData);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DailyScheduler.this, "Error fetching data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    void copyData(String key) {
        DatabaseReference targetRef = FirebaseDatabase.getInstance().getReference(new DBNames().getBin()).child(key);
        DatabaseReference sourceRef = FirebaseDatabase.getInstance().getReference(new DBNames().getSchedulerDB()).child(key);

        // Read data from the source node
        sourceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                    if (data == null) {
                        data = new HashMap<>();
                    }

                    data.put("token", "deleted from " + new DBNames().getSchedulerDB());

                    //Object data = dataSnapshot.getValue();
                    targetRef.setValue(data)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(DailyScheduler.this, "Data copied successfully.", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(aVoid -> {
                                Toast.makeText(DailyScheduler.this, "Failure.", Toast.LENGTH_SHORT).show();
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
}
