package com.example.noteapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.UUID;

public class CreatePageActivity extends AppCompatActivity {

    private EditText titleInput, contentInput;
    private Button saveButton;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_page);

        titleInput = findViewById(R.id.titleInput);
        contentInput = findViewById(R.id.contentInput);
        saveButton = findViewById(R.id.saveButton);

        sharedPreferences = getSharedPreferences("MyPages", MODE_PRIVATE);

        saveButton.setOnClickListener(view -> {
            String title = titleInput.getText().toString();
            String content = contentInput.getText().toString();

            if (!title.isEmpty()) {

                String id = UUID.randomUUID().toString(); // ID unique
                Page newpage = new Page(id, title, content);
                pages.add(newpage);
                finish(); // Retour à la MainActivity

            } else {
                Toast.makeText(this, "Le titre est requis", Toast.LENGTH_SHORT).show();
            }
        });
    }
}