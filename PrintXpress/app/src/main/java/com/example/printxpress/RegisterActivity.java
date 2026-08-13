package com.example.printxpress;

import android.content.ContentValues;
import android.content.Intent;
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

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    EditText etName, etPhone, etEmail, etPassword, etConfirmPw;
    Button btnRegister;
    TextView tvError, tvLogin;
    DatabaseHelper dbHelper;
    ValidationHelper validationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPw = findViewById(R.id.etConfirmPw);
        btnRegister = findViewById(R.id.btnRegister);
        tvError = findViewById(R.id.tvError);
        tvLogin = findViewById(R.id.tvLogin);
        dbHelper = new DatabaseHelper(this);
        validationHelper = new ValidationHelper();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString();
                String phone = etPhone.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirmPw = etConfirmPw.getText().toString().trim();

                // simple input validations + regex validation for format checks
                if(name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPw.isEmpty()){
                    tvError.setText("Please fill all fields!");
                } else if(!password.equals(confirmPw)){
                    tvError.setText("Passwords do not match!");
                } else if(!validationHelper.validateEmail(email)){
                    tvError.setText("Invalid email address!");
                } else if (!validationHelper.validatePhone(phone)) {
                    tvError.setText("Invalid phone number!");
                } else if(dbHelper.isEmailRegistered(email)) {
                    tvError.setText("Email is already registered!");
                } else if(dbHelper.isPhoneRegistered(phone)) {
                    tvError.setText("Phone number is already registered!");
                } else if(!validationHelper.validatePasswordStrength(password)) {
                    tvError.setText("Password too weak!");
                }
                else {
                    long newRowId = dbHelper.insertCustomer(name, email, phone, password);
                    if(newRowId != -1){
                        Toast.makeText(RegisterActivity.this, "Registration Successful!",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    } else {
                        tvError.setText("Registration Failed!");
                    }
                }
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}