package com.example.diariodebolso.view;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import com.example.diariodebolso.R;
import com.example.diariodebolso.service.DiaryService;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class AddEntryActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;
    private static final int REQUEST_LOCATION_PERMISSION = 101;
    private static final int REQUEST_CAMERA_PERMISSION = 102;

    private EditText editTextTitle, editTextContent, editTextDate;
    private Button buttonSave, buttonAddPhoto, buttonAddLocation;
    private String photoPath;
    private ImageView imagePreview; // Adicione este ImageView no seu XML
    private DiaryService diaryService;
    private Calendar calendar;
    private Uri cameraImageUri;
    private Uri selectedImageUri = null;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Uri> cameraLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);

        editTextTitle = findViewById(R.id.editTextEntryTitle);
        editTextContent = findViewById(R.id.editTextEntryContent);
        editTextDate = findViewById(R.id.editTextDate);

        buttonAddPhoto = findViewById(R.id.buttonAddPhoto);
        buttonSave = findViewById(R.id.buttonSaveEntry);

        calendar = Calendar.getInstance();
        editTextDate.setInputType(InputType.TYPE_NULL); // evita teclado

        diaryService = new DiaryService(this);

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


        buttonAddPhoto.setOnClickListener(v -> {
            String[] options = { "Câmera", "Galeria" };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Selecione uma opção");
            builder.setItems(options, (dialog, which) -> {
                switch (which) {
                    case 0:
                        abrirCamera();
                        break;
                    case 1:
                        abrirGaleria();
                        break;
                }
            });
            builder.show();
        });

        // Adiciona um listener ao campo de data
        editTextDate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int day = calendar.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                AddEntryActivity.this,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(
                                            DatePicker view,
                                            int dayOfMonth,
                                            int month,
                                            int year
                                    ) {
                                        // Mês começa em 0, então adicionamos +1
                                        String selectedDate = String.format(
                                                "%02d/%02d/%d",
                                                dayOfMonth,
                                                month + 1,
                                                year
                                        );
                                        editTextDate.setText(selectedDate);
                                    }
                                },
                                day,
                                month,
                                year
                        );

                        datePickerDialog.show();
                    }
                }
        );

        // Adiciona um listener ao botão "Salvar"
        buttonSave.setOnClickListener(v -> {
            String title = editTextTitle.getText().toString().trim();
            String content = editTextContent.getText().toString().trim();
            String date = editTextDate.getText().toString().trim();
            String imagePath = selectedImageUri != null ? selectedImageUri.toString() : "";

            if (diaryService.createEntry(title, content, date, imagePath)) {
                Toast.makeText(this, "Entrada salva com sucesso!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "O título é obrigatório.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void abrirCamera() {
        try {
            File storageDir = new File(getExternalCacheDir(), "images");
            if (!storageDir.exists()) storageDir.mkdirs();

            File photoFile = File.createTempFile("photo_", ".jpg", storageDir);
            cameraImageUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider",
                    photoFile
            );

            cameraLauncher.launch(cameraImageUri);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

}
