package com.example.noteapp;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PageRepository {
    private PageDao pageDao;
    private LiveData<List<Page>> normalPages;
    private LiveData<List<Page>> secretPages;
    private ExecutorService executorService;

    public PageRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        pageDao = database.pageDao();
        normalPages = pageDao.getNormalPages();
        secretPages = pageDao.getSecretPages();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(Page page) {
        executorService.execute(() -> pageDao.insert(page));
    }

    public void update(Page page) {
        executorService.execute(() -> pageDao.update(page));
    }

    public void delete(Page page) {
        executorService.execute(() -> pageDao.delete(page));
    }

    public LiveData<List<Page>> getNormalPages() { return normalPages; }
    public LiveData<List<Page>> getSecretPages() { return secretPages; }
}