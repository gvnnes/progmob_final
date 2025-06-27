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

    public boolean createEntry(String title, String content) {
        if (title == null || title.trim().isEmpty()) {
            return false; // Título é obrigatório
        }

        DiaryEntry entry = new DiaryEntry();
        entry.setTitle(title);
        entry.setContent(content);
        entry.setDate(new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date()));

        return dbHelper.addEntry(entry);
    }

    public List<DiaryEntry> getEntries() {
        return dbHelper.getAllEntries();
    }
}