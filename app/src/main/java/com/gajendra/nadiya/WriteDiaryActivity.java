package com.gajendra.nadiya;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.gajendra.nadiya.classes.DBNames;
import com.gajendra.nadiya.classes.DiaryDataClass;
import com.gajendra.nadiya.classes.TODOTasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class WriteDiaryActivity extends AppCompatActivity {

    EditText editTextTitle,editTextStory;
    TextView header_title,header_story,top_header;
    CardView submitButton,backButton;
    Intent i;
    String title,story;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_write_diary);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextStory = findViewById(R.id.editTextStory);
        submitButton = findViewById(R.id.submitButton);
        header_title = findViewById(R.id.header_title);
        header_story = findViewById(R.id.header_story);
        top_header = findViewById(R.id.top_header);
        backButton = findViewById(R.id.backButton);

        //read functionality
        try {
            i = getIntent();
            title = i.getStringExtra("title");
            story = i.getStringExtra("story");
            if (title != null && !title.isEmpty() && story != null && !story.isEmpty()){
                header_title.setText(title);
                header_story.setText(story);
                top_header.setText(R.string.read_only);
                editTextStory.setVisibility(View.GONE);
                editTextTitle.setVisibility(View.GONE);
                submitButton.setVisibility(View.GONE);
                backButton.setVisibility(View.VISIBLE);
            }else {
                header_title.setText(R.string.title);
                header_story.setText(R.string.story);
                top_header.setText(R.string.write);
                submitButton.setVisibility(View.VISIBLE);
                backButton.setVisibility(View.GONE);

            }
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }

        //write functionality
        submitButton.setOnClickListener(v -> {
                saveSharedData(currentDate(), editTextTitle.getText().toString(), editTextStory.getText().toString());
                finish();
        });
        backButton.setOnClickListener(v -> {
            startActivity(new Intent(this, ReadDiaryActivity.class));
            finish();
        });

    }

    void saveSharedData(String currentDate, String title, String story) {
        try {
            if (!title.isEmpty() && !story.isEmpty()){
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                DatabaseReference database = FirebaseDatabase.getInstance().getReference(new DBNames().getDiaryDB());

                if (mAuth.getCurrentUser() != null) {
                    String userId = mAuth.getCurrentUser().getUid();
                    String key = database.push().getKey();

                    assert key != null;
                    DiaryDataClass diaryDataClass = new DiaryDataClass(title, story, currentDate, userId);
                    database.child(key).setValue(diaryDataClass)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(this, "Error saving data!", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }else {
                Toast.makeText(this, "empty", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "m:"+e, Toast.LENGTH_LONG).show();
        }
    }
    String currentDate() {
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return localDate.format(formatter);
    }
}