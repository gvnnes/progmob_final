package com.example.diariodebolso;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diariodebolso.model.DiaryEntry;
import com.example.diariodebolso.service.DiaryService;
import com.example.diariodebolso.util.NotificationUtils;
import com.example.diariodebolso.view.AddEntryActivity;
import com.example.diariodebolso.view.adapter.DiaryEntryAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DiaryEntryAdapter adapter;
    private DiaryService diaryService;
    private List<DiaryEntry> entryList = new ArrayList<>();
    private long userId = -1;

    private ProgressBar progressBar;
    private TextView textViewEmptyList;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    NotificationUtils.scheduleDailyReminder(this);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        askNotificationPermission();

        userId = getIntent().getLongExtra("USER_ID", -1);

        progressBar = findViewById(R.id.progressBar);
        textViewEmptyList = findViewById(R.id.textViewEmptyList);
        recyclerView = findViewById(R.id.recyclerViewEntries);
        FloatingActionButton fab = findViewById(R.id.fabAddEntry);
        diaryService = new DiaryService(this);

        setupRecyclerView();

        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddEntryActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DiaryEntryAdapter(entryList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAllEntries();
    }

    private void loadAllEntries() {
        showLoading(true);
        if (userId != -1) {
            new android.os.Handler().postDelayed(() -> {
                List<DiaryEntry> newEntries = diaryService.getEntriesByUserId(userId);
                updateUIWithEntries(newEntries);
            }, 500);
        }
    }

    private void loadEntriesByDate(String date) {
        showLoading(true);
        if (userId != -1) {
            new android.os.Handler().postDelayed(() -> {
                List<DiaryEntry> newEntries = diaryService.getEntriesByDate(userId, date);
                if (newEntries.isEmpty()) {
                    Toast.makeText(this, "Nenhuma entrada encontrada para esta data.", Toast.LENGTH_SHORT).show();
                }
                updateUIWithEntries(newEntries);
            }, 500);
        }
    }

    private void updateUIWithEntries(List<DiaryEntry> entries) {
        showLoading(false);
        if (entries.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            textViewEmptyList.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            textViewEmptyList.setVisibility(View.GONE);
            adapter.updateEntries(entries);
        }
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            textViewEmptyList.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_filter_by_date) {
            showDatePickerDialog();
            return true;
        } else if (id == R.id.action_clear_filter) {
            loadAllEntries();
            Toast.makeText(this, "Filtro limpo.", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month1 + 1, year1);
            loadEntriesByDate(selectedDate);
        }, year, month, day).show();
    }

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            } else {
                NotificationUtils.scheduleDailyReminder(this);
            }
        } else {
            NotificationUtils.scheduleDailyReminder(this);
        }
    }
}