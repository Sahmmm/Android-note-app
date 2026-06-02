package com.example.noteapp.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.noteapp.R;
import com.example.noteapp.data.PageStorage;
import com.example.noteapp.model.Page;
import com.example.noteapp.ui.common.PageListRenderer;
import com.example.noteapp.ui.create.CreatePageDialogFragment;
import com.example.noteapp.ui.editor.ModifyPageActivity;
import com.example.noteapp.ui.menu.MenuDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton btnCreatePage;
    private ActivityResultLauncher<Intent> modifyPageLauncher, secretMainLauncher;
    private ImageView menuIcon;
    private TextView toolbarTitle;
    private int clickSecretCount;
    private PageStorage pageStorage;
    private PageListRenderer pageListRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCreatePage = findViewById(R.id.btnCreatePage);
        menuIcon = findViewById(R.id.menuIcon);
        toolbarTitle = findViewById(R.id.toolbarTitle);
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

        secretMainLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        refreshPageList();
                    }
                }
        );

        btnCreatePage.setOnClickListener(view -> {
            btnCreatePage.setEnabled(false);

            CreatePageDialogFragment dialog = new CreatePageDialogFragment(this::refreshPageList);
            dialog.show(getSupportFragmentManager(), "CreatePageDialog");

            new Handler(Looper.getMainLooper()).postDelayed(() -> btnCreatePage.setEnabled(true), 1000);
        });

        menuIcon.setOnClickListener(view -> {
            menuIcon.setEnabled(false);

            MenuDialogFragment menu = new MenuDialogFragment();
            menu.show(getSupportFragmentManager(), "MenuDialog");

            new Handler(Looper.getMainLooper()).postDelayed(() -> menuIcon.setEnabled(true), 800);
        });

        clickSecretCount = 0;
        toolbarTitle.setOnClickListener(view -> {
            clickSecretCount++;
            if (clickSecretCount >= 14) {
                clickSecretCount = 0;
                Toast.makeText(this, "Pages secrètes trouvées", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, SecretMainActivity.class);
                secretMainLauncher.launch(intent);
            }
        });
    }

    private void refreshPageList() {
        if (!pageStorage.hasPagesFile(false)) {
            Toast.makeText(this, PageStorage.PAGES_FILE + " introuvable", Toast.LENGTH_LONG).show();
            return;
        }

        pageListRenderer.clear();

        try {
            List<Page> pages = pageStorage.loadPages(false);
            System.out.println("Pages chargées : " + pages.size());
            pageListRenderer.render(pages, this::openModifyPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openModifyPage(Page clickedPage) {
        Intent intent = new Intent(MainActivity.this, ModifyPageActivity.class);
        intent.putExtra("page", clickedPage);
        modifyPageLauncher.launch(intent);
    }
}
