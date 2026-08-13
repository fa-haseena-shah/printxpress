package com.example.printxpress;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProductDetailActivity extends AppCompatActivity {
    TextView tvProductName, tvPrice, tvDescription, tvBack;
    Button btnPlaceOrder;
    ImageView imgSample;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvProductName = findViewById(R.id.tvProductName);
        tvPrice = findViewById(R.id.tvPrice);
        tvDescription = findViewById(R.id.tvDescription);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        imgSample = findViewById(R.id.imgSample);
        tvBack = findViewById(R.id.tvBack);
        databaseHelper = new DatabaseHelper(this);

        // get product id through intent passed from previous activity
        int productId = getIntent().getIntExtra("product_id", -1);
        // get specific product object using the product id
        Product product = databaseHelper.getProductById(productId);

        if (product != null) { // set details to display if product object was created
            tvProductName.setText(product.getName());
            tvPrice.setText("LKR " + product.getBasePrice());
            tvDescription.setText(product.getDescription());
            String imageName = product.getSample().replace(".png", "");
            int imageResId = getResources().getIdentifier(
                    imageName, "drawable", getPackageName()
            );
            imgSample.setImageResource(imageResId);
        } else {
            Toast.makeText(ProductDetailActivity.this, "Something went wrong...!", Toast.LENGTH_SHORT).show();
        }

        // back button to previous activity (home)
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProductOrderActivity.class);
                // pass current product id to ProductOrderActivity so specs can be fetched
                intent.putExtra("product_id", productId);
                startActivity(intent);
            }
        });
    }
}