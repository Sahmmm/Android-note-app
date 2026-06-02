package com.example.noteapp;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

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
    private EditText editTextContent, newListItemInput;
    private TextView pageTitleView, pageMetaView, addListItemButton;
    private LinearLayout mainLayout, listEditorLayout, listItemsContainer;
    private float touchStartX, touchStartY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_page);

        imageReturnView = findViewById(R.id.imageReturnView);
        imageOptionsView = findViewById(R.id.imageOptionsView);
        editTextContent = findViewById(R.id.editTextContent);
        newListItemInput = findViewById(R.id.newListItemInput);
        pageTitleView = findViewById(R.id.pageTitleView);
        pageMetaView = findViewById(R.id.pageMetaView);
        addListItemButton = findViewById(R.id.addListItemButton);
        mainLayout = findViewById(R.id.mainLayout);
        listEditorLayout = findViewById(R.id.listEditorLayout);
        listItemsContainer = findViewById(R.id.listItemsContainer);

        Page linkedPage = (Page) getIntent().getSerializableExtra("page");

        if ( linkedPage != null){
            pageTitleView.setText(linkedPage.getTitle());
            editTextContent.setText(linkedPage.getContent());
            mainLayout.setBackgroundColor(Color.parseColor(linkedPage.getColorFont()));
            bindPageType(linkedPage);
        }

        addListItemButton.setOnClickListener(v -> addListItemFromInput());
        setupSwipeToExit(linkedPage);

        imageReturnView.setOnClickListener(view -> {
            assert linkedPage != null;
            closePage(linkedPage);
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
                    String textToCopy = getCurrentContent(linkedPage);

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
                String currentContent = getCurrentContent(page);
                if(!verifyPages.get(i).getContent().equals(currentContent)){
                    verifyPages.get(i).setDate(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()));
                    verifyPages.get(i).setTime(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
                }
                verifyPages.get(i).setContent(currentContent);
                page.setContent(currentContent);
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
            editTextContent.setVisibility(View.VISIBLE);
            listEditorLayout.setVisibility(View.GONE);
            pageMetaView.setVisibility(View.VISIBLE);
            pageMetaView.setText(page.getReminderLabel());
            editTextContent.setHint("Détails du rappel...");
        } else if (page.isList()) {
            editTextContent.setVisibility(View.GONE);
            listEditorLayout.setVisibility(View.VISIBLE);
            pageMetaView.setVisibility(View.VISIBLE);
            pageMetaView.setText("Une ligne par élément de liste");
            bindListItems(page.getContent());
        } else {
            editTextContent.setVisibility(View.VISIBLE);
            listEditorLayout.setVisibility(View.GONE);
            pageMetaView.setVisibility(View.GONE);
            editTextContent.setHint("Écris ici...");
        }
    }

    private void bindListItems(String content) {
        listItemsContainer.removeAllViews();
        String trimmedContent = content == null ? "" : content.trim();
        if (trimmedContent.isEmpty()) {
            return;
        }

        String[] lines = trimmedContent.split("\\R+");
        for (String line : lines) {
            boolean checked = line.startsWith("- [x] ") || line.startsWith("- [X] ");
            String text = line
                    .replaceFirst("^- \\[[xX]\\] ", "")
                    .replaceFirst("^- \\[ \\] ", "")
                    .trim();
            if (!text.isEmpty()) {
                addCheckableListItem(text, checked);
            }
        }
    }

    private void addListItemFromInput() {
        String text = newListItemInput.getText().toString().trim();
        if (text.isEmpty()) {
            Toast.makeText(this, "Élément vide", Toast.LENGTH_SHORT).show();
            return;
        }
        addCheckableListItem(text, false);
        newListItemInput.setText("");
    }

    private void addCheckableListItem(String text, boolean checked) {
        CheckBox checkBox = new CheckBox(this);
        checkBox.setText(text);
        checkBox.setChecked(checked);
        checkBox.setTextColor(getResources().getColor(R.color.textColorBis));
        checkBox.setTextSize(18);
        checkBox.setPadding(8, 10, 8, 10);
        checkBox.setOnLongClickListener(v -> {
            listItemsContainer.removeView(v);
            return true;
        });
        listItemsContainer.addView(checkBox);
    }

    private String getCurrentContent(Page page) {
        if (page != null && page.isList()) {
            return serializeListItems();
        }
        return editTextContent.getText().toString().trim();
    }

    private String serializeListItems() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < listItemsContainer.getChildCount(); i++) {
            View child = listItemsContainer.getChildAt(i);
            if (child instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) child;
                String text = checkBox.getText().toString().trim();
                if (!text.isEmpty()) {
                    if (builder.length() > 0) {
                        builder.append('\n');
                    }
                    builder.append(checkBox.isChecked() ? "- [x] " : "- [ ] ");
                    builder.append(text);
                }
            }
        }
        return builder.toString();
    }

    private void setupSwipeToExit(Page linkedPage) {
        View.OnTouchListener listener = (view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                touchStartX = event.getX();
                touchStartY = event.getY();
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                float deltaX = event.getX() - touchStartX;
                float deltaY = Math.abs(event.getY() - touchStartY);
                if (deltaX > 180 && deltaY < 120 && linkedPage != null) {
                    closePage(linkedPage);
                    return true;
                }
            }
            return false;
        };
        mainLayout.setOnTouchListener(listener);
        editTextContent.setOnTouchListener(listener);
        listEditorLayout.setOnTouchListener(listener);
    }

    private void closePage(Page page) {
        saveChange(page);
        setResult(RESULT_OK);
        finish();
    }
}
