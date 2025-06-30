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
    private Button buttonSave, buttonAddPhoto, buttonAddLocation;
    private DiaryService diaryService;
    private Calendar calendar;
    private Uri cameraImageUri;
    private Uri selectedImageUri = null;
    private long userId;
    private String locationString = null;

    private ActivityResultLauncher<String> requestCameraPermissionLauncher;
    private ActivityResultLauncher<String> requestLocationPermissionLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Uri> cameraLauncher;

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);

        initializeLaunchers();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        userId = getIntent().getLongExtra("USER_ID", -1);

        editTextTitle = findViewById(R.id.editTextEntryTitle);
        editTextContent = findViewById(R.id.editTextEntryContent);
        editTextDate = findViewById(R.id.editTextDate);
        buttonAddPhoto = findViewById(R.id.buttonAddPhoto);
        buttonAddLocation = findViewById(R.id.buttonAddLocation);
        buttonSave = findViewById(R.id.buttonSaveEntry);
        calendar = Calendar.getInstance();
        editTextDate.setInputType(InputType.TYPE_NULL);
        diaryService = new DiaryService(this);

        buttonAddPhoto.setOnClickListener(v -> selectImage());
        buttonAddLocation.setOnClickListener(v -> checkLocationPermission());

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

            if (diaryService.createEntry(title, content, date, imagePath, locationString, userId)) {
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
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            // --- INÍCIO DA CORREÇÃO ---
                            // Pede permissão de leitura persistente para este URI
                            final int takeFlags = result.getData().getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            getContentResolver().takePersistableUriPermission(uri, takeFlags);
                            // --- FIM DA CORREÇÃO ---
                            selectedImageUri = uri;
                            Toast.makeText(this, "Imagem selecionada", Toast.LENGTH_SHORT).show();
                        }
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
        // --- ALTERADO PARA ACTION_OPEN_DOCUMENT ---
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }
}