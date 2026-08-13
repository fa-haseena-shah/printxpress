package com.example.printxpress;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

// similar to design section in ProfileActivity but we need to prevent the delete listener
// and make the listener capture user selection
public class SelectDesignActivity extends AppCompatActivity {
    TextView tvProductName;
    Button btnNewDesign;
    ListView lvDesigns;
    private DatabaseHelper databaseHelper;
    private List<Design> designList;
    private DesignAdapter designAdapter;
    private int customerId, productId;
    private ActivityResultLauncher<Intent> newDesignLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_design);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tvProductName = findViewById(R.id.tvProductName);
        btnNewDesign = findViewById(R.id.btnNewDesign);
        lvDesigns = findViewById(R.id.lvDesigns);
        databaseHelper = new DatabaseHelper(this);
        SharedPreferences sharedPreferences = getSharedPreferences("cusPrefs", MODE_PRIVATE);
        customerId = sharedPreferences.getInt("customerId", -1);

        // get product object to set the name
        productId = getIntent().getIntExtra("product_id",-1);
        Product product = databaseHelper.getProductById(productId);
        if(product!=null) {
            tvProductName.setText(product.getName());
        } else {
            tvProductName.setText(" ");
        }

        registerNewDesignLauncher();
        loadDesigns(); // set the designs

        btnNewDesign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewDesignActivity.class);
                intent.putExtra("customer_id", customerId);
                // this is needed so NewDesignActivity redirects back here
                intent.putExtra("select_design", true);
                newDesignLauncher.launch(intent);
            }
        });
    }

    private void registerNewDesignLauncher() {
        newDesignLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        loadDesigns(); // refresh so the newly uploaded design appears
                    }
                });
    }

    private void loadDesigns() {
        designList = databaseHelper.getCusDesigns(customerId);
        designAdapter = new DesignAdapter(this, designList, null, false);
        lvDesigns.setAdapter(designAdapter);

        // instead of making the click delete, we capture the user selection when the item is clicked,
        lvDesigns.setOnItemClickListener((parent, view, position, id) -> {
            Design selected = designList.get(position);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("design_id", selected.getDesignId());
            resultIntent.putExtra("design_path", selected.getFilePath());
            setResult(RESULT_OK, resultIntent);
            finish(); // go back to ProductOrderActivity
        });
    }
}