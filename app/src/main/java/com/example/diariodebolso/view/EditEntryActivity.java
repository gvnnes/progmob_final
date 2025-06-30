package com.example.diariodebolso.view;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.diariodebolso.R;
import com.example.diariodebolso.model.DiaryEntry;
import com.example.diariodebolso.service.DiaryService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

public class EditEntryActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextContent, editTextDate;
    private Button buttonSave, buttonAddPhoto, buttonAddLocation;
    private DiaryService diaryService;
    private DiaryEntry currentEntry;
    private long entryId;
    private String locationString = null;
    private Uri selectedImageUri = null;
    private Uri cameraImageUri = null;

    private FusedLocationProviderClient fusedLocationClient;
    private ActivityResultLauncher<String> requestLocationPermissionLauncher;
    private ActivityResultLauncher<String> requestCameraPermissionLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Uri> cameraLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_entry);

        diaryService = new DiaryService(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        editTextTitle = findViewById(R.id.editTextEntryTitle);
        editTextContent = findViewById(R.id.editTextEntryContent);
        editTextDate = findViewById(R.id.editTextDate);
        buttonSave = findViewById(R.id.buttonSaveEntry);
        buttonAddPhoto = findViewById(R.id.buttonAddPhoto);
        buttonAddLocation = findViewById(R.id.buttonAddLocation);

        editTextDate.setInputType(InputType.TYPE_NULL);

        entryId = getIntent().getLongExtra("ENTRY_ID", -1);
        if (entryId == -1) {
            Toast.makeText(this, "Erro ao carregar entrada.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeLaunchers();
        loadEntryData();

        buttonAddPhoto.setOnClickListener(v -> selectImage()); // Adiciona a ação ao botão de foto
        buttonAddLocation.setOnClickListener(v -> checkLocationPermission());
        buttonSave.setOnClickListener(v -> saveChanges());

        editTextDate.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            new DatePickerDialog(
                    EditEntryActivity.this,
                    (datePicker, year1, month1, dayOfMonth) -> {
                        String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month1 + 1, year1);
                        editTextDate.setText(selectedDate);
                    },
                    year, month, day
            ).show();
        });
    }

    private void initializeLaunchers() {
        requestLocationPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permissão de localização negada", Toast.LENGTH_SHORT).show();
            }
        });

        requestCameraPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                openCamera();
            } else {
                Toast.makeText(this, "Permissão da câmera negada", Toast.LENGTH_SHORT).show();
            }
        });

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                selectedImageUri = result.getData().getData();
                Toast.makeText(this, "Nova imagem selecionada", Toast.LENGTH_SHORT).show();
                buttonAddPhoto.setText("Nova Foto Adicionada");
            }
        });

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
            if (result) {
                selectedImageUri = cameraImageUri;
                Toast.makeText(this, "Nova foto capturada", Toast.LENGTH_SHORT).show();
                buttonAddPhoto.setText("Nova Foto Capturada");
            }
        });
    }

    private void loadEntryData() {
        currentEntry = diaryService.getEntryById(entryId);
        if (currentEntry != null) {
            editTextTitle.setText(currentEntry.getTitle());
            editTextContent.setText(currentEntry.getContent());
            editTextDate.setText(currentEntry.getDate());
            locationString = currentEntry.getLocation();

            if (locationString != null && !locationString.isEmpty()) {
                buttonAddLocation.setText("Localização Salva");
            }
            if (currentEntry.getPhotoPath() != null && !currentEntry.getPhotoPath().isEmpty()){
                buttonAddPhoto.setText("Alterar Foto");
            }
        }
    }

    private void selectImage() {
        final CharSequence[] options = {"Câmera", "Galeria"};
        new AlertDialog.Builder(this)
                .setTitle("Adicionar Nova Foto")
                .setItems(options, (dialog, which) -> {
                    if (options[which].equals("Câmera")) {
                        checkCameraPermission();
                    } else if (options[which].equals("Galeria")) {
                        openGallery();
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

    private void openCamera() {
        try {
            File photoFile = File.createTempFile("photo_edit_", ".jpg", getExternalFilesDir("Pictures"));
            cameraImageUri = FileProvider.getUriForFile(this, "com.example.diariodebolso.fileprovider", photoFile);
            cameraLauncher.launch(cameraImageUri);
        } catch (IOException ex) {
            ex.printStackTrace();
            Toast.makeText(this, "Erro ao criar arquivo da foto", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        locationString = location.getLatitude() + "," + location.getLongitude();
                        Toast.makeText(this, "Localização atualizada!", Toast.LENGTH_SHORT).show();
                        buttonAddLocation.setText("Localização Adicionada");
                    } else {
                        Toast.makeText(this, "Não foi possível obter a localização.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveChanges() {
        String title = editTextTitle.getText().toString().trim();
        String content = editTextContent.getText().toString().trim();
        String date = editTextDate.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "O título é obrigatório.", Toast.LENGTH_SHORT).show();
            return;
        }

        currentEntry.setTitle(title);
        currentEntry.setContent(content);
        currentEntry.setDate(date);
        currentEntry.setLocation(locationString);
        // Se uma nova imagem foi selecionada, atualiza o caminho. Senão, mantém o antigo.
        if (selectedImageUri != null) {
            currentEntry.setPhotoPath(selectedImageUri.toString());
        }

        if (diaryService.updateEntry(currentEntry)) {
            Toast.makeText(this, "Entrada atualizada com sucesso!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Falha ao atualizar a entrada.", Toast.LENGTH_SHORT).show();
        }
    }
}