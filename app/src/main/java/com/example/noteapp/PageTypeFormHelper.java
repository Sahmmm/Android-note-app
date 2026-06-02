package com.example.noteapp;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

public class PageTypeFormHelper {
    private final RadioGroup typeRadioGroup;
    private final LinearLayout reminderFields;

    public PageTypeFormHelper(RadioGroup typeRadioGroup, LinearLayout reminderFields) {
        this.typeRadioGroup = typeRadioGroup;
        this.reminderFields = reminderFields;
    }

    public void bind(String selectedType) {
        setSelectedType(selectedType);
        updateReminderVisibility(selectedType);
        typeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> updateReminderVisibility(getSelectedType()));
    }

    public String getSelectedType() {
        int checkedId = typeRadioGroup.getCheckedRadioButtonId();
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

    private void updateReminderVisibility(String type) {
        reminderFields.setVisibility(Page.TYPE_REMINDER.equals(type) ? View.VISIBLE : View.GONE);
    }
}
