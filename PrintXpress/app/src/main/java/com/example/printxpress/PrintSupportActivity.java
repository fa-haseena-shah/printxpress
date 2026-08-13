package com.example.printxpress;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PrintSupportActivity extends AppCompatActivity {

    Button btnEmail, btnSms, btnCall, btnFaq1, btnFaq2, btnFaq3, btnFaq4;
    LinearLayout panel1, panel2, panel3, panel4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_print_support);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // nav bar
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        BottomNavHelper.setupNav(this, bottomNav, R.id.nav_help);

        btnEmail = findViewById(R.id.btnEmail);
        btnSms = findViewById(R.id.btnSms);
        btnCall = findViewById(R.id.btnCall);
        btnFaq1 = findViewById(R.id.btnFaq1);
        btnFaq2 = findViewById(R.id.btnFaq2);
        btnFaq3 = findViewById(R.id.btnFaq3);
        btnFaq4 = findViewById(R.id.btnFaq4);

        panel1 = findViewById(R.id.panel1);
        panel2 = findViewById(R.id.panel2);
        panel3 = findViewById(R.id.panel3);
        panel4 = findViewById(R.id.panel4);

        btnFaq1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePanel(panel1, panel2, panel3, panel4);
            }
        });

        btnFaq2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePanel(panel2, panel1, panel3, panel4);
            }
        });

        btnFaq3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePanel(panel3, panel1, panel2, panel4);
            }
        });

        btnFaq4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePanel(panel4, panel1, panel2, panel3);
            }
        });

        // implicit intents
        // send email
        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"printxpresslk@gmail.com"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Customer Inquiry |");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi, can I know...");
                startActivity(emailIntent);
            }
        });

        // send sms
        btnSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                smsIntent.setData(Uri.parse("smsto:"+"+94304890376"));
                smsIntent.putExtra("sms-body", "Hi, can I know...");
                startActivity(smsIntent);
            }
        });

        // call
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:+94304890376"));
                startActivity(callIntent);
            }
        });
    }

    // helper to make accordion function
    private void togglePanel(LinearLayout selected, LinearLayout... others) {
        for (LinearLayout panel : others) {
            panel.setVisibility(View.GONE);
        }
        if (selected.getVisibility() == View.VISIBLE) {
            selected.setVisibility(View.GONE);
        } else {
            selected.setVisibility(View.VISIBLE);
        }
    }
}