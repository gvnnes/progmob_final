package com.example.diariodebolso.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.diariodebolso.model.DiaryEntry;
import com.example.diariodebolso.model.User;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "diario.db";
    private static final int DATABASE_VERSION = 1;

    // Tabela de Usuários
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USER_NAME = "username";
    private static final String COLUMN_USER_PASSWORD_HASH = "password_hash";
    private static final String COLUMN_USER_PHOTO = "photo_path";

    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USER_NAME + " TEXT UNIQUE,"
            + COLUMN_USER_PASSWORD_HASH + " TEXT,"
            + COLUMN_USER_PHOTO + " TEXT" + ")";

    // Tabela de Entradas do Diário
    private static final String TABLE_ENTRIES = "entries";
    private static final String COLUMN_ENTRY_ID = "id";
    private static final String COLUMN_ENTRY_TITLE = "title";
    private static final String COLUMN_ENTRY_CONTENT = "content";
    private static final String COLUMN_ENTRY_PHOTO = "photo_path";
    private static final String COLUMN_ENTRY_LOCATION = "location";
    private static final String COLUMN_ENTRY_DATE = "date";
    private static final String COLUMN_ENTRY_USER_ID = "user_id";

    private static final String CREATE_TABLE_ENTRIES = "CREATE TABLE " + TABLE_ENTRIES + "("
            + COLUMN_ENTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_ENTRY_TITLE + " TEXT,"
            + COLUMN_ENTRY_CONTENT + " TEXT,"
            + COLUMN_ENTRY_PHOTO + " TEXT,"
            + COLUMN_ENTRY_LOCATION + " TEXT,"
            + COLUMN_ENTRY_DATE + " TEXT,"
            + COLUMN_ENTRY_USER_ID + " INTEGER,"
            + "FOREIGN KEY(" + COLUMN_ENTRY_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENTRIES);
        onCreate(db);
    }

    // --- Métodos para Usuários ---

    public boolean addUser(String username, String passwordHash) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, username);
        values.put(COLUMN_USER_PASSWORD_HASH, passwordHash);
        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    public boolean updateUserPhoto(String username, String photoPath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_PHOTO, photoPath);
        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_USER_NAME + " = ?", new String[]{username});
        db.close();
        return rowsAffected > 0;
    }

    public User findUserByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null,
                COLUMN_USER_NAME + " = ?", new String[]{username},
                null, null, null);

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
            user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME)));
            user.setPasswordHash(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PASSWORD_HASH)));
            user.setPhotoPath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PHOTO)));
            cursor.close();
        }
        db.close();
        return user;
    }

    // --- Métodos para Entradas do Diário ---

    public boolean addEntry(DiaryEntry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ENTRY_TITLE, entry.getTitle());
        values.put(COLUMN_ENTRY_CONTENT, entry.getContent());
        values.put(COLUMN_ENTRY_PHOTO, entry.getPhotoPath());
        values.put(COLUMN_ENTRY_LOCATION, entry.getLocation());
        values.put(COLUMN_ENTRY_DATE, entry.getDate());
        values.put(COLUMN_ENTRY_USER_ID, entry.getUserId());

        long result = db.insert(TABLE_ENTRIES, null, values);
        db.close();
        return result != -1;
    }

    public List<DiaryEntry> getAllEntries() {
        List<DiaryEntry> entryList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ENTRIES + " ORDER BY " + COLUMN_ENTRY_ID + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                DiaryEntry entry = new DiaryEntry();
                entry.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ENTRY_ID)));
                entry.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ENTRY_TITLE)));
                entry.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ENTRY_CONTENT)));
                entry.setPhotoPath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ENTRY_PHOTO)));
                entry.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ENTRY_LOCATION)));
                entry.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ENTRY_DATE)));
                entry.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ENTRY_USER_ID)));
                entryList.add(entry);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return entryList;
    }

    public DiaryEntry getEntryById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ENTRIES, null,
                COLUMN_ENTRY_ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null);

        DiaryEntry entry = null;
        if (cursor != null && cursor.moveToFirst()) {
            entry = new DiaryEntry();
            entry.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ENTRY_ID)));
            entry.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ENTRY_TITLE)));
            entry.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ENTRY_CONTENT)));
            entry.setPhotoPath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ENTRY_PHOTO)));
            entry.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ENTRY_LOCATION)));
            entry.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ENTRY_DATE)));
            entry.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ENTRY_USER_ID)));
            cursor.close();
        }

        db.close();
        return entry;
    }

    public boolean updateEntry(DiaryEntry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ENTRY_TITLE, entry.getTitle());
        values.put(COLUMN_ENTRY_CONTENT, entry.getContent());
        values.put(COLUMN_ENTRY_PHOTO, entry.getPhotoPath());
        values.put(COLUMN_ENTRY_LOCATION, entry.getLocation());
        values.put(COLUMN_ENTRY_DATE, entry.getDate());

        int rows = db.update(TABLE_ENTRIES, values, COLUMN_ENTRY_ID + " = ?", new String[]{String.valueOf(entry.getId())});
        db.close();
        return rows > 0;
    }

    public boolean deleteEntryById(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_ENTRIES, COLUMN_ENTRY_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }
}
