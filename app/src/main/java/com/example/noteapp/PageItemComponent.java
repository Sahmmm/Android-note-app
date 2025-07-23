package com.example.noteapp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PageItemComponent extends LinearLayout {
    public TextView titleView;
    public TextView dateView, pageIconView;
    private Page pageData;

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
        dateView = findViewById(R.id.pageDateView);
        pageIconView = findViewById(R.id.pageIconView);
    }

    public void bind(Page page) {
        this.pageData = page;
        titleView.setText(page.getTitle());
        dateView.setText(page.getDate());
        pageIconView.setText(page.getIcon());

    }

    public Page getPage() {
        return pageData;
    }

}

