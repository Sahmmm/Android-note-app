package com.example.noteapp;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class ModifyPageDialogFragment extends DialogFragment {

    private EditText editTitleInput, editTextIcon, reminderDateInput, reminderTimeInput;
    private OnModifyListener listener;
    private ImageButton saveButton;
    private LinearLayout reminderFields;
    private RadioGroup typeRadioGroup;
    private View btnClose;

    public interface OnModifyListener {
        void onModify(String title, String icon, String type, String reminderDate, String reminderTime);
    }

    public void setOnModifyListener(OnModifyListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_modify_page, null); // ton layout perso

        editTitleInput = view.findViewById(R.id.editTitleInput);
        editTextIcon = view.findViewById(R.id.editTextIcon);
        reminderDateInput = view.findViewById(R.id.reminderDateInput);
        reminderTimeInput = view.findViewById(R.id.reminderTimeInput);
        reminderFields = view.findViewById(R.id.reminderFields);
        typeRadioGroup = view.findViewById(R.id.typeRadioGroup);
        saveButton = view.findViewById(R.id.saveButton);
        btnClose = view.findViewById(R.id.btnClose);

        String pageColor = getArguments() != null ? getArguments().getString("color", "#FFFFFF") : "#FFFFFF";
        String pageTitle = getArguments() != null ? getArguments().getString("title", "") : "";
        String pageIcon = getArguments() != null ? getArguments().getString("icon", "") : "";
        String pageType = getArguments() != null ? getArguments().getString("type", Page.TYPE_NOTE) : Page.TYPE_NOTE;
        String reminderDate = getArguments() != null ? getArguments().getString("reminderDate", "") : "";
        String reminderTime = getArguments() != null ? getArguments().getString("reminderTime", "") : "";

        editTitleInput.setText(pageTitle);
        editTextIcon.setText(pageIcon);
        reminderDateInput.setText(reminderDate);
        reminderTimeInput.setText(reminderTime);
        setSelectedType(pageType);
        reminderFields.setVisibility(Page.TYPE_REMINDER.equals(pageType) ? View.VISIBLE : View.GONE);
        typeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            reminderFields.setVisibility(Page.TYPE_REMINDER.equals(getSelectedType(checkedId)) ? View.VISIBLE : View.GONE);
        });

        int parsedColor = Color.parseColor(pageColor);
        saveButton.setBackgroundTintList(ColorStateList.valueOf(parsedColor));

        btnClose.setOnClickListener(v->{dismiss();});

        builder.setView(view);
        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(d -> {
            saveButton.setOnClickListener(v -> {

                if (!editTitleInput.getText().toString().trim().isEmpty()) {
                    if (listener != null) {
                        listener.onModify(
                                editTitleInput.getText().toString(),
                                editTextIcon.getText().toString(),
                                getSelectedType(typeRadioGroup.getCheckedRadioButtonId()),
                                reminderDateInput.getText().toString(),
                                reminderTimeInput.getText().toString()
                        );
                    }
                    dialog.dismiss(); // fermer le popup après sauvegarde
                } else {
                    Toast.makeText(getContext(), "Le titre est requis", Toast.LENGTH_SHORT).show();
                }
            });
        });

        return dialog;
    }

    private String getSelectedType(int checkedId) {
        if (checkedId == R.id.typeReminder) {
            return Page.TYPE_REMINDER;
        }
        if (checkedId == R.id.typeList) {
            return Page.TYPE_LIST;
        }
        return Page.TYPE_NOTE;
    }

    private void setSelectedType(String type) {
        if (Page.TYPE_REMINDER.equals(type)) {
            typeRadioGroup.check(R.id.typeReminder);
        } else if (Page.TYPE_LIST.equals(type)) {
            typeRadioGroup.check(R.id.typeList);
        } else {
            typeRadioGroup.check(R.id.typeNote);
        }
    }
}
