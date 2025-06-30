public class ShowItensActivity {
    
}
package com.example.diariodebolso.view;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.view.View;
import android.widget.Button;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import com.example.diariodebolso.MainActivity;
import com.example.diariodebolso.R;
import com.example.diariodebolso.data.DatabaseHelper;
import com.example.diariodebolso.service.DiaryService;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class ShowItensActivity extends AppCompatActivity {

    private TextView textViewTitle;
    private TextView textViewDate;
    private DatabaseHelper dbHelper;
    private Button buttonEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_diary_entry); // Substitua pelo nome do seu layout XML

        // 1. Referenciar os TextViews
        textViewTitle = findViewById(R.id.textViewEntryTitle);
        textViewDate = findViewById(R.id.textViewEntryDate);

        // 2. Obter dados do banco
        dbHelper = new DatabaseHelper(this);
        Cursor cursor = (Cursor) dbHelper.getAllEntries();

        if (cursor.moveToFirst()) {
            // Pegue o primeiro registro (exemplo)
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));

            // 3. Atualizar os TextViews
            textViewTitle.setText(title);
            textViewDate.setText(date);
        }

        cursor.close();

        buttonEdit = findViewById(R.id.buttonEdit);

        buttonEdit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Aqui vai a ação ao clicar no botão Editar
                        Toast.makeText(
                                ShowItensActivity.this,
                                "Botão Editar clicado",
                                Toast.LENGTH_SHORT
                        ).show();
                        // Exemplo: abrir outra tela de edição
                        //Intent intent = new Intent(ShowItensActivity.this, EditActivity.class);
                        //startActivity(intent);
                    }
                }
        );
    }
}
