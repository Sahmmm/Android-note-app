package com.example.noteapp.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.noteapp.R;
import com.example.noteapp.data.PageStorage;
import com.example.noteapp.model.Page;
import com.example.noteapp.ui.common.PageListRenderer;
import com.example.noteapp.ui.editor.ModifyPageActivity;

import java.io.IOException;
import java.util.List;

public class SecretMainActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> modifyPageLauncher;
    private ImageView secretMenuReturn;
    private PageStorage pageStorage;
    private PageListRenderer pageListRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret_main);

        secretMenuReturn = findViewById(R.id.secretMenuReturn);
        LinearLayout componentContainer = findViewById(R.id.componentContainer);

        pageStorage = new PageStorage(this);
        pageListRenderer = new PageListRenderer(this, componentContainer);

        refreshPageList();

        modifyPageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        refreshPageList();
                    }
                }
        );

        secretMenuReturn.setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });
    }

    private void refreshPageList() {
        if (!pageStorage.hasPagesFile(true)) {
            return;
        }

        pageListRenderer.clear();

        try {
            List<Page> pages = pageStorage.loadPages(true);
            System.out.println("Pages chargées : " + pages.size());
            pageListRenderer.render(pages, this::openModifyPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openModifyPage(Page clickedPage) {
        Intent intent = new Intent(SecretMainActivity.this, ModifyPageActivity.class);
        intent.putExtra("page", clickedPage);
        modifyPageLauncher.launch(intent);
    }
}
