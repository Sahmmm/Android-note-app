package com.example.noteapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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
                Page newpage = new Page(id, title, content);
                pages.add(newpage);

                // Étape 3 : réécrire la liste complète
                try {
                    om.writeValue(file, pages);
                } catch (IOException e) {
                    e.printStackTrace(); // gestion d'erreur en écriture
                }
//                // etape 4 : print
//                try {
//                    List<Page> verifyPages = om.readValue(file, new TypeReference<List<Page>>() {});
//                    System.out.println("Contenu final de pages.json :");
//                    for (Page p : verifyPages) {
//                        System.out.println(p.getTitle() + " : " + p.getContent());
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                setResult(RESULT_OK);
                finish(); // Retour à la MainActivity

            } else {
                Toast.makeText(this, "Le titre est requis", Toast.LENGTH_SHORT).show();
            }
        });
    }
}