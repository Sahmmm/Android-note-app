package com.example.noteapp.ui.create;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.noteapp.R;

public class AddColorDialogFragment extends DialogFragment {

    private OnModifyListener listener;

    private View btnClose;
    private EditText newColorInput;
    private ImageButton saveButton;

    public interface OnModifyListener {
        void onModify(String color);
    }

    public void setOnModifyListener(OnModifyListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_color, null);

        btnClose = view.findViewById(R.id.btnClose);
        saveButton = view.findViewById(R.id.saveButton);
        newColorInput = view.findViewById(R.id.newColorInput);

        btnClose.setOnClickListener(v -> dismiss());

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(d -> saveButton.setOnClickListener(v -> {
            String colorStr = newColorInput.getText().toString().trim();

            if (colorStr.matches("^#(?:[0-9a-fA-F]{6}|[0-9a-fA-F]{8})$")) {
                if (listener != null) {
                    listener.onModify(newColorInput.getText().toString());
                }
                dialog.dismiss();
            } else {
                Toast.makeText(getContext(), "Format de couleur invalide", Toast.LENGTH_SHORT).show();
            }
        }));
        return dialog;
    }
}
