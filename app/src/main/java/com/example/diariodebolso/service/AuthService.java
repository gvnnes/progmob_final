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

    /**
     * Registra um novo usuário no banco de dados.
     * @param username O nome de usuário.
     * @param password A senha em texto plano.
     * @return true se o registro for bem-sucedido, false caso contrário (ex: usuário já existe).
     */
    public boolean registerUser(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return false; // Falha na validação de campos vazios
        }

        // Verifica se o usuário já existe
        if (dbHelper.findUserByUsername(username) != null) {
            return false; // Usuário já cadastrado
        }

        String passwordHash = hashPassword(password);
        if (passwordHash == null) {
            return false; // Falha ao gerar o hash da senha
        }

        return dbHelper.addUser(username, passwordHash);
    }

    /**
     * Realiza o login de um usuário.
     * @param username O nome de usuário.
     * @param password A senha em texto plano.
     * @return true se o login for bem-sucedido, false caso contrário.
     */
    public boolean login(String username, String password) {
        User user = dbHelper.findUserByUsername(username);
        if (user == null) {
            return false; // Usuário não encontrado
        }

        String passwordHash = hashPassword(password);
        if (passwordHash == null) {
            return false; // Falha ao gerar o hash da senha
        }

        // Compara o hash da senha digitada com o hash salvo no banco
        return passwordHash.equals(user.getPasswordHash());
    }

    /**
     * Atualiza o caminho da foto de perfil de um usuário.
     * @param username O nome de usuário a ser atualizado.
     * @param photoPath O novo caminho para a foto.
     * @return true se a atualização for bem-sucedida, false caso contrário.
     */
    public boolean updateUserPhoto(String username, String photoPath) {
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
            // Em um app real, seria bom ter um log mais robusto aqui.
            e.printStackTrace();
            return null;
        }
    }
}