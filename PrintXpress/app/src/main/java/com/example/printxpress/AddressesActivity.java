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

public class AddressesActivity extends AppCompatActivity {
    ListView lvAddresses;
    TextView tvBack;
    DatabaseHelper databaseHelper;
    AddressAdapter addressAdapter;
    List<Address> addressList;
    private int cusId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_addresses);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        lvAddresses = findViewById(R.id.lvAddresses);
        databaseHelper = new DatabaseHelper(this);
        tvBack = findViewById(R.id.tvBack);

        // back button
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });
        cusId = getIntent().getIntExtra("customer_id", -1);
        loadAddresses(); // set existing ones to view
    }

    private void loadAddresses() {
        addressList = AddressHelper.loadAddresses(databaseHelper, cusId);
        addressAdapter = new AddressAdapter(this, addressList, address -> {
            boolean deleted = AddressHelper.deleteAddress(databaseHelper, address.getAddressId());
            if (deleted) {
                Toast.makeText(AddressesActivity.this, "Address removed", Toast.LENGTH_SHORT).show();
                loadAddresses();
            } else {
                Toast.makeText(AddressesActivity.this, "Sorry! Address used in order...", Toast.LENGTH_LONG).show();
            }
        });
        lvAddresses.setAdapter(addressAdapter);
    }
}