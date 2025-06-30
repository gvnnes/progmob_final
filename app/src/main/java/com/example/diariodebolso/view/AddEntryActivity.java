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
import com.example.diariodebolso.service.DiaryService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class AddEntryActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextContent, editTextDate;
    private Button buttonSave, buttonAddPhoto, buttonAddLocation; // Botão de localização adicionado
    private DiaryService diaryService;
    private Calendar calendar;
    private Uri cameraImageUri;
    private Uri selectedImageUri = null;
    private long userId;
    private String locationString = null; // Variável para guardar a localização

    private ActivityResultLauncher<String> requestCameraPermissionLauncher;
    private ActivityResultLauncher<String> requestLocationPermissionLauncher; // Lançador para permissão de localização
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Uri> cameraLauncher;

    private FusedLocationProviderClient fusedLocationClient; // Cliente de localização

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);

        initializeLaunchers();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this); // Inicializa o cliente

        userId = getIntent().getLongExtra("USER_ID", -1);

        editTextTitle = findViewById(R.id.editTextEntryTitle);
        editTextContent = findViewById(R.id.editTextEntryContent);
        editTextDate = findViewById(R.id.editTextDate);
        buttonAddPhoto = findViewById(R.id.buttonAddPhoto);
        buttonAddLocation = findViewById(R.id.buttonAddLocation); // Referencia o botão
        buttonSave = findViewById(R.id.buttonSaveEntry);
        calendar = Calendar.getInstance();
        editTextDate.setInputType(InputType.TYPE_NULL);
        diaryService = new DiaryService(this);

        buttonAddPhoto.setOnClickListener(v -> selectImage());

        buttonAddLocation.setOnClickListener(v -> checkLocationPermission()); // Ação para o botão

        editTextDate.setOnClickListener(view -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            new DatePickerDialog(
                    AddEntryActivity.this,
                    (datePicker, year1, month1, dayOfMonth) -> {
                        String selectedDate = String.format("%02d/%02d/%d", dayOfMonth, month1 + 1, year1);
                        editTextDate.setText(selectedDate);
                    },
                    year, month, day
            ).show();
        });

        buttonSave.setOnClickListener(v -> {
            String title = editTextTitle.getText().toString().trim();
            String content = editTextContent.getText().toString().trim();
            String date = editTextDate.getText().toString().trim();
            String imagePath = selectedImageUri != null ? selectedImageUri.toString() : "";

            if (diaryService.createEntry(title, content, date, imagePath, locationString, userId)) { // Passa a localização
                Toast.makeText(this, "Entrada salva com sucesso!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "O título é obrigatório.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeLaunchers() {
        requestCameraPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                openCamera();
            } else {
                Toast.makeText(this, "Permissão da câmera negada", Toast.LENGTH_SHORT).show();
            }
        });

        // Lançador para permissão de localização
        requestLocationPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permissão de localização negada", Toast.LENGTH_SHORT).show();
            }
        });

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        Toast.makeText(this, "Imagem selecionada", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                result -> {
                    if (result) {
                        selectedImageUri = cameraImageUri;
                        Toast.makeText(this, "Foto capturada", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void selectImage() {
        final CharSequence[] options = {"Câmera", "Galeria"};
        new AlertDialog.Builder(this)
                .setTitle("Selecione uma opção")
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
                        Toast.makeText(this, "Localização capturada!", Toast.LENGTH_SHORT).show();
                        buttonAddLocation.setText("Localização Adicionada");
                    } else {
                        Toast.makeText(this, "Não foi possível obter a localização. Tente novamente.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openCamera() {
        try {
            File photoFile = File.createTempFile("photo_", ".jpg", getExternalFilesDir("Pictures"));
            cameraImageUri = FileProvider.getUriForFile(
                    this,
                    "com.example.diariodebolso.fileprovider",
                    photoFile
            );
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
}