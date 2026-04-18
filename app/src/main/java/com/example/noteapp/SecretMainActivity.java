package com.example.noteapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SecretMainActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> modifyPageLauncher;
    private ImageView secretMenuReturn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret_main);

        secretMenuReturn = findViewById(R.id.secretMenuReturn);


        refreshPageList();

        modifyPageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        refreshPageList();
                    }
                }
        );

        secretMenuReturn.setOnClickListener(v->{
            setResult(RESULT_OK); // ✅ Indique que tout s’est bien passé
            finish();
        });



    }

    private void refreshPageList() {
        File file = new File(getFilesDir(), "pagesSecret.json");
        if (!file.exists()) {
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

            Intent intent = new Intent(SecretMainActivity.this, ModifyPageActivity.class);
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
