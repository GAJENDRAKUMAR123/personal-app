package com.gajendra.nadiya;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gajendra.nadiya.adapter.ToDoAdapter;
import com.gajendra.nadiya.classes.DBNames;
import com.gajendra.nadiya.classes.TODOTasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TodoList extends AppCompatActivity implements ToDoAdapter.OnItemClickListener{

     EditText editTextTask;
     TextView buttonAdd;
     RecyclerView recyclerView;
     List<TODOTasks> list;
     ToDoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        try {
            editTextTask = findViewById(R.id.editTextTask);
            buttonAdd = findViewById(R.id.buttonAdd);
            recyclerView = findViewById(R.id.listViewTasks);

            list = new ArrayList<>();
            adapter = new ToDoAdapter(this, list,this);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);


            //add task
            buttonAdd.setOnClickListener(v -> {
                //add item
                currentDate();
                String task = editTextTask.getText().toString();
                addTodoItem(
                        task,
                        currentDate(),
                        new DBNames().getTodoDB()
                );
            });

            //fetch data
            fetchData(
            );
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(int position) {

    }
    @Override
    public void onItemLongClick(TODOTasks todoTasks, int position) {
        copyData(
                todoTasks.getKey(),
                new DBNames().getBin(),
                new DBNames().getTodoDB()
        );

        deleteItemFromFirebase(
                todoTasks.getKey(),
                position
        );
    }
    void addTodoItem(String todoTask, String date, String reference) {
        try {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            DatabaseReference database = FirebaseDatabase.getInstance().getReference(reference);

            if (mAuth.getCurrentUser() != null) {
                String userId = mAuth.getCurrentUser().getUid();
                String key = database.push().getKey(); //Generate a unique key for the data

                assert key != null;
                TODOTasks todoTasks = new TODOTasks(todoTask,date);
                database.child(key).setValue(todoTasks)
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
        DatabaseReference targetDatesRef = FirebaseDatabase.getInstance().getReference(new DBNames().getTodoDB());
        targetDatesRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    TODOTasks data = snapshot.getValue(TODOTasks.class);
                    assert data != null;
                    data.setKey(key);
                    list.add(data);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TodoList.this, "Error fetching data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    void copyData(String key, String targetStr, String sourceStr) {
        DatabaseReference targetRef = FirebaseDatabase.getInstance().getReference(targetStr).child(key);
        DatabaseReference sourceRef = FirebaseDatabase.getInstance().getReference(sourceStr).child(key);

        // Read data from the source node
        sourceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                    if (data == null) {
                        data = new HashMap<>();
                    }

                    data.put("token", "deleted from todoDB");

                    //Object data = dataSnapshot.getValue();
                    targetRef.setValue(data)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(TodoList.this, "Data copied successfully.", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(aVoid -> {
                                Toast.makeText(TodoList.this, "Failure.", Toast.LENGTH_SHORT).show();
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
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(new DBNames().getTodoDB());
                databaseReference.child(key).removeValue()
                        .addOnSuccessListener(aVoid -> {
                            if (position >= 0 && position < list.size()) {
                                list.remove(position);
                                adapter.notifyDataSetChanged();
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
    String currentDate() {
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return localDate.format(formatter);
    }
}
