package com.example.noteapp;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
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
    private TextView pageTitleView, pageMetaView;
    private LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_page);

        imageReturnView = findViewById(R.id.imageReturnView);
        imageOptionsView = findViewById(R.id.imageOptionsView);
        editTextContent = findViewById(R.id.editTextContent);
        pageTitleView = findViewById(R.id.pageTitleView);
        pageMetaView = findViewById(R.id.pageMetaView);
        mainLayout = findViewById(R.id.mainLayout);

        Page linkedPage = (Page) getIntent().getSerializableExtra("page");

        if ( linkedPage != null){
            pageTitleView.setText(linkedPage.getTitle());
            editTextContent.setText(linkedPage.getContent());
            mainLayout.setBackgroundColor(Color.parseColor(linkedPage.getColorFont()));
            bindPageType(linkedPage);
        }

        imageReturnView.setOnClickListener(view -> {
            assert linkedPage != null;
            saveChange(linkedPage);
            setResult(RESULT_OK); // ✅ Indique que tout s’est bien passé
            finish();
        });

        imageOptionsView.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, v);
            popupMenu.getMenuInflater().inflate(R.menu.topbar_menu_page, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();

                if (id == R.id.menu_edit) {
                    ModifyPageDialogFragment dialog = new ModifyPageDialogFragment();

                    Bundle args = new Bundle();
                    assert linkedPage != null;
                    args.putString("color", linkedPage.getColorFont()); // ex: "#FF00FF"
                    args.putString("title", linkedPage.getTitle());
                    args.putString("icon", linkedPage.getIcon());
                    args.putString("type", linkedPage.getType());
                    args.putString("reminderDate", linkedPage.getReminderDate());
                    args.putString("reminderTime", linkedPage.getReminderTime());
                    dialog.setArguments(args);

                    dialog.setOnModifyListener((newTitle, newIcon, newType, reminderDate, reminderTime) -> {
                        pageTitleView.setText(newTitle);
                        linkedPage.setTitle(newTitle);
                        linkedPage.setIcon(newIcon);
                        linkedPage.setType(newType);
                        linkedPage.setReminderDate(reminderDate);
                        linkedPage.setReminderTime(reminderTime);
                        bindPageType(linkedPage);
                        saveChange(linkedPage);
                    });
                    dialog.show(getSupportFragmentManager(), "ModifyPageDialog");
                    return true;
                } else if (id == R.id.menu_delete) {
                    deletePage(linkedPage);
                    setResult(RESULT_OK); // ✅ Indique que tout s’est bien passé
                    finish();
                    return true;
                } else if (id == R.id.menu_copy) {
                    String textToCopy = editTextContent.getText().toString();

                    // Copie dans le presse-papier
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("copied_text", textToCopy);
                    clipboard.setPrimaryClip(clip);

                    return true;
                }

                return false;
            });

            popupMenu.show();
        });

        View rootView = findViewById(R.id.constraintLayoutTitle); // met l'ID de ton ConstraintLayout

        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            int topInset = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            v.setPadding(8, topInset, 8, 12); // applique le padding supérieur
            return insets;
        });

    }

    private void saveChange(Page page) {
        String json;
        if(page.isSecret){
            json = "pagesSecret.json";
        } else {
            json = "pages.json";
        }
        File file = new File(getFilesDir(), json);
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
                    verifyPages.get(i).setTime(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
                }
                verifyPages.get(i).setContent(editTextContent.getText().toString().trim());
                // To manage the change made
                verifyPages.get(i).setTitle(page.getTitle());
                verifyPages.get(i).setType(page.getType());
                verifyPages.get(i).setReminderDate(page.getReminderDate());
                verifyPages.get(i).setReminderTime(page.getReminderTime());
                if(!page.getIcon().isEmpty()) {
                    verifyPages.get(i).setIcon(page.getIcon());
                }

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

        String json;
        if(page.isSecret){
            json = "pagesSecret.json";
        } else {
            json = "pages.json";
        }
        File file = new File(getFilesDir(), json);
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

    private void bindPageType(Page page) {
        if (page.isReminder()) {
            pageMetaView.setVisibility(View.VISIBLE);
            pageMetaView.setText(page.getReminderLabel());
            editTextContent.setHint("Détails du rappel...");
        } else if (page.isList()) {
            pageMetaView.setVisibility(View.VISIBLE);
            pageMetaView.setText("Une ligne par élément de liste");
            editTextContent.setHint("Ajoute un élément par ligne...");
        } else {
            pageMetaView.setVisibility(View.GONE);
            editTextContent.setHint("Écris ici...");
        }
    }
}
