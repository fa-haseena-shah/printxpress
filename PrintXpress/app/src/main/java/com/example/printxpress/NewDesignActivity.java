package com.example.printxpress;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class NewDesignActivity extends AppCompatActivity {
    Button btnAddDesign, btnConfirmDesign;
    EditText etDesignTitle;
    ImageView imgPreview;
    TextView tvBack;
    private ActivityResultLauncher<Intent> filePickerLauncher;
    private int cusId;
    private boolean comingFromDesign;
    private String savedFilePath;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_design);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnAddDesign = findViewById(R.id.btnAddDesign);
        etDesignTitle = findViewById(R.id.etDesignTitle);
        btnConfirmDesign = findViewById(R.id.btnConfirmDesign);
        tvBack = findViewById(R.id.tvBack);
        imgPreview = findViewById(R.id.imgPreview);
        databaseHelper = new DatabaseHelper(this);

        cusId = getIntent().getIntExtra("customer_id", -1);
        // if enteriing from SelectDesignActivity, this value would be true
        comingFromDesign = getIntent().getBooleanExtra("select_design", false);

        // back button
        if(comingFromDesign) { // use that value to navigate back to SelectDesignActivity
            tvBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), SelectDesignActivity.class);
                    startActivity(intent);
                    finish(); // // so that user doesnt end in a loop
                }
            });
        } else {
            tvBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    startActivity(intent);
                    finish(); // so that user doesnt end in a loop
                }
            });
        }

        registerFilePickerLauncher();

        btnAddDesign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });
        btnConfirmDesign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDesign();
            }
        });
    }

    private void registerFilePickerLauncher() {
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri fileUri = result.getData().getData();
                        if (fileUri != null) {
                            try {
                                savedFilePath = saveFileToInternalStorage(fileUri);
                                imgPreview.setImageURI(Uri.parse(savedFilePath));
                            } catch (Exception e) {
                                Toast.makeText(NewDesignActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        filePickerLauncher.launch(Intent.createChooser(intent, "Select Artwork"));
    }

    private String saveFileToInternalStorage(Uri uri) throws Exception {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        // giving a unique name to the file path
        String fileName = "design_" + cusId + "_" + System.currentTimeMillis() + ".png";
        File destinationFile = new File(getFilesDir(), fileName);
        FileOutputStream outputStream = new FileOutputStream(destinationFile);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

        outputStream.flush();
        outputStream.close();
        inputStream.close();

        return destinationFile.getAbsolutePath();
    }

    private void saveDesign() {
        // validations
        if (savedFilePath == null) {
            Toast.makeText(NewDesignActivity.this, "Please select an image first", Toast.LENGTH_SHORT).show();
            return;
        }

        String title = etDesignTitle.getText().toString().trim();
        if (title.isEmpty()) {
            title = "Design " + System.currentTimeMillis(); // if user skips naming it, give it a default name
        }

        long newId = databaseHelper.insertDesign(title, savedFilePath, cusId);
        if (newId != -1) {
            Toast.makeText(NewDesignActivity.this, "Design saved!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(NewDesignActivity.this, "Failed to save design", Toast.LENGTH_SHORT).show();
        }
    }
}