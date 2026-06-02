package com.example.noteapp.ui.editor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.noteapp.R;

public class ModifyPageDialogFragment extends DialogFragment {

    private EditText editTitleInput, editTextIcon;
    private OnModifyListener listener;
    private ImageButton saveButton;
    private View btnClose;

    public interface OnModifyListener {
        void onModify(String title, String icon);
    }

    public void setOnModifyListener(OnModifyListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_modify_page, null);

        editTitleInput = view.findViewById(R.id.editTitleInput);
        editTextIcon = view.findViewById(R.id.editTextIcon);
        saveButton = view.findViewById(R.id.saveButton);
        btnClose = view.findViewById(R.id.btnClose);

        String pageColor = getArguments() != null ? getArguments().getString("color", "#FFFFFF") : "#FFFFFF";

        int parsedColor = Color.parseColor(pageColor);
        saveButton.setBackgroundTintList(ColorStateList.valueOf(parsedColor));

        btnClose.setOnClickListener(v -> dismiss());

        builder.setView(view);
        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(d -> saveButton.setOnClickListener(v -> {
            if (!editTitleInput.getText().isEmpty()) {
                if (listener != null) {
                    listener.onModify(
                            editTitleInput.getText().toString(),
                            editTextIcon.getText().toString()
                    );
                }
                dialog.dismiss();
            } else {
                Toast.makeText(getContext(), "Le titre est requis", Toast.LENGTH_SHORT).show();
            }
        }));

        return dialog;
    }
}
