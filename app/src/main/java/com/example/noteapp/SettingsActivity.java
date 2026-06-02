package com.example.noteapp;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private TextView publicCountView, secretCountView, remindersCountView, listsCountView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ImageView backButton = findViewById(R.id.settingsBackButton);
        publicCountView = findViewById(R.id.publicCountView);
        secretCountView = findViewById(R.id.secretCountView);
        remindersCountView = findViewById(R.id.remindersCountView);
        listsCountView = findViewById(R.id.listsCountView);

        backButton.setOnClickListener(v -> finish());
        bindStats();
    }

    private void bindStats() {
        PageRepository pageRepository = new PageRepository(this);
        List<Page> publicPages = pageRepository.loadPublicPages();
        List<Page> secretPages = pageRepository.loadSecretPages();
        List<Page> allPages = new ArrayList<>();
        allPages.addAll(publicPages);
        allPages.addAll(secretPages);

        int reminders = 0;
        int lists = 0;
        for (Page page : allPages) {
            if (page.isReminder()) {
                reminders++;
            } else if (page.isList()) {
                lists++;
            }
        }

        publicCountView.setText(String.valueOf(publicPages.size()));
        secretCountView.setText(String.valueOf(secretPages.size()));
        remindersCountView.setText(String.valueOf(reminders));
        listsCountView.setText(String.valueOf(lists));
    }
}
