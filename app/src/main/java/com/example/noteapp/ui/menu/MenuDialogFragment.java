package com.example.noteapp.ui.menu;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.noteapp.R;

public class MenuDialogFragment extends DialogFragment {

    private TextView btnQuit, menuLists, menuNotes, menuReminders;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_hamburger_menu, container, false);

        btnQuit = view.findViewById(R.id.btnQuit);
        btnQuit.setOnClickListener(v -> requireActivity().finishAffinity());

        View outside = view.findViewById(R.id.dialogRoot);
        outside.setOnClickListener(v -> dismiss());

        menuLists = view.findViewById(R.id.menuLists);
        menuNotes = view.findViewById(R.id.menuNotes);
        menuReminders = view.findViewById(R.id.menuReminders);

        menuNotes.setOnClickListener(v -> Toast.makeText(getContext(), "Prout", Toast.LENGTH_SHORT).show());
        menuLists.setOnClickListener(v -> Toast.makeText(getContext(), "à venir...", Toast.LENGTH_SHORT).show());
        menuReminders.setOnClickListener(v -> Toast.makeText(getContext(), "à venir...", Toast.LENGTH_SHORT).show());

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.CustomDialogFullScreen);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            Window window = getDialog().getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            window.setGravity(Gravity.START);
            window.setWindowAnimations(R.style.SlideDialogAnimation);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}
