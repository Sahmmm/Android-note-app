package com.example.noteapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.noteapp.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView pageListView;
    private FloatingActionButton btnCreatePage;
    private ArrayList<Page> pages;

    private TextView tvPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCreatePage = findViewById(R.id.btnCreatePage);
        tvPages = findViewById(R.id.tvPages);
        loadPagesFromJson();

        ActivityResultLauncher<Intent> createPageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        loadPagesFromJson();
                    }
                }
        );

        btnCreatePage.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, CreatePageActivity.class);
            createPageLauncher.launch(intent);

        });
    }

    private void loadPagesFromJson() {
        File file = new File(getFilesDir(), "pages.json");
        ObjectMapper om = new ObjectMapper();
        try {
            List<Page> verifyPages = om.readValue(file, new TypeReference<List<Page>>() {});

            StringBuilder display = new StringBuilder();
            for (Page p : verifyPages) {
                display.append("📄 ").append(p.getTitle()).append("\n")
                        .append("    ").append(p.getContent()).append("\n\n");
            }
            tvPages.setText(display.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}