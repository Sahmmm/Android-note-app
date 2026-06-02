package com.example.noteapp;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.DialogFragment;

public class MenuDialogFragment extends DialogFragment {

    public static final String ACTION_NOTES = Page.TYPE_NOTE;
    public static final String ACTION_REMINDERS = Page.TYPE_REMINDER;
    public static final String ACTION_LISTS = Page.TYPE_LIST;
    public static final String ACTION_SETTINGS = "settings";

    public interface OnMenuActionSelectedListener {
        void onMenuActionSelected(String action);
    }

    private TextView btnQuit, btnSettings, menuLists, menuNotes, menuReminders;

    private LinearLayout linearLayoutMenu;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {



        View view = inflater.inflate(R.layout.dialog_hamburger_menu, container, false);


        btnQuit = view.findViewById(R.id.btnQuit);
        btnQuit.setOnClickListener( v->{
            requireActivity().finishAffinity();
        });

        View outside = view.findViewById(R.id.dialogRoot);
        outside.setOnClickListener(v -> dismiss());

        menuLists = view.findViewById(R.id.menuLists);
        menuNotes = view.findViewById(R.id.menuNotes);
        menuReminders = view.findViewById(R.id.menuReminders);
        btnSettings = view.findViewById(R.id.btnSettings);
        bindSelectedState(getArguments() != null ? getArguments().getString("selectedAction", ACTION_NOTES) : ACTION_NOTES);

        menuNotes.setOnClickListener(v -> selectAction(ACTION_NOTES));
        menuLists.setOnClickListener(v -> selectAction(ACTION_LISTS));
        menuReminders.setOnClickListener(v -> selectAction(ACTION_REMINDERS));
        btnSettings.setOnClickListener(v -> selectAction(ACTION_SETTINGS));

        return view;
    }

    private void selectAction(String action) {
        if (getActivity() instanceof OnMenuActionSelectedListener) {
            ((OnMenuActionSelectedListener) getActivity()).onMenuActionSelected(action);
        }
        dismiss();
    }

    private void bindSelectedState(String selectedAction) {
        menuNotes.setBackgroundResource(ACTION_NOTES.equals(selectedAction) ? R.drawable.menu_item_selected : R.drawable.menu_item);
        menuReminders.setBackgroundResource(ACTION_REMINDERS.equals(selectedAction) ? R.drawable.menu_item_selected : R.drawable.menu_item);
        menuLists.setBackgroundResource(ACTION_LISTS.equals(selectedAction) ? R.drawable.menu_item_selected : R.drawable.menu_item);
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

