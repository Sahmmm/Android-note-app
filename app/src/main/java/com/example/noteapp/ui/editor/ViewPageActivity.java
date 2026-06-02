package com.example.noteapp.ui.editor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.noteapp.R;

public class ViewPageActivity extends AppCompatActivity {

    private TextView pageTitle, pageContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_page);

        pageTitle = findViewById(R.id.pageTitle);
        pageContent = findViewById(R.id.pageContent);

        Intent intent = getIntent();
        pageTitle.setText(intent.getStringExtra("title"));
        pageContent.setText(intent.getStringExtra("content"));
    }
}
