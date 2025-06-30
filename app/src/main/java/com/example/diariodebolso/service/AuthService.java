package com.example.diariodebolso.service;

import android.content.Context;
import com.example.diariodebolso.data.DatabaseHelper;
import com.example.diariodebolso.model.User;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AuthService {

    private DatabaseHelper dbHelper;

    public AuthService(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public boolean registerUser(String username, String password) {
        // ... (código existente sem alterações)
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return false;
        }
        if (dbHelper.findUserByUsername(username) != null) {
            return false;
        }
        String passwordHash = hashPassword(password);
        if (passwordHash == null) {
            return false;
        }
        return dbHelper.addUser(username, passwordHash);
    }

    public User login(String username, String password) {
        User user = dbHelper.findUserByUsername(username);
        if (user == null) {
            return null;
        }

        String passwordHash = hashPassword(password);
        if (passwordHash == null) {
            return null;
        }

        if (passwordHash.equals(user.getPasswordHash())) {
            return user;
        }
        return null;
    }

    public boolean updateUserPhoto(String username, String photoPath) {
        // ... (código existente sem alterações)
        if (username == null || photoPath == null) {
            return false;
        }
        return dbHelper.updateUserPhoto(username, photoPath);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}