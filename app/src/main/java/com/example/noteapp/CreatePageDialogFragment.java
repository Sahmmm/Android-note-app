package com.example.noteapp;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class CreatePageDialogFragment extends DialogFragment {

    public interface OnPageCreatedListener {
        void onPageCreated();
    }
    private OnPageCreatedListener listener;
    private String defaultType = Page.TYPE_NOTE;
    public CreatePageDialogFragment(OnPageCreatedListener listener) {
        this.listener = listener;
    }

    public CreatePageDialogFragment(OnPageCreatedListener listener, String defaultType) {
        this.listener = listener;
        this.defaultType = defaultType;
    }
    private EditText titleInput, editTextIcon, reminderDateInput, reminderTimeInput;
    private CheckBox checkSecret;
    private ImageButton saveButton;
    private LinearLayout reminderFields;
    private RadioGroup typeRadioGroup;
    private PageTypeFormHelper pageTypeFormHelper;
    private View btnClose, color1View, color2View, color3View, color4View, color5View, color6View, colorAddView;
    private ImageView checkOverlay1, checkOverlay2, checkOverlay3, checkOverlay4, checkOverlay5, checkOverlay6;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_create_page, container, false);

        titleInput = view.findViewById(R.id.editTitleInput);
        editTextIcon = view.findViewById(R.id.editTextIcon);
        reminderDateInput = view.findViewById(R.id.reminderDateInput);
        reminderTimeInput = view.findViewById(R.id.reminderTimeInput);
        saveButton = view.findViewById(R.id.saveButton);
        checkSecret = view.findViewById(R.id.checkSecret);
        typeRadioGroup = view.findViewById(R.id.typeRadioGroup);
        reminderFields = view.findViewById(R.id.reminderFields);
        btnClose = view.findViewById(R.id.btnClose);
        checkSecret = view.findViewById(R.id.checkSecret);
        checkOverlay1 = view.findViewById(R.id.checkOverlay1);
        checkOverlay2 = view.findViewById(R.id.checkOverlay2);
        checkOverlay3 = view.findViewById(R.id.checkOverlay3);
        checkOverlay4 = view.findViewById(R.id.checkOverlay4);
        checkOverlay5 = view.findViewById(R.id.checkOverlay5);
        checkOverlay6 = view.findViewById(R.id.checkOverlay6);
        color1View = view.findViewById(R.id.color1View);
        color2View = view.findViewById(R.id.color2View);
        color3View = view.findViewById(R.id.color3View);
        color4View = view.findViewById(R.id.color4View);
        color5View = view.findViewById(R.id.color5View);
        color6View = view.findViewById(R.id.color6View);
        colorAddView = view.findViewById(R.id.colorAddView);

        List<ImageView> checkOverlays = Arrays.asList(checkOverlay1, checkOverlay2, checkOverlay3, checkOverlay4, checkOverlay5, checkOverlay6);

        List<View> colorsChecked = Arrays.asList(color1View, color2View, color3View, color4View, color5View, color6View);
        AtomicReference<String> backgroundColor = new AtomicReference<>("#B0E1FA");

        pageTypeFormHelper = new PageTypeFormHelper(typeRadioGroup, reminderFields);
        pageTypeFormHelper.bind(defaultType);

        for (int i = 0; i < colorsChecked.size(); i++) {
            final int index = i; // capture correcte
            View colorChecked = colorsChecked.get(i);

            colorChecked.setOnClickListener(v -> {
                // Cacher tous les overlays
                for (ImageView overlay : checkOverlays) {
                    overlay.setVisibility(View.INVISIBLE);
                }

                // Trouver le parent et la pastille de couleur
                View colorView = colorsChecked.get(index);

                if (colorView != null && colorView.getBackgroundTintList() != null) {
                    int color = colorView.getBackgroundTintList().getDefaultColor();
                    backgroundColor.set(String.format("#%06X", (0xFFFFFF & color)));
                    // Convertir la chaîne en int couleur
                    int parsedColor = Color.parseColor(backgroundColor.get());
                    saveButton.setBackgroundTintList(ColorStateList.valueOf(parsedColor));
                }

                // Afficher uniquement l’overlay sélectionné
                checkOverlays.get(index).setVisibility(View.VISIBLE);
            });
        }


        saveButton.setOnClickListener(v -> {
            String title = titleInput.getText().toString();
            String icon = editTextIcon.getText().toString();
            boolean isSecret = checkSecret.isChecked();
            String selectedType = pageTypeFormHelper.getSelectedType();
            String reminderDate = reminderDateInput.getText().toString();
            String reminderTime = reminderTimeInput.getText().toString();

            if (!title.isEmpty()) {
                String id = UUID.randomUUID().toString();
                Page newPage = new Page(id, title, icon, backgroundColor.get(), isSecret, selectedType, reminderDate, reminderTime);
                new PageRepository(requireContext()).addPage(newPage);

                if (listener != null) listener.onPageCreated();
                dismiss(); // Fermer la popup
            } else {
                Toast.makeText(getContext(), "Le titre est requis", Toast.LENGTH_SHORT).show();
            }
        });

        btnClose.setOnClickListener(v ->{
            dismiss();
        });

        colorAddView.setOnClickListener(v -> {
            AddColorDialogFragment dialog = new AddColorDialogFragment();

            dialog.setOnModifyListener(newColorInput -> {
                try {

                    int color = Color.parseColor(newColorInput);
                    color6View.setVisibility(View.VISIBLE);
                    color6View.setBackgroundTintList(ColorStateList.valueOf(color));

                    // ✅ Désactiver le clic (si plus modifiable après)
                    colorAddView.setVisibility(View.GONE);
                    colorAddView.setClickable(false);

                } catch (IllegalArgumentException e) {
                    Toast.makeText(requireContext(), "Couleur invalide", Toast.LENGTH_SHORT).show();
                }
            });

            dialog.show(requireActivity().getSupportFragmentManager(), "AddNewColor");
        });
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomDialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            Window window = dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
        }
    }

}

