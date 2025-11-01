package com.gajendra.nadiya;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MyDiaryActivity extends AppCompatActivity {

    LinearLayout read_diary,write_diary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_diary);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        write_diary = findViewById(R.id.write_diary);
        read_diary = findViewById(R.id.read_diary);

        write_diary.setOnClickListener(v -> startActivity(new Intent(MyDiaryActivity.this,WriteDiaryActivity.class)));
        read_diary.setOnClickListener(v -> startActivity(new Intent(MyDiaryActivity.this,ReadDiaryActivity.class)));
    }
}