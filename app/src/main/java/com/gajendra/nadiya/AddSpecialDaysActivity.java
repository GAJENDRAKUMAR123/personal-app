package com.gajendra.nadiya;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.gajendra.nadiya.classes.DBNames;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class AddSpecialDaysActivity extends AppCompatActivity {

    EditText etAddSpecialDaysTitle;
    LinearLayout chooseSpecialDate;
    TextView chosenDate,addSpecialDayBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_special_days);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etAddSpecialDaysTitle = findViewById(R.id.etAddSpecialDaysTitle);
        chooseSpecialDate = findViewById(R.id.chooseSpecialDate);
        chosenDate = findViewById(R.id.chosenDate);
        addSpecialDayBtn  = findViewById(R.id.addSpecialDayBtn);

        chooseSpecialDate.setOnClickListener(v -> {

            Calendar c = Calendar.getInstance();
            int currentYear = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Open DatePickerDialog to select the date
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        // Update the text with the selected date
                        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        chosenDate.setText(date);
                    },
                    currentYear,
                    month,
                    day
            );
            datePickerDialog.show();
        });

        addSpecialDayBtn.setOnClickListener(v -> {
            String title = etAddSpecialDaysTitle.getText().toString();
            String date = chosenDate.getText().toString();
            if (!title.isEmpty() && !date.isEmpty()){
                saveSharedData(title, date);
            }else {
                Toast.makeText(this, "Pagal ho kya?", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void saveSharedData(String content,String create_at) {
        try {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            DatabaseReference database = FirebaseDatabase.getInstance().getReference(new DBNames().getMemoriesDB());

            if (mAuth.getCurrentUser() != null) {
                String userId = mAuth.getCurrentUser().getUid();
                String key = database.push().getKey(); // Generate a unique key for the data

                SharedData sharedData = new SharedData(userId, content, create_at);
                assert key != null;
                database.child(key).setValue(sharedData)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                             Toast.makeText(this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
                             finish();
                            } else {
                            Toast.makeText(this, "Error saving data!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
            }
        } catch (Exception e) {
            Toast.makeText(this, e+"-", Toast.LENGTH_LONG).show();
        }
    }
}