package com.example.noteapp;

import android.content.Intent;
import android.os.Bundle;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton btnCreatePage;
    private ActivityResultLauncher<Intent> modifyPageLauncher, secretMainLauncher;
    private ImageView menuIcon;
    private TextView toolbarTitle;
    private int clickSecretCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnCreatePage = findViewById(R.id.btnCreatePage);
        menuIcon = findViewById(R.id.menuIcon);
        toolbarTitle = findViewById(R.id.toolbarTitle);


        refreshPageList();

        modifyPageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        refreshPageList();
                    }
                }
        );

        secretMainLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        refreshPageList();
                    }
                }
        );

        btnCreatePage.setOnClickListener(view -> {
            // Désactiver le bouton
            btnCreatePage.setEnabled(false);

            CreatePageDialogFragment dialog = new CreatePageDialogFragment(this::refreshPageList);
            dialog.show(getSupportFragmentManager(), "CreatePageDialog");

            // Réactiver après un délai
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                btnCreatePage.setEnabled(true);
            }, 1000); // 1 seconde
        });

        menuIcon.setOnClickListener(view -> {
            menuIcon.setEnabled(false);

            MenuDialogFragment menu = new MenuDialogFragment();
            menu.show(getSupportFragmentManager(), "MenuDialog");

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                menuIcon.setEnabled(true);
            }, 800);
        });

        clickSecretCount = 0;
        toolbarTitle.setOnClickListener( view -> {
            clickSecretCount++;
            if(clickSecretCount >= 14){
                clickSecretCount = 0;
                Toast.makeText(this, "Pages secrètes trouvées", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, SecretMainActivity.class);
                secretMainLauncher.launch(intent);
            }

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