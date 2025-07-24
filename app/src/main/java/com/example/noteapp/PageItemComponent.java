package com.example.noteapp;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

public class PageItemComponent extends LinearLayout {
    public TextView titleView;
    public TextView dateView, timeView, pageIconView;
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
        pageContainer = findViewById(R.id.pageContainer);
    }

    public void bind(Page page) {
        this.pageData = page;
        titleView.setText(page.getTitle());
        timeView.setText(page.getTime());
        dateView.setText(page.getDate());
        pageIconView.setText(page.getIcon());

        int color = Color.parseColor(page.getColorFont());
        pageContainer.setBackgroundTintList(ColorStateList.valueOf(color));



    }

    public Page getPage() {
        return pageData;
    }

}

