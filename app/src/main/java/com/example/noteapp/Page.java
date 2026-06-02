package com.example.noteapp;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Page implements Serializable {
    public static final String TYPE_NOTE = "note";
    public static final String TYPE_REMINDER = "reminder";
    public static final String TYPE_LIST = "list";

    private String id;
    private String title;
    private String content;
    private String icon;
    private String colorFont;
    private String colorText;
    private String time;
    private String date;
    private String type;
    private String reminderDate;
    private String reminderTime;
    public boolean isSecret;


    public Page(String id, String title, String content, String icon, String colorFont, String colorText, boolean isSecret){
        this.id = id;
        this.title = title;
        this.content = content;
        this.icon = icon;
        this.colorFont = colorFont;
        this.colorText = colorText;
        this.time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        this.date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        this.type = TYPE_NOTE;
        this.reminderDate = "";
        this.reminderTime = "";
        this.isSecret = isSecret;
    }

    public Page(String id, String title, String icon, String colorFont, boolean isSecret, String type, String reminderDate, String reminderTime) {
        this(id, title, "", icon, colorFont, "#FFFFFFFF", isSecret);
        setType(type);
        this.reminderDate = reminderDate == null ? "" : reminderDate.trim();
        this.reminderTime = reminderTime == null ? "" : reminderTime.trim();
        if (icon == null || icon.isEmpty()) {
            setIcon(getDefaultIconForType(this.type));
        }
    }

    public Page(String id, String title, String icon, String colorFont, boolean isSecret) {
        this(id, title, "", icon, colorFont, "#FFFFFFFF", isSecret);
        if(icon == null || icon.isEmpty()){
            setIcon(getDefaultIconForType(TYPE_NOTE));
        }
    }

    public Page() {
    }

    public String getId(){return id;}
    public String getTitle(){return title == null ? "" : title;}
    public String getContent(){return content == null ? "" : content;}
    public String getIcon(){return icon == null || icon.isEmpty() ? getDefaultIconForType(getType()) : icon;}
    public String getColorFont(){return colorFont == null || colorFont.isEmpty() ? "#B0E1FA" : colorFont;}
    public String getColorText(){return colorText == null || colorText.isEmpty() ? "#FFFFFFFF" : colorText;}
    public String getTime(){return time;}
    public String getDate() {return date;}
    public String getType(){return normalizeType(type);}
    public String getReminderDate(){return reminderDate == null ? "" : reminderDate;}
    public String getReminderTime(){return reminderTime == null ? "" : reminderTime;}


    public void setTitle(String title){this.title = title;}
    public void setContent(String content){this.content = content;}
    public void setIcon(String icon){this.icon=icon;}
    public void setColorFont(String colorFont){this.colorFont = colorFont;}
    public void setColorText(String colorText){this.colorText = colorText;}
    public void setTime(String newTime){this.time = newTime;}
    public void setDate(String newDate){this.date = newDate;}
    public void setType(String type){this.type = normalizeType(type);}
    public void setReminderDate(String reminderDate){this.reminderDate = reminderDate == null ? "" : reminderDate.trim();}
    public void setReminderTime(String reminderTime){this.reminderTime = reminderTime == null ? "" : reminderTime.trim();}

    public boolean isReminder(){return TYPE_REMINDER.equals(getType());}
    public boolean isList(){return TYPE_LIST.equals(getType());}

    public String getTypeLabel() {
        switch (getType()) {
            case TYPE_REMINDER:
                return "Rappel";
            case TYPE_LIST:
                return "Liste";
            default:
                return "Note";
        }
    }

    public String getReminderLabel() {
        if (!isReminder()) {
            return "";
        }
        String datePart = getReminderDate();
        String timePart = getReminderTime();
        if (datePart.isEmpty() && timePart.isEmpty()) {
            return "Rappel sans date";
        }
        return (datePart + " " + timePart).trim();
    }

    private static String normalizeType(String type) {
        if (TYPE_REMINDER.equals(type) || TYPE_LIST.equals(type)) {
            return type;
        }
        return TYPE_NOTE;
    }

    private static String getDefaultIconForType(String type) {
        switch (normalizeType(type)) {
            case TYPE_REMINDER:
                return "\u23F0";
            case TYPE_LIST:
                return "\u2611";
            default:
                return "\uD83D\uDCC4";
        }
    }

}
