package com.example.diariodebolso.view;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.diariodebolso.R;
import com.example.diariodebolso.service.AuthService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextNewUser, editTextNewPassword;
    private ImageView imageViewProfile;
    private Button buttonRegister;
    private AuthService authService;
    private Uri imageUri;
    private String currentPhotoPath;

    private ActivityResultLauncher<String> requestCameraPermissionLauncher;
    private ActivityResultLauncher<String> requestGalleryPermissionLauncher;
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private ActivityResultLauncher<String> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeLaunchers();

        authService = new AuthService(this);
        editTextNewUser = findViewById(R.id.editTextNewUser);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        imageViewProfile = findViewById(R.id.imageViewProfile);
        buttonRegister = findViewById(R.id.buttonRegister);

        imageViewProfile.setOnClickListener(v -> selectImage());
        buttonRegister.setOnClickListener(v -> registerUser());
    }

    private void initializeLaunchers() {

        requestCameraPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                openCamera();
            } else {
                Toast.makeText(this, "Permissão da câmera negada", Toast.LENGTH_SHORT).show();
            }
        });

        requestGalleryPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                openGallery();
            } else {
                Toast.makeText(this, "Permissão da galeria negada", Toast.LENGTH_SHORT).show();
            }
        });

        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
            if (success && imageUri != null) {
                imageViewProfile.setImageURI(imageUri);
                currentPhotoPath = imageUri.getPath(); // O caminho já foi salvo ao criar o arquivo
            }
        });

        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                imageUri = uri;
                imageViewProfile.setImageURI(imageUri);

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    saveImageToInternalStorage(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void selectImage() {
        final CharSequence[] options = {"Tirar Foto", "Escolher da Galeria", "Cancelar"};
        new AlertDialog.Builder(this)
                .setTitle("Adicionar Foto")
                .setItems(options, (dialog, item) -> {
                    if (options[item].equals("Tirar Foto")) {
                        checkCameraPermission();
                    } else if (options[item].equals("Escolher da Galeria")) {
                        checkGalleryPermission();
                    }
                })
                .show();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void checkGalleryPermission() {
        String permission = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                ? Manifest.permission.READ_MEDIA_IMAGES
                : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            requestGalleryPermissionLauncher.launch(permission);
        }
    }

    private void openCamera() {
        imageUri = createImageUri();
        if (imageUri != null) {
            takePictureLauncher.launch(imageUri);
        }
    }

    private Uri createImageUri() {
        File imageFile = new File(getExternalFilesDir("Pictures"), "profile_image_" + System.currentTimeMillis() + ".jpg");
        currentPhotoPath = imageFile.getAbsolutePath();
        return FileProvider.getUriForFile(this, "com.example.diariodebolso.fileprovider", imageFile);
    }

    private void openGallery() {
        pickImageLauncher.launch("image/*");
    }

    private void saveImageToInternalStorage(Bitmap bitmap) {
        File directory = getApplicationContext().getDir("profile_images", Context.MODE_PRIVATE);
        String fileName = UUID.randomUUID().toString() + ".jpg";
        File mypath = new File(directory, fileName);

        try (FileOutputStream fos = new FileOutputStream(mypath)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos);
            this.currentPhotoPath = mypath.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Falha ao salvar a imagem", Toast.LENGTH_SHORT).show();
        }
    }

    private void registerUser() {
        String user = editTextNewUser.getText().toString().trim();
        String pass = editTextNewPassword.getText().toString().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Nome de usuário e senha são obrigatórios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (authService.registerUser(user, pass)) {
            if (currentPhotoPath != null) {
                authService.updateUserPhoto(user, currentPhotoPath);
            }
            Toast.makeText(this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Erro: Este nome de usuário já existe", Toast.LENGTH_SHORT).show();
        }
    }
}