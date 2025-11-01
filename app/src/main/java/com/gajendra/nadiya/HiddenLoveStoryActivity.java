package com.gajendra.nadiya;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gajendra.nadiya.adapter.OurStoryAdapter;

import java.util.ArrayList;
import java.util.List;

public class HiddenLoveStoryActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<SharedData> list;
    OurStoryAdapter ourStoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_hidden_love_story);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerView = findViewById(R.id.ourStoryRV);

        list = new ArrayList<>();
        list.add(new SharedData(R.drawable.page1));
        list.add(new SharedData(R.drawable.page2));
        list.add(new SharedData(R.drawable.page3));
        list.add(new SharedData(R.drawable.page4));
        list.add(new SharedData(R.drawable.page5));
        list.add(new SharedData(R.drawable.page6));
        list.add(new SharedData(R.drawable.page7));
        list.add(new SharedData(R.drawable.page8));
        list.add(new SharedData(R.drawable.page9));
        list.add(new SharedData(R.drawable.page10));
        list.add(new SharedData(R.drawable.page11));

        ourStoryAdapter = new OurStoryAdapter(this,list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(ourStoryAdapter);
    }
}