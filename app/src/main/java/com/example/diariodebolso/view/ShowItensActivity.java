package com.example.diariodebolso.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.diariodebolso.R;
import com.example.diariodebolso.model.DiaryEntry;
import com.example.diariodebolso.service.DiaryService;

public class ShowItensActivity extends AppCompatActivity {

    private TextView textViewTitle, textViewDate, textViewContent, textViewLocation;
    private ImageView imageViewPhoto;
    private Button buttonEdit, buttonDelete;
    private DiaryService diaryService;
    private long entryId = -1;
    private DiaryEntry currentEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_item);

        textViewTitle = findViewById(R.id.textViewEntryTitle);
        textViewDate = findViewById(R.id.textViewEntryDate);
        textViewContent = findViewById(R.id.textViewEntryContent);
        textViewLocation = findViewById(R.id.textViewEntryLocation);
        imageViewPhoto = findViewById(R.id.imageViewPhoto);
        buttonEdit = findViewById(R.id.buttonEdit);
        buttonDelete = findViewById(R.id.buttonDelete);

        diaryService = new DiaryService(this);
        entryId = getIntent().getLongExtra("ENTRY_ID", -1);

        if (entryId != -1) {
            loadEntryDetails();
        } else {
            Toast.makeText(this, "Erro: ID da entrada não encontrado.", Toast.LENGTH_SHORT).show();
            finish();
        }

        buttonEdit.setOnClickListener(v -> {
            Intent intent = new Intent(ShowItensActivity.this, EditEntryActivity.class);
            intent.putExtra("ENTRY_ID", entryId);
            startActivity(intent);
        });

        buttonDelete.setOnClickListener(v -> {
            if (diaryService.deleteEntry(entryId)) {
                Toast.makeText(this, "Entrada apagada com sucesso.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Erro ao apagar a entrada.", Toast.LENGTH_SHORT).show();
            }
        });

        textViewLocation.setOnClickListener(v -> openMap());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (entryId != -1) {
            loadEntryDetails();
        }
    }

    private void loadEntryDetails() {
        currentEntry = diaryService.getEntryById(entryId);
        if (currentEntry != null) {
            textViewTitle.setText(currentEntry.getTitle());
            textViewDate.setText(currentEntry.getDate());
            textViewContent.setText(currentEntry.getContent());

            if (currentEntry.getPhotoPath() != null && !currentEntry.getPhotoPath().isEmpty()) {
                imageViewPhoto.setImageURI(Uri.parse(currentEntry.getPhotoPath()));
                imageViewPhoto.setVisibility(View.VISIBLE);
            } else {
                imageViewPhoto.setVisibility(View.GONE);
            }

            if (currentEntry.getLocation() != null && !currentEntry.getLocation().isEmpty()) {
                textViewLocation.setText("Ver localização no mapa");
                textViewLocation.setVisibility(View.VISIBLE);
            } else {
                textViewLocation.setVisibility(View.GONE);
            }
        } else {
            Toast.makeText(this, "Entrada não encontrada.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void openMap() {
        if (currentEntry != null && currentEntry.getLocation() != null && !currentEntry.getLocation().isEmpty()) {
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + currentEntry.getLocation() + "(" + Uri.encode(currentEntry.getTitle()) + ")");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                Toast.makeText(this, "Google Maps não está instalado.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}