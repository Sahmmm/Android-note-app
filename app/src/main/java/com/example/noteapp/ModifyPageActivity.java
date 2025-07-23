package com.example.noteapp;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ModifyPageActivity extends AppCompatActivity {

    private ImageView imageReturnView, imageOptionsView;
    private EditText editTextContent;
    private TextView pageTitleView;
    private LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_page);

        imageReturnView = findViewById(R.id.imageReturnView);
        imageOptionsView = findViewById(R.id.imageOptionsView);
        editTextContent = findViewById(R.id.editTextContent);
        pageTitleView = findViewById(R.id.pageTitleView);
        mainLayout = findViewById(R.id.mainLayout);

        Page linkedPage = (Page) getIntent().getSerializableExtra("page");

        if ( linkedPage != null){
            pageTitleView.setText(linkedPage.getTitle());
            editTextContent.setText(linkedPage.getContent());
            mainLayout.setBackgroundColor(Color.parseColor(linkedPage.getColorFont()));
        }

        imageReturnView.setOnClickListener(view -> {
            saveChange(linkedPage);
            setResult(RESULT_OK); // ✅ Indique que tout s’est bien passé
            finish();
        });

        imageOptionsView.setOnClickListener(view -> {
            deletePage(linkedPage);
            setResult(RESULT_OK); // ✅ Indique que tout s’est bien passé
            finish();
        });

        View rootView = findViewById(R.id.constraintLayoutTitle); // met l'ID de ton ConstraintLayout

        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            int topInset = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            v.setPadding(8, topInset, 8, 12); // applique le padding supérieur
            return insets;
        });

    }

    private void saveChange(Page page) {
        File file = new File(getFilesDir(), "pages.json");
        ObjectMapper om = new ObjectMapper();
        List<Page> verifyPages = new ArrayList<>();

        try {
            verifyPages = om.readValue(file, new TypeReference<List<Page>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        String currentPageID = page.getId();
        for (int i = 0; i < verifyPages.size(); i++){

            if (verifyPages.get(i).getId().equals(currentPageID)) {
                // only if content get changed
                if(!verifyPages.get(i).getContent().equals(editTextContent.getText().toString().trim())){
                    verifyPages.get(i).setDate(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()));
                }
                verifyPages.get(i).setContent(editTextContent.getText().toString().trim());
                break;
            }
        }
        try {
            om.writeValue(file, verifyPages);
        } catch (IOException e) {
            e.printStackTrace(); // gestion d'erreur en écriture
        }
    }
    
    private void deletePage(Page page){
        File file = new File(getFilesDir(), "pages.json");
        ObjectMapper om = new ObjectMapper();
        List<Page> verifyPages = new ArrayList<>();
        try {
            verifyPages = om.readValue(file, new TypeReference<List<Page>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        String currentPageID = page.getId();
        for (int i = 0; i < verifyPages.size(); i++){               // suppression de la page
            if (verifyPages.get(i).getId().equals(currentPageID)) {
                verifyPages.remove(i);
                break;
            }
        }
        try {
            om.writeValue(file, verifyPages);
        } catch (IOException e) {
            e.printStackTrace(); // gestion d'erreur en écriture
        }
    }
}
