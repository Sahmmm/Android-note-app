package com.example.noteapp;

import android.content.Intent;
import android.os.Bundle;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;


import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton btnCreatePage;
    private ActivityResultLauncher<Intent> modifyPageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCreatePage = findViewById(R.id.btnCreatePage);

        refreshPageList();

        modifyPageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        refreshPageList();
                    }
                }
        );

        btnCreatePage.setOnClickListener(view -> {
            // Appelé quand le dialog se ferme après ajout
            CreatePageDialogFragment dialog = new CreatePageDialogFragment(this::refreshPageList);
            dialog.show(getSupportFragmentManager(), "CreatePageDialog");
        });

    }

    private void refreshPageList() {
        File file = new File(getFilesDir(), "pages.json");
        if (!file.exists()) {
            Toast.makeText(this, "pages.json introuvable", Toast.LENGTH_LONG).show();
            return;
        }
        ObjectMapper om = new ObjectMapper();

        // component de la liste
        LinearLayout container = findViewById(R.id.componentContainer);
        container.removeAllViews();

        try {
            List<Page> verifyPages = om.readValue(file, new TypeReference<List<Page>>() {});
            System.out.println("Pages chargées : " + verifyPages.size());
            for (Page p : verifyPages) {
                container.addView(createItem(p));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private PageItemComponent createItem(Page p){
        PageItemComponent item = new PageItemComponent(this);
        item.bind(p); // Liaison des données

        item.setOnClickListener(v -> {
            Page clickedPage = item.getPage();

            Intent intent = new Intent(MainActivity.this, ModifyPageActivity.class);
            intent.putExtra("page", clickedPage); // Envoie l'objet
            modifyPageLauncher.launch(intent);
        });

        item.setOnLongClickListener(v -> {
            // TODO
            return false;
        });

        return item;
    }
}