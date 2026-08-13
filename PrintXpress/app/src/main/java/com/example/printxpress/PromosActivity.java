package com.example.printxpress;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class PromosActivity extends AppCompatActivity {
    PromoHelper promoHelper;
    ListView lvPromotions;
    TextView tvBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_promos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        promoHelper = new PromoHelper();
        lvPromotions = findViewById(R.id.lvPromotions);
        tvBack = findViewById(R.id.tvBack);

        List<Promo> validPromos = promoHelper.getValidPromos();
        PromoAdapter adapter = new PromoAdapter(this, validPromos);
        lvPromotions.setAdapter(adapter);

        // back button
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });

        lvPromotions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Promo promo = ((PromoAdapter) parent.getAdapter()).getItem(position);
                Toast.makeText(
                        PromosActivity.this, promo.getCode() + " • " +
                                (int) promo.getDiscountPercent() + "% OFF\nValid until " + promo.getValidTill(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}