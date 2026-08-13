package com.example.printxpress;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    EditText etNewName, etNewPhone, etNewEmail, etNewPass, etHouseNo, etStreetName, etSuburb;
    Spinner citySpinner;
    Button btnSave, btnNewDesign, btnViewAddress,btnViewDesigns, btnLogout;
    TextView tvAddAddress;
    DatabaseHelper databaseHelper;
    Spinner customerTypeSpinner;
    int cusId;
    String currentPassword, currentEmail, currentPhone;
    ValidationHelper validationHelper;
    private ActivityResultLauncher<Intent> newDesignLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etNewName = findViewById(R.id.etNewName);
        etNewEmail = findViewById(R.id.etNewEmail);
        etNewPhone = findViewById(R.id.etNewPhone);
        etNewPass = findViewById(R.id.etNewPass);
        customerTypeSpinner = findViewById(R.id.customerTypeSpinner);
        etHouseNo = findViewById(R.id.etHouseNo);
        etStreetName = findViewById(R.id.etStreet);
        etSuburb = findViewById(R.id.etSuburb);
        citySpinner = findViewById(R.id.citySpinner);
        btnSave = findViewById(R.id.btnSave);
        btnNewDesign = findViewById(R.id.btnNewDesign);
        btnViewAddress = findViewById(R.id.btnViewAddress);
        btnViewDesigns = findViewById(R.id.btnViewDesigns);
        btnLogout = findViewById(R.id.btnLogout);
        tvAddAddress = findViewById(R.id.tvAddAddress);

        databaseHelper = new DatabaseHelper(this);
        validationHelper = new ValidationHelper();

        // btm navbar
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        BottomNavHelper.setupNav(this, bottomNav, R.id.nav_profile);

        ///  manage customer profile
        // getting the user existing details from the shared prefs we stored earlier,
        SharedPreferences sharedPreferences = getSharedPreferences("cusPrefs", MODE_PRIVATE);
        cusId = sharedPreferences.getInt("customerId", -1);
        if(cusId!=-1) {
            Customer customer = databaseHelper.getCustomerInfo(cusId);
            // set the mandatory customer details by autofilling
            if(customer!=null) {
                etNewName.setText(customer.getName());
                etNewEmail.setText(customer.getEmail());
                etNewPhone.setText(customer.getPhone());
                currentPassword = customer.getPassword();
                // we get currentEmail and currentPhone to use for update profile checks
                currentEmail = customer.getEmail();
                currentPhone = customer.getPhone();
                // pre-select spinner option based on current type
                String currentType = customer.getType();
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.customer_types, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                customerTypeSpinner.setAdapter(adapter);
                int position = adapter.getPosition(currentType);
                if (currentType != null && currentType.equalsIgnoreCase("individual")) {
                    customerTypeSpinner.setSelection(position);
                }
            } else {
                etNewName.setText("");
                etNewEmail.setText("");
                etNewPhone.setText("");
            }
        }
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCustomerProfile();
                etNewPass.setText(""); // make it empty again once it has been saved (safety purpose)
            }
        });

        // ADDRESSES
        //  add addresses
        tvAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String houseNo = etHouseNo.getText().toString();
                String streetName = etStreetName.getText().toString();
                String suburb = etSuburb.getText().toString();
                String city = citySpinner.getSelectedItem().toString();

                AddressHelper.addAddress(databaseHelper, cusId, houseNo, streetName, suburb, city, new AddressHelper.AddressCallback() {
                    @Override
                    public void onSuccess() {
                        AddressHelper.clearAddressForm(etHouseNo, etStreetName, etSuburb, citySpinner);
                        Toast.makeText(ProfileActivity.this, "New address added!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        // open address list
        btnViewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddressesActivity.class);
                intent.putExtra("customer_id", cusId);
                startActivity(intent);
            }
        });

        // DESIGNS
        // open designs list
        btnViewDesigns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DesignsActivity.class);
                intent.putExtra("customer_id", cusId);
                startActivity(intent);
            }
        });
        // add design
        registerNewDesignLauncher();
        btnNewDesign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewDesignActivity.class);
                intent.putExtra("customer_id", cusId); // passing customer id in the intent to NewDesignActivity
                newDesignLauncher.launch(intent);
            }
        });

        /// manage logout
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                Toast.makeText(ProfileActivity.this, "You have logged out!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCustomerProfile() {
        String newName = etNewName.getText().toString().trim();
        String newEmail = etNewEmail.getText().toString().trim();
        String newPhone = etNewPhone.getText().toString().trim();
        String newPass = etNewPass.getText().toString().trim();
        String type = customerTypeSpinner.getSelectedItem().toString().toLowerCase();

        if (newName.isEmpty() || newEmail.isEmpty() || newPhone.isEmpty()) {
            Toast.makeText(ProfileActivity.this, "Do not leave empty fields!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!validationHelper.validateEmail(newEmail)) {
            Toast.makeText(ProfileActivity.this, "Invalid email!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!validationHelper.validatePhone(newPhone)) {
            Toast.makeText(ProfileActivity.this, "Invalid phone!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!newEmail.equals(currentEmail) && databaseHelper.isEmailRegistered(newEmail)) {
            Toast.makeText(ProfileActivity.this, "That email is already registered!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPhone.equals(currentPhone) && databaseHelper.isPhoneRegistered(newPhone)) {
            Toast.makeText(ProfileActivity.this, "That phone number is already registered!", Toast.LENGTH_SHORT).show();
            return;
        }

        // we don't autofill password so if user types something only, we check
        // password change is optional
        String passwordToSave = currentPassword;
        if (!newPass.isEmpty()) {
            if (newPass.equals(currentPassword)) {
                Toast.makeText(ProfileActivity.this, "New password must be different from current password!", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!validationHelper.validatePasswordStrength(newPass)) {
                Toast.makeText(ProfileActivity.this, "New password too weak!", Toast.LENGTH_SHORT).show();
                return;
            }
            passwordToSave = newPass;
        }

        boolean updated = databaseHelper.updateCustomer(cusId, newName, type, newEmail, newPhone, passwordToSave);
        if (updated) {
            Toast.makeText(ProfileActivity.this, "Changes saved!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ProfileActivity.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
        }
    }

    private void registerNewDesignLauncher() {
        newDesignLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Toast.makeText(ProfileActivity.this, "Design added!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}