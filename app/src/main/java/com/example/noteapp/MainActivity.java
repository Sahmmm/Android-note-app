package com.example.noteapp;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.DragEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MenuDialogFragment.OnMenuActionSelectedListener {

    private FloatingActionButton btnCreatePage;
    private ActivityResultLauncher<Intent> modifyPageLauncher, secretMainLauncher;
    private ImageView menuIcon;
    private TextView toolbarTitle, emptyStateTitle, emptyStateSubtitle, trashDropZone;
    private EditText searchInput;
    private LinearLayout emptyStateContainer;
    private PageRepository pageRepository;
    private int clickSecretCount;
    private String currentTypeFilter = Page.TYPE_NOTE;
    private String searchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnCreatePage = findViewById(R.id.btnCreatePage);
        menuIcon = findViewById(R.id.menuIcon);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        searchInput = findViewById(R.id.searchInput);
        emptyStateContainer = findViewById(R.id.emptyStateContainer);
        emptyStateTitle = findViewById(R.id.emptyStateTitle);
        emptyStateSubtitle = findViewById(R.id.emptyStateSubtitle);
        trashDropZone = findViewById(R.id.trashDropZone);
        pageRepository = new PageRepository(this);


        refreshPageList();
        updateToolbarTitle();
        setupSearch();
        setupTrashDropZone();

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

            CreatePageDialogFragment dialog = new CreatePageDialogFragment(this::refreshPageList, currentTypeFilter);
            dialog.show(getSupportFragmentManager(), "CreatePageDialog");

            // Réactiver après un délai
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                btnCreatePage.setEnabled(true);
            }, 1000); // 1 seconde
        });

        menuIcon.setOnClickListener(view -> {
            menuIcon.setEnabled(false);

            MenuDialogFragment menu = new MenuDialogFragment();
            Bundle args = new Bundle();
            args.putString("selectedAction", currentTypeFilter);
            menu.setArguments(args);
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
        LinearLayout container = findViewById(R.id.componentContainer);
        container.removeAllViews();
        int visibleItems = 0;

        List<Page> pages = pageRepository.loadPublicPages();
        for (Page p : pages) {
            if (matchesCurrentFilter(p)) {
                container.addView(createItem(p));
                visibleItems++;
            }
        }
        updateEmptyState(visibleItems);
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
            ClipData data = ClipData.newPlainText("page_id", p.getId());
            trashDropZone.setVisibility(View.VISIBLE);
            v.startDragAndDrop(data, new View.DragShadowBuilder(v), item, 0);
            return true;
        });

        return item;
    }

    @Override
    public void onMenuActionSelected(String action) {
        if (MenuDialogFragment.ACTION_SETTINGS.equals(action)) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return;
        }
        currentTypeFilter = action;
        updateToolbarTitle();
        refreshPageList();
    }

    private boolean matchesCurrentFilter(Page page) {
        if (!currentTypeFilter.equals(page.getType())) {
            return false;
        }
        if (searchQuery.isEmpty()) {
            return true;
        }
        String normalizedQuery = searchQuery.toLowerCase();
        return page.getTitle().toLowerCase().contains(normalizedQuery)
                || page.getContent().toLowerCase().contains(normalizedQuery)
                || page.getTypeLabel().toLowerCase().contains(normalizedQuery);
    }

    private void updateToolbarTitle() {
        switch (currentTypeFilter) {
            case Page.TYPE_REMINDER:
                toolbarTitle.setText("Rappels");
                break;
            case Page.TYPE_LIST:
                toolbarTitle.setText("Listes");
                break;
            default:
                toolbarTitle.setText("Notes");
                break;
        }
    }

    private void setupSearch() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString().trim();
                refreshPageList();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupTrashDropZone() {
        trashDropZone.setOnDragListener((view, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    trashDropZone.setVisibility(View.VISIBLE);
                    return true;
                case DragEvent.ACTION_DROP:
                    if (event.getLocalState() instanceof PageItemComponent) {
                        Page page = ((PageItemComponent) event.getLocalState()).getPage();
                        deletePage(page);
                        Toast.makeText(this, "Page supprimée", Toast.LENGTH_SHORT).show();
                        refreshPageList();
                        return true;
                    }
                    return false;
                case DragEvent.ACTION_DRAG_ENDED:
                    trashDropZone.setVisibility(View.GONE);
                    return true;
                default:
                    return true;
            }
        });
    }

    private void updateEmptyState(int visibleItems) {
        emptyStateContainer.setVisibility(visibleItems == 0 ? View.VISIBLE : View.GONE);
        if (visibleItems != 0) {
            return;
        }
        if (!searchQuery.isEmpty()) {
            emptyStateTitle.setText("Aucun résultat");
            emptyStateSubtitle.setText("Essaie avec un autre mot-clé.");
        } else {
            emptyStateTitle.setText(getCurrentEmptyTitle());
            emptyStateSubtitle.setText("Appuie sur + pour créer " + getCurrentTypeArticle() + ".");
        }
    }

    private String getCurrentEmptyTitle() {
        switch (currentTypeFilter) {
            case Page.TYPE_REMINDER:
                return "Aucun rappel";
            case Page.TYPE_LIST:
                return "Aucune liste";
            default:
                return "Aucune note";
        }
    }

    private String getCurrentTypeArticle() {
        switch (currentTypeFilter) {
            case Page.TYPE_REMINDER:
                return "un rappel";
            case Page.TYPE_LIST:
                return "une liste";
            default:
                return "une note";
        }
    }

    private void deletePage(Page page) {
        pageRepository.deletePage(page);
    }
}