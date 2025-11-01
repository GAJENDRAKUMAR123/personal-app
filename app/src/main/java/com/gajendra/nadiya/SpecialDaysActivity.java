package com.gajendra.nadiya;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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

import com.gajendra.nadiya.adapter.SpecialDaysAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class SpecialDaysActivity extends AppCompatActivity implements SpecialDaysAdapter.OnItemClickListener {
    TextView total_days;
    ImageView addMemories;
    RecyclerView specialDaysRV;
    List<SharedData> list;
    SpecialDaysAdapter specialDaysAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_special_days);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        total_days = findViewById(R.id.total_days);
        addMemories = findViewById(R.id.addMemories);
        specialDaysRV = findViewById(R.id.specialDaysRV);

        //total_days.setText("0");

        addMemories.setOnClickListener(v -> {
            try{
                Intent i = new Intent(SpecialDaysActivity.this, AddSpecialDaysActivity.class);
                startActivity(i);
            }catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        specialDaysRV = findViewById(R.id.specialDaysRV);
        specialDaysRV.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        specialDaysAdapter = new SpecialDaysAdapter(this,list,this);
        specialDaysRV.setAdapter(specialDaysAdapter);

        // Fetch data from Firebase
        fetchData();
        specialDate();
    }
     void fetchData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference targetDatesRef = database.getReference("Data");

        targetDatesRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SharedData sharedData = snapshot.getValue(SharedData.class);
                    list.add(sharedData);
                }
                specialDaysAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SpecialDaysActivity.this, "Error fetching data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void specialDate(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference eventsRef = database.getReference("Data");

        // Query the database for the event with the specified title
        eventsRef.orderByChild("title").equalTo("Our Anniversary").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        SharedData event = snapshot.getValue(SharedData.class);

                        assert event != null;
                        total_days.setText(calculateDate(event.getCreate_at()));
                    }
                }
            }

            String calculateDate(String createAt) {
                String[] myArray = createAt.split("/");
                String day = myArray[0];
                String month = myArray[1];
                String year = myArray[2];

                LocalDate anniversary = LocalDate.of(
                        Integer.parseInt(year),
                        Integer.parseInt(month),
                        Integer.parseInt(day));

                long daysBetween = ChronoUnit.DAYS.between(anniversary, LocalDate.now());
                return String.valueOf(daysBetween);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(SpecialDaysActivity.this, "Error fetching data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "I Love U ❤️", Toast.LENGTH_LONG).show();
    }
}

