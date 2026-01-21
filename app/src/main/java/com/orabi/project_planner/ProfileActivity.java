package com.orabi.project_planner;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageButton btnBack = findViewById(R.id.btnBack);
        EditText etUserName = findViewById(R.id.etUserName);
        ImageButton btnSaveName = findViewById(R.id.btnSaveName);
        TextView tvUserName = findViewById(R.id.tvUserName);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 2. زر حفظ الاسم (العلامة ✓)
        btnSaveName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newName = etUserName.getText().toString().trim();


                if (newName.isEmpty()) {
                    Toast.makeText(ProfileActivity.this, "يرجى إدخال اسم", Toast.LENGTH_SHORT).show();
                    return;
                }


                tvUserName.setText(newName.toUpperCase());


                etUserName.setText("");


                Toast.makeText(ProfileActivity.this, "تم تحديث الاسم", Toast.LENGTH_SHORT).show();
            }
        });
    }
}