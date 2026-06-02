package com.example.noteapp;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;

public class PageViewModel extends AndroidViewModel {
    private PageRepository repository;
    private LiveData<List<Page>> normalPages;
    private LiveData<List<Page>> secretPages;

    public PageViewModel(@NonNull Application application) {
        super(application);
        repository = new PageRepository(application);
        normalPages = repository.getNormalPages();
        secretPages = repository.getSecretPages();
    }

    public void insert(Page page) { repository.insert(page); }
    public void update(Page page) { repository.update(page); }
    public void delete(Page page) { repository.delete(page); }

    public LiveData<List<Page>> getNormalPages() { return normalPages; }

    public LiveData<List<Page>> getSecretPages() { return secretPages; }
}