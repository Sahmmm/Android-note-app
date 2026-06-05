package com.example.noteapp.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Page implements Serializable {
    private String id;
    private String title;
    private String content;
    private String icon;
    private String colorFont;
    private String colorText;
    private String time;
    private String date;
    public boolean isSecret;

    public Page(String id, String title, String content, String icon, String colorFont, String colorText, boolean isSecret) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.icon = icon;
        this.colorFont = colorFont;
        this.colorText = colorText;
        this.time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        this.date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        this.isSecret = isSecret;
    }

    public Page(String id, String title, String icon, String colorFont, boolean isSecret) {
        this(id, title, "", icon, colorFont, "#FFFFFFFF", isSecret);
        if (icon.isEmpty()) {
            setIcon("\uD83D\uDCC4");
        }
    }

    public Page() {
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getIcon() {
        return icon;
    }

    public String getColorFont() {
        return colorFont;
    }

    public String getColorText() {
        return colorText;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setColorFont(String colorFont) {
        this.colorFont = colorFont;
    }

    public void setColorText(String colorText) {
        this.colorText = colorText;
    }

    public void setTime(String newTime) {
        this.time = newTime;
    }

    public void setDate(String newDate) {
        this.date = newDate;
    }
}
