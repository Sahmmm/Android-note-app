package com.example.noteapp.data;

import android.content.Context;

import com.example.noteapp.model.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class PageStorage {
    public static final String PAGES_FILE = "pages.json";
    public static final String SECRET_PAGES_FILE = "pagesSecret.json";

    private final File filesDir;
    private final ObjectMapper objectMapper;

    public PageStorage(Context context) {
        filesDir = context.getFilesDir();
        objectMapper = new ObjectMapper();
    }

    public boolean hasPagesFile(boolean secret) {
        return getPagesFile(secret).exists();
    }

    public Page createPage(String title, String icon, String colorFont, boolean secret) {
        return new Page(UUID.randomUUID().toString(), title, icon, colorFont, secret);
    }

    public List<Page> loadPages(boolean secret) throws IOException {
        File file = getPagesFile(secret);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        return objectMapper.readValue(file, new TypeReference<List<Page>>() {
        });
    }

    public void addPage(Page page) throws IOException {
        List<Page> pages = loadPagesForWrite(page.isSecret);
        pages.add(page);
        savePages(page.isSecret, pages);
    }

    public void updatePage(Page page, String content) throws IOException {
        List<Page> pages = loadPagesForWrite(page.isSecret);
        String currentPageId = page.getId();

        for (int i = 0; i < pages.size(); i++) {
            Page existingPage = pages.get(i);
            if (existingPage.getId().equals(currentPageId)) {
                if (!existingPage.getContent().equals(content)) {
                    existingPage.setDate(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()));
                    existingPage.setTime(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
                }
                existingPage.setContent(content);
                existingPage.setTitle(page.getTitle());
                if (!page.getIcon().isEmpty()) {
                    existingPage.setIcon(page.getIcon());
                }
                break;
            }
        }

        savePages(page.isSecret, pages);
    }

    public void deletePage(Page page) throws IOException {
        List<Page> pages = loadPagesForWrite(page.isSecret);
        String currentPageId = page.getId();

        for (int i = 0; i < pages.size(); i++) {
            if (pages.get(i).getId().equals(currentPageId)) {
                pages.remove(i);
                break;
            }
        }

        savePages(page.isSecret, pages);
    }

    public void savePages(boolean secret, List<Page> pages) throws IOException {
        objectMapper.writeValue(getPagesFile(secret), pages);
    }

    private File getPagesFile(boolean secret) {
        return new File(filesDir, secret ? SECRET_PAGES_FILE : PAGES_FILE);
    }

    private List<Page> loadPagesForWrite(boolean secret) {
        try {
            return loadPages(secret);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
