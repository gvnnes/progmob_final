package com.example.diariodebolso;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diariodebolso.model.DiaryEntry;
import com.example.diariodebolso.service.DiaryService;
import com.example.diariodebolso.view.AddEntryActivity;
import com.example.diariodebolso.view.adapter.DiaryEntryAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DiaryEntryAdapter adapter;
    private DiaryService diaryService;
    private List<DiaryEntry> entryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerViewEntries);
        FloatingActionButton fab = findViewById(R.id.fabAddEntry);
        diaryService = new DiaryService(this);

        // Configuração do RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DiaryEntryAdapter(entryList);
        recyclerView.setAdapter(adapter);

        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddEntryActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEntries();
    }

    private void loadEntries() {
        List<DiaryEntry> newEntries = diaryService.getEntries();
        adapter.updateEntries(newEntries);
    }
}