package com.example.noteapp.ui.common;

import android.content.Context;
import android.widget.LinearLayout;

import com.example.noteapp.model.Page;
import com.example.noteapp.ui.widget.PageItemComponent;

import java.util.List;

public class PageListRenderer {
    private final Context context;
    private final LinearLayout container;

    public interface OnPageClickListener {
        void onPageClick(Page page);
    }

    public PageListRenderer(Context context, LinearLayout container) {
        this.context = context;
        this.container = container;
    }

    public void clear() {
        container.removeAllViews();
    }

    public void render(List<Page> pages, OnPageClickListener listener) {
        clear();
        for (Page page : pages) {
            container.addView(createItem(page, listener));
        }
    }

    private PageItemComponent createItem(Page page, OnPageClickListener listener) {
        PageItemComponent item = new PageItemComponent(context);
        item.bind(page);

        item.setOnClickListener(v -> listener.onPageClick(item.getPage()));
        item.setOnLongClickListener(v -> false);

        return item;
    }
}
