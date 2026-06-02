package com.example.noteapp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface PageDao {
    @Insert
    void insert(Page page);

    @Update
    void update(Page page);

    @Delete
    void delete(Page page);

    // Récupère les pages normales (non secrètes), triées de la plus récente à la plus ancienne
    @Query("SELECT * FROM pages_table WHERE isSecret = 0 ORDER BY timestamp DESC")
    LiveData<List<Page>> getNormalPages();

    // Récupère les pages secrètes
    @Query("SELECT * FROM pages_table WHERE isSecret = 1 ORDER BY timestamp DESC")
    LiveData<List<Page>> getSecretPages();
}