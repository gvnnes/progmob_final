package com.example.diariodebolso.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.diariodebolso.R;
import com.example.diariodebolso.service.DiaryService;

public class AddEntryActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextContent;
    private Button buttonSave;
    private DiaryService diaryService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);

        diaryService = new DiaryService(this);
        editTextTitle = findViewById(R.id.editTextEntryTitle);
        editTextContent = findViewById(R.id.editTextEntryContent);
        buttonSave = findViewById(R.id.buttonSaveEntry);

        buttonSave.setOnClickListener(v -> {
            String title = editTextTitle.getText().toString().trim();
            String content = editTextContent.getText().toString().trim();

            if (diaryService.createEntry(title, content)) {
                Toast.makeText(this, "Entrada salva com sucesso!", Toast.LENGTH_SHORT).show();
                finish(); // Fecha a tela e volta para a MainActivity
            } else {
                Toast.makeText(this, "O título é obrigatório.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}