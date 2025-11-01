package com.gajendra.nadiya;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gajendra.nadiya.adapter.MyDiaryAdapter;
import com.gajendra.nadiya.classes.DBNames;
import com.gajendra.nadiya.classes.DiaryDataClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReadDiaryActivity extends AppCompatActivity implements MyDiaryAdapter.OnItemClickListener{
    RecyclerView myDiaryRV;
    List<DiaryDataClass> list;
    MyDiaryAdapter myDiaryAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_read_diary);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        myDiaryRV = findViewById(R.id.myDiaryRV);
        list = new ArrayList<>();
        myDiaryAdapter = new MyDiaryAdapter(this,list,this);
        myDiaryRV.setAdapter(myDiaryAdapter);
        myDiaryRV.setLayoutManager(new LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                true)
        );
        fetchData();
    }

    @Override
    public void onItemClick(DiaryDataClass diaryDataClass, int position) {
        Intent i = new Intent(this,WriteDiaryActivity.class);
        i.putExtra("title", diaryDataClass.getTitle());
        i.putExtra("story", diaryDataClass.getStory());
        startActivity(i);
    }
    @Override
    public void onItemLongClick(DiaryDataClass diaryDataClass, int position) {
        copyData(diaryDataClass.getKey());
        deleteItemFromFirebase(diaryDataClass.getKey(),position);
    }
    void fetchData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference targetDatesRef = database.getReference(new DBNames().getDiaryDB());

        targetDatesRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try{
                    list.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String key = snapshot.getKey();
                        DiaryDataClass diaryDataClass = snapshot.getValue(DiaryDataClass.class);
                        assert diaryDataClass != null;
                        diaryDataClass.setKey(key);
                        list.add(diaryDataClass);
                    }
                    myDiaryAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Toast.makeText(ReadDiaryActivity.this, e+"", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ReadDiaryActivity.this, "Error fetching data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    void copyData(String key) {
        DatabaseReference targetRef = FirebaseDatabase.getInstance().getReference(new DBNames().getBin()).child(key);
        DatabaseReference sourceRef = FirebaseDatabase.getInstance().getReference(new DBNames().getDiaryDB()).child(key);

        // Read data from the source nod
        sourceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                    if (data == null) {
                        data = new HashMap<>();
                    }

                    data.put("token", "deleted from " + new DBNames().getDiaryDB());

                    //Object data = dataSnapshot.getValue();
                    targetRef.setValue(data)
                            .addOnSuccessListener(aVoid -> {
                                //Toast.makeText(ReadDiaryActivity.this, "Data copied successfully.", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(aVoid -> {
                                Toast.makeText(ReadDiaryActivity.this, "Failure.", Toast.LENGTH_SHORT).show();
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
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(new DBNames().getDiaryDB());
                databaseReference.child(key).removeValue()
                        .addOnSuccessListener(aVoid -> {
                            if (position >= 0 && position < list.size()) {
                                list.remove(position);
                                myDiaryAdapter.notifyDataSetChanged();
                                Toast.makeText(this, "Deleted!", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e("RecyclerViewError", "Invalid position: " + position);
                            }
                        })
                        .addOnFailureListener(e -> {
                            // Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            System.err.println("Failed to delete data: " + e.getMessage());
                        });
            }else {
                Toast.makeText(this, "key is empty!", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(this, "de"+e, Toast.LENGTH_SHORT).show();
        }
    }
}