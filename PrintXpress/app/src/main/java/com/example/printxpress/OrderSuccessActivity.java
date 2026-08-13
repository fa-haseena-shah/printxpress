package com.example.printxpress;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class OrderSuccessActivity extends AppCompatActivity {
    TextView tvOrderId, tvDelId, tvOrderTotal, tvDelFee, tvTotal;
    Button btnTrackOrder;
    private int orderId, deliveryId;
    private double orderTotal, delFee, total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_success);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tvOrderId = findViewById(R.id.tvOrderId);
        tvDelId = findViewById(R.id.tvDelId);
        tvOrderTotal = findViewById(R.id.tvOrderTotal);
        tvDelFee = findViewById(R.id.tvDelFee);
        tvTotal = findViewById(R.id.tvTotal);
        btnTrackOrder = findViewById(R.id.btnTrackOrder);
        orderId = getIntent().getIntExtra("order_id", -1);
        deliveryId = getIntent().getIntExtra("delivery_id", -1);
        orderTotal = getIntent().getDoubleExtra("order_total", -1);
        delFee = getIntent().getDoubleExtra("del_fee", -1);
        total = getIntent().getDoubleExtra("total_amount", -1);
        // log to debug
        Log.d("CONFIRM", "orderId" + orderId);
        Log.d("CONFIRM", "delId" + deliveryId);
        Log.d("CONFIRM", "orderTotal" + orderTotal);
        Log.d("CONFIRM", "delFee" + delFee);
        Log.d("CONFIRM", "total" + total);
        if(orderId != -1) {
            tvOrderId.setText("Order ID #" + orderId);
        }
        if(deliveryId != -1) {
            tvDelId.setText("Collection ID #" + deliveryId);
        }
        if(orderTotal!=-1) {
            tvOrderTotal.setText("Rs. " + String.valueOf(orderTotal));
        }
        if(delFee!=-1) {
            tvDelFee.setText("Rs. " + String.valueOf(delFee));
        } else {
            tvDelFee.setText("None");
        }
        if(total!=-1) {
            tvTotal.setText("Rs. " + String.valueOf(total));
        }
        btnTrackOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OrderHistoryActivity.class);
                startActivity(intent);
            }
        });
    }
}