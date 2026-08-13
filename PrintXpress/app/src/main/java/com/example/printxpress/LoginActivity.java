package com.example.printxpress;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    EditText etCredential, etPassword;
    Button btnLogin;
    TextView tvError, tvRegister;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etCredential = findViewById(R.id.etCredential);
        etPassword = findViewById(R.id.etPassword);
        tvError = findViewById(R.id.tvError);
        tvRegister = findViewById(R.id.tvRegister);
        btnLogin = findViewById(R.id.btnLogin);
        dbHelper = new DatabaseHelper(this);

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneEmail = etCredential.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                if(phoneEmail.isEmpty() || password.isEmpty()){
                    tvError.setText("Please fill in all fields!");
                } else {
                    Customer customer = dbHelper.getCustomerByCredential(phoneEmail, password);
                    if(customer!=null) {
                        Toast.makeText(LoginActivity.this, "Logged In!",
                                Toast.LENGTH_SHORT).show();
                        // we save in shared preferences which saves data in key=> value pairs
                        // we save important customer info that will be retrieved later
                        SharedPreferences sharedPreferences = getSharedPreferences("cusPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("customerId", customer.getId());
                        editor.putString("name", customer.getName());
                        editor.apply();

                        // navigating to home activity
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        tvError.setText("Invalid credentials...");
                    }
                }
            }
        });
    }
}