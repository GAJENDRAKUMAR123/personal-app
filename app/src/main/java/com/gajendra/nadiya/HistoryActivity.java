package com.gajendra.nadiya;

import android.annotation.SuppressLint;
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

import com.gajendra.nadiya.adapter.BinAdapter;
import com.gajendra.nadiya.classes.BinClass;
import com.gajendra.nadiya.classes.DBNames;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity implements BinAdapter.OnItemClickListener{

    RecyclerView recyclerView;
    BinAdapter binAdapter;
    List<BinClass> list;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.historyRV);
        list = new ArrayList<>();
        binAdapter = new BinAdapter(this,list,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(binAdapter);
        binAdapter.notifyDataSetChanged();

        fetchData();
    }

    @Override
    public void onItemClick(BinClass diaryDataClass, int position) {

    }

    @Override
    public void onItemLongClick(BinClass diaryDataClass, int position) {
        deleteItemFromFirebase(diaryDataClass.getKey(),position);
    }

    void fetchData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference targetDatesRef = database.getReference(new DBNames().getBin());

        targetDatesRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try{
                    list.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String key = snapshot.getKey();
                        BinClass binClass = snapshot.getValue(BinClass.class);
                        assert binClass != null;
                        binClass.setKey(key);
                        list.add(binClass);
                    }
                    binAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Toast.makeText(HistoryActivity.this, e+"", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HistoryActivity.this, "Error fetching data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @SuppressLint("NotifyDataSetChanged")
    void deleteItemFromFirebase(String key, int position) {
        try{
            if (!key.isBlank() && !key.isEmpty()){
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(new DBNames().getBin());
                databaseReference.child(key).removeValue()
                        .addOnSuccessListener(aVoid -> {
                            if (position >= 0 && position < list.size()) {
                                list.remove(position);
                                Toast.makeText(this, "Deleted!", Toast.LENGTH_SHORT).show();
                                binAdapter.notifyDataSetChanged();
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