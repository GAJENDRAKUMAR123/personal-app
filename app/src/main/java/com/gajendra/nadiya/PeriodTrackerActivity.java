package com.gajendra.nadiya;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gajendra.nadiya.adapter.PeriodsTrackerAdapter;
import com.gajendra.nadiya.classes.DBNames;
import com.gajendra.nadiya.classes.PTrackerDataClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PeriodTrackerActivity extends AppCompatActivity implements PeriodsTrackerAdapter.OnItemClickListener{

    ImageView addDate;
    RecyclerView periodTrackerRV;
    List<PTrackerDataClass> list;
    PeriodsTrackerAdapter periodsTrackerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_period_tracker);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addDate = findViewById(R.id.addPeriodsDate);
        periodTrackerRV = findViewById(R.id.periodTrackerRV);
        periodTrackerRV = findViewById(R.id.periodTrackerRV);
        periodTrackerRV.setLayoutManager(new LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                true)
        );
        list = new ArrayList<>();
        periodsTrackerAdapter = new PeriodsTrackerAdapter(this,list, this);
        periodTrackerRV.setAdapter(periodsTrackerAdapter);
        fetchData();
        addDate.setOnClickListener(v -> {
            try{
                Calendar c = Calendar.getInstance();
                int currentYear = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // Open DatePickerDialog to select the date
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        PeriodTrackerActivity.this,
                        (view, year, monthOfYear, dayOfMonth) -> {
                            // Update the text with the selected date
                            String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                            //chosenDate.setText(date);
                            saveSharedData(date,getMonth(date));
                        },
                        currentYear,
                        month,
                        day
                );
                datePickerDialog.show();
            } catch (Exception e) {
                Toast.makeText(PeriodTrackerActivity.this, "c"+e, Toast.LENGTH_LONG).show();
            }
        });
    }
    String getMonth(String currentDate) {
        String[] myArray = currentDate.split("/");

        // Parse the month (second element in DD/MM/YYYY format)
        int month = Integer.parseInt(myArray[1]);

        switch (month) {
            case 1: return "January";
            case 2: return "February";
            case 3: return "March";
            case 4: return "April";
            case 5: return "May";
            case 6: return "June";
            case 7: return "July";
            case 8: return "August";
            case 9: return "September";
            case 10: return "October";
            case 11: return "November";
            case 12: return "December";
            default: return "Invalid Month";
        }
    }

    public void saveSharedData(String month,String date) {
        try {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            DatabaseReference database = FirebaseDatabase.getInstance().getReference(new DBNames().getPtDB());

            if (mAuth.getCurrentUser() != null) {
                String userId = mAuth.getCurrentUser().getUid();
                String key = database.push().getKey(); // Generate a unique key for the data

                assert key != null;
                PTrackerDataClass pTrackerDataClass = new PTrackerDataClass(userId, date, month);
                database.child(key).setValue(pTrackerDataClass)
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

    void fetchData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference targetDatesRef = database.getReference(new DBNames().getPtDB());

        targetDatesRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try{
                    list.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String key = snapshot.getKey();
                        PTrackerDataClass pTrackerDataClass = snapshot.getValue(PTrackerDataClass.class);
                        assert pTrackerDataClass != null;
                        pTrackerDataClass.setKey(key);
                        list.add(pTrackerDataClass);
                    }
                    periodsTrackerAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Toast.makeText(PeriodTrackerActivity.this, e+"", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PeriodTrackerActivity.this, "Error fetching data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onItemClick(PTrackerDataClass pTrackerDataClass, int position) {
        Toast.makeText(this, pTrackerDataClass.getCreate_at()+" - "+pTrackerDataClass.getMonth(), Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onItemLongClick(PTrackerDataClass pTrackerDataClass, int position) {
        copyData(pTrackerDataClass.getKey());
        deleteItemFromFirebase(pTrackerDataClass.getKey(),position);
        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void deleteItemFromFirebase(String key, int position) {
        try{
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(new DBNames().getPtDB());
            databaseReference.child(key).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        if (position >= 0 && position < list.size()) {
                            list.remove(position);
                            periodsTrackerAdapter.notifyDataSetChanged();
                        } else {
                            Log.e("RecyclerViewError", "Invalid position: " + position);
                        }
                    })
                    .addOnFailureListener(Throwable::printStackTrace);
        }catch (Exception e){
            Toast.makeText(this, "de"+e, Toast.LENGTH_SHORT).show();
        }
    }

    void copyData(String key) {
        DatabaseReference targetRef = FirebaseDatabase.getInstance().getReference(new DBNames().getBin()).child(key);
        DatabaseReference sourceRef = FirebaseDatabase.getInstance().getReference(new DBNames().getPtDB()).child(key);

        // Read data from the source node
        sourceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                    if (data == null) {
                        data = new HashMap<>();
                    }

                    data.put("token", "deleted from " + new DBNames().getPtDB());

                    //Object data = dataSnapshot.getValue();
                    targetRef.setValue(data)
                            .addOnSuccessListener(aVoid -> {
                                //Toast.makeText(ReadDiaryActivity.this, "Data copied successfully.", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(aVoid -> {
                                Toast.makeText(PeriodTrackerActivity.this, "Failure.", Toast.LENGTH_SHORT).show();
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
