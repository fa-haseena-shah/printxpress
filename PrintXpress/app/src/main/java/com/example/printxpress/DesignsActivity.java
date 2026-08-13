package com.example.printxpress;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class DesignsActivity extends AppCompatActivity {
    ListView lvDesigns;
    TextView tvBack;
    DatabaseHelper databaseHelper;
    DesignAdapter designAdapter;
    List<Design> designList;
    private int cusId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_designs);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        lvDesigns = findViewById(R.id.lvDesigns);
        databaseHelper = new DatabaseHelper(this);
        tvBack = findViewById(R.id.tvBack);

        // will be used as argument to get the customers existing designs
        cusId = getIntent().getIntExtra("customer_id", -1);
        loadDesigns(); // set existing ones to view
        // back button
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadDesigns() {
        // using cusId to get only their existing designs
        designList = databaseHelper.getCusDesigns(cusId);
        designAdapter = new DesignAdapter(this, designList, design -> {
            boolean deleted = databaseHelper.deleteDesign(design.getDesignId());
            if(deleted) { // deleted = true : design was deleted
                Toast.makeText(DesignsActivity.this, "Design removed", Toast.LENGTH_SHORT).show();
                loadDesigns();
            } else { // deleted = false : design referenced in order so cannot delete. show toast,
                Toast.makeText(DesignsActivity.this, "Sorry! " +
                        "Design used in order...", Toast.LENGTH_LONG).show();
            }
        });
        lvDesigns.setAdapter(designAdapter);
    }
}