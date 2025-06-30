package com.example.diariodebolso.service;

import android.content.Context;
import com.example.diariodebolso.data.DatabaseHelper;
import com.example.diariodebolso.model.DiaryEntry;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DiaryService {
    private DatabaseHelper dbHelper;

    public DiaryService(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public boolean createEntry(String title, String content, String date, String photoPath, String location, long userId) {
        try {
            if (title == null || title.trim().isEmpty()) {
                return false;
            }

            String entryDate = (date != null && !date.isEmpty()) ? date :
                    new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

            DiaryEntry entry = new DiaryEntry(title.trim(), content != null ? content.trim() : "", entryDate);
            entry.setPhotoPath(photoPath);
            entry.setLocation(location);
            entry.setUserId(userId);

            return dbHelper.addEntry(entry);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<DiaryEntry> getEntriesByUserId(long userId) {
        return dbHelper.getEntriesByUserId(userId);
    }

    public List<DiaryEntry> getEntriesByDate(long userId, String date) {
        return dbHelper.getEntriesByDate(userId, date);
    }

    public boolean updateEntry(DiaryEntry entry) {
        if (entry == null || entry.getId() == 0 || entry.getTitle() == null || entry.getTitle().trim().isEmpty()) {
            return false;
        }
        return dbHelper.updateEntry(entry);
    }

    public boolean deleteEntry(long id) {
        return dbHelper.deleteEntryById(id);
    }

    public DiaryEntry getEntryById(long id) {
        return dbHelper.getEntryById(id);
    }
}