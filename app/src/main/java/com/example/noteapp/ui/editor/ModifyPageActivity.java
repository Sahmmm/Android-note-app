package com.example.noteapp.ui.editor;

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

import com.example.noteapp.R;
import com.example.noteapp.data.PageStorage;
import com.example.noteapp.model.Page;

import java.io.IOException;

public class ModifyPageActivity extends AppCompatActivity {

    private ImageView imageReturnView, imageOptionsView;
    private EditText editTextContent;
    private TextView pageTitleView;
    private LinearLayout mainLayout;
    private PageStorage pageStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_page);

        imageReturnView = findViewById(R.id.imageReturnView);
        imageOptionsView = findViewById(R.id.imageOptionsView);
        editTextContent = findViewById(R.id.editTextContent);
        pageTitleView = findViewById(R.id.pageTitleView);
        mainLayout = findViewById(R.id.mainLayout);
        pageStorage = new PageStorage(this);

        Page linkedPage = (Page) getIntent().getSerializableExtra("page");

        if (linkedPage != null) {
            pageTitleView.setText(linkedPage.getTitle());
            editTextContent.setText(linkedPage.getContent());
            mainLayout.setBackgroundColor(Color.parseColor(linkedPage.getColorFont()));
        }

        imageReturnView.setOnClickListener(view -> {
            assert linkedPage != null;
            saveChange(linkedPage);
            setResult(RESULT_OK);
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
                    args.putString("color", linkedPage.getColorFont());
                    dialog.setArguments(args);

                    dialog.setOnModifyListener((newTitle, newIcon) -> {
                        pageTitleView.setText(newTitle);
                        linkedPage.setTitle(newTitle);
                        linkedPage.setIcon(newIcon);
                        saveChange(linkedPage);
                    });
                    dialog.show(getSupportFragmentManager(), "ModifyPageDialog");
                    return true;
                } else if (id == R.id.menu_delete) {
                    deletePage(linkedPage);
                    setResult(RESULT_OK);
                    finish();
                    return true;
                } else if (id == R.id.menu_copy) {
                    String textToCopy = editTextContent.getText().toString();

                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("copied_text", textToCopy);
                    clipboard.setPrimaryClip(clip);

                    return true;
                }

                return false;
            });

            popupMenu.show();
        });

        View rootView = findViewById(R.id.constraintLayoutTitle);

        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            int topInset = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            v.setPadding(8, topInset, 8, 12);
            return insets;
        });
    }

    private void saveChange(Page page) {
        try {
            pageStorage.updatePage(page, editTextContent.getText().toString().trim());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deletePage(Page page) {
        try {
            pageStorage.deletePage(page);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
