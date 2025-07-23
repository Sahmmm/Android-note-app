package com.example.noteapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.io.File;


public class CreatePageActivity extends AppCompatActivity {

    private EditText titleInput, editTextIcon;
    private ImageButton saveButton;
    private CheckBox checkSecret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_page);

        titleInput = findViewById(R.id.titleInput);
        editTextIcon = findViewById(R.id.editTextIcon);
        saveButton = findViewById(R.id.saveButton);
        checkSecret = findViewById(R.id.checkSecret);

        saveButton.setOnClickListener(view -> {
            String title = titleInput.getText().toString();
            String icon = editTextIcon.getText().toString();

            if (!title.isEmpty()) {

                File file = new File(this.getFilesDir(), "pages.json");
                ObjectMapper om = new ObjectMapper();
                List<Page> pages = new ArrayList<>();

                // Étape 1 : charger les anciennes pages si le fichier existe
                if (file.exists()) {
                    try {
                        pages = om.readValue(file, new TypeReference<List<Page>>() {});
                    } catch (IOException e) {
                        e.printStackTrace(); // gestion d'erreur en lecture
                    }
                }

                // Étape 2 : ajouter la nouvelle page
                String id = UUID.randomUUID().toString(); // ID unique
                Page newpage = new Page(id, title, icon, "@color/bluePastel");
                pages.add(newpage);

                // Étape 3 : réécrire la liste complète
                try {
                    om.writeValue(file, pages);
                } catch (IOException e) {
                    e.printStackTrace(); // gestion d'erreur en écriture
                }

                setResult(RESULT_OK);
                finish(); // Retour à la MainActivity

            } else {
                Toast.makeText(this, "Le titre est requis", Toast.LENGTH_SHORT).show();
            }
        });
    }
}