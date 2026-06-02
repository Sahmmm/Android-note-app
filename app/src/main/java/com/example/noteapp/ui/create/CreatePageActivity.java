package com.example.noteapp.ui.create;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.noteapp.R;
import com.example.noteapp.data.PageStorage;
import com.example.noteapp.model.Page;

import java.io.IOException;

public class CreatePageActivity extends AppCompatActivity {

    private EditText titleInput, editTextIcon;
    private ImageButton saveButton;
    private CheckBox checkSecret;
    private PageStorage pageStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_create_page);

        titleInput = findViewById(R.id.editTitleInput);
        editTextIcon = findViewById(R.id.editTextIcon);
        saveButton = findViewById(R.id.saveButton);
        checkSecret = findViewById(R.id.checkSecret);
        pageStorage = new PageStorage(this);

        saveButton.setOnClickListener(view -> {
            String title = titleInput.getText().toString();
            if (!title.isEmpty()) {
                createPage();
            } else {
                Toast.makeText(this, "Le titre est requis", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createPage() {
        String title = titleInput.getText().toString();
        String icon = editTextIcon.getText().toString();
        boolean isSecret = checkSecret.isChecked();

        Page newPage = pageStorage.createPage(title, icon, "@color/bluePastel", isSecret);
        try {
            pageStorage.addPage(newPage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        setResult(RESULT_OK);
        finish();
    }
}
