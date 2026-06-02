package com.example.noteapp;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewOutlineProvider;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

public class PageItemComponent extends LinearLayout {
    public TextView titleView;
    public TextView dateView, timeView, pageIconView, typeView, metaView;
    private Page pageData;
    private ConstraintLayout pageContainer;

    public PageItemComponent(Context context) {
        super(context);
        init(context);
    }

    public PageItemComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PageItemComponent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.component_page_item, this, true);
        titleView = findViewById(R.id.pageTitleView);
        timeView = findViewById(R.id.pageTimeView);
        dateView = findViewById(R.id.pageDateView);
        pageIconView = findViewById(R.id.pageIconView);
        typeView = findViewById(R.id.pageTypeView);
        metaView = findViewById(R.id.pageMetaView);
        pageContainer = findViewById(R.id.pageContainer);
    }

    public void bind(Page page) {
        this.pageData = page;
        titleView.setText(page.getTitle());
        timeView.setText(page.getTime());
        dateView.setText(page.getDate());
        pageIconView.setText(page.getIcon());
        typeView.setText(page.getTypeLabel());
        metaView.setText(getMetaText(page));

        int color = Color.parseColor(page.getColorFont());
//        pageContainer.setBackgroundTintList(ColorStateList.valueOf(color));


        Drawable background = pageContainer.getBackground().mutate();

        if (background instanceof GradientDrawable) {
            GradientDrawable drawable = (GradientDrawable) background;
            drawable.setColor(color); // Fond

        }



    }

    public Page getPage() {
        return pageData;
    }

    private String getMetaText(Page page) {
        if (page.isReminder()) {
            return page.getReminderLabel();
        }
        if (page.isList()) {
            int totalItems = 0;
            int checkedItems = 0;
            String content = page.getContent();
            if (content != null && !content.trim().isEmpty()) {
                String[] lines = content.trim().split("\\R+");
                for (String line : lines) {
                    if (!line.trim().isEmpty()) {
                        totalItems++;
                        if (line.startsWith("- [x] ") || line.startsWith("- [X] ")) {
                            checkedItems++;
                        }
                    }
                }
            }
            if (totalItems == 0) {
                return "Liste vide";
            }
            return checkedItems + "/" + totalItems + " cochés";
        }
        return "Modifiée le " + page.getDate();
    }

}

