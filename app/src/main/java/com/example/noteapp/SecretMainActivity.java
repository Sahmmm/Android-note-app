package com.example.noteapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class SecretMainActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> modifyPageLauncher;
    private ImageView secretMenuReturn;
    private PageRepository pageRepository;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret_main);

        secretMenuReturn = findViewById(R.id.secretMenuReturn);
        pageRepository = new PageRepository(this);


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
        LinearLayout container = findViewById(R.id.componentContainer);
        container.removeAllViews();

        List<Page> pages = pageRepository.loadSecretPages();
        for (Page p : pages) {
            container.addView(createItem(p));
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
