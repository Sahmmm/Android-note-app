package com.example.noteapp;

import android.content.Context;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PageRepository {
    public static final String PUBLIC_PAGES_FILE = "pages.json";
    public static final String SECRET_PAGES_FILE = "pagesSecret.json";

    private final Context context;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PageRepository(Context context) {
        this.context = context.getApplicationContext();
    }

    public List<Page> loadPublicPages() {
        return loadPages(false);
    }

    public List<Page> loadSecretPages() {
        return loadPages(true);
    }

    public List<Page> loadPages(boolean secret) {
        File file = getFile(secret);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try {
            return objectMapper.readValue(file, new TypeReference<List<Page>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void addPage(Page page) {
        List<Page> pages = loadPages(page.isSecret);
        pages.add(page);
        savePages(page.isSecret, pages);
    }

    public void updatePage(Page page) {
        List<Page> pages = loadPages(page.isSecret);
        boolean updated = false;
        for (int i = 0; i < pages.size(); i++) {
            if (pages.get(i).getId().equals(page.getId())) {
                pages.set(i, page);
                updated = true;
                break;
            }
        }
        if (!updated) {
            pages.add(page);
        }
        savePages(page.isSecret, pages);
    }

    public void deletePage(Page page) {
        List<Page> pages = loadPages(page.isSecret);
        for (int i = 0; i < pages.size(); i++) {
            if (pages.get(i).getId().equals(page.getId())) {
                pages.remove(i);
                break;
            }
        }
        savePages(page.isSecret, pages);
    }

    public Page findPageById(Page page) {
        for (Page storedPage : loadPages(page.isSecret)) {
            if (storedPage.getId().equals(page.getId())) {
                return storedPage;
            }
        }
        return null;
    }

    private void savePages(boolean secret, List<Page> pages) {
        try {
            objectMapper.writeValue(getFile(secret), pages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getFile(boolean secret) {
        return new File(context.getFilesDir(), secret ? SECRET_PAGES_FILE : PUBLIC_PAGES_FILE);
    }
}
