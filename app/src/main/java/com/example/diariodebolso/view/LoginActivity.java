package com.example.diariodebolso.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.diariodebolso.MainActivity;
import com.example.diariodebolso.R;
import com.example.diariodebolso.service.AuthService;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUser, editTextPassword;
    private Button buttonLogin;
    private TextView createAccTextView;
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authService = new AuthService(this);

        editTextUser = findViewById(R.id.editTextUser);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        createAccTextView = findViewById(R.id.textViewCreateAcc);

        buttonLogin.setOnClickListener(v -> {
            String user = editTextUser.getText().toString().trim();
            String pass = editTextPassword.getText().toString().trim();

            if (authService.login(user, pass)) {
                Toast.makeText(this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Usuário ou senha inválidos", Toast.LENGTH_SHORT).show();
            }
        });

        createAccTextView.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}