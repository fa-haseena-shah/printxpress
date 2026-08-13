package com.example.printxpress;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DeliveryActivity extends AppCompatActivity {

    EditText etHouseNo, etStreetName, etSuburb;
    Spinner citySpinner;
    Button btnConfirmCollection;
    TextView tvAddAddress, tvDateTime, tvDelFee, tvSelectedAddress;
    ListView lvAddresses;
    LinearLayout llAddressSection;
    RadioGroup radioGroup;
    RadioButton rbDelivery, rbPickUp;
    AddressAdapter addressAdapter;
    List<Address> addressList;
    DatabaseHelper databaseHelper;
    ValidationHelper validationHelper;
    private int cusId, orderId, productId, qty, selectedDesignId;
    private int selectedAddressId = -1;
    private double selectedDelFee = -1;
    private String customText;
    private double unitPrice;
    private ArrayList<Integer> specIds;

    // stored in regular hashmap as key=> value pairs used to display delivery fee
    // per city : order is not important
    public static final Map<String, Double> CITY_FEES = new HashMap<>() {{
        put("colombo", 250.0);
        put("dehiwala", 250.0);
        put("kotte", 300.0);
        put("maharagama", 300.0);
        put("moratuwa", 350.0);
        put("negombo", 400.0);
        put("gampaha", 400.0);
        put("kalutara", 450.0);
        put("panadura", 450.0);
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_delivery);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvDateTime = findViewById(R.id.tvDateTime);
        tvDelFee = findViewById(R.id.tvDelFee);
        etHouseNo = findViewById(R.id.etHouseNo);
        etStreetName = findViewById(R.id.etStreet);
        etSuburb = findViewById(R.id.etSuburb);
        citySpinner = findViewById(R.id.citySpinner);
        tvAddAddress = findViewById(R.id.tvAddAddress);
        btnConfirmCollection = findViewById(R.id.btnConfirmCollection);
        lvAddresses = findViewById(R.id.lvAddresses);
        tvSelectedAddress = findViewById(R.id.tvSelectedAddress);
        llAddressSection = findViewById(R.id.llAddressSection);
        radioGroup = findViewById(R.id.radioGroup);
        rbDelivery = findViewById(R.id.rbDelivery);
        rbPickUp = findViewById(R.id.rbPickUp);
        databaseHelper = new DatabaseHelper(this);
        validationHelper = new ValidationHelper();

        // get all order + customer related details from intent
        SharedPreferences sharedPreferences = getSharedPreferences("cusPrefs", MODE_PRIVATE);
        cusId = sharedPreferences.getInt("customerId", -1);
        productId = getIntent().getIntExtra("product_id", -1);
        qty = getIntent().getIntExtra("qty", 0);
        customText = getIntent().getStringExtra("custom_text");
        selectedDesignId = getIntent().getIntExtra("design_id", -1);
        unitPrice = getIntent().getDoubleExtra("unit_price", 0);
        specIds = getIntent().getIntegerArrayListExtra("spec_ids");

        // initiallly the linear layout containing address related stuff (saved + new address fields)
        // is set to be not visible
        llAddressSection.setVisibility(View.GONE);

        // collection option radio buttons
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbDelivery) { // if delivery button chosen -> address section visible
                llAddressSection.setVisibility(View.VISIBLE);
                loadAddresses(); // set existing addresses to view
            } else if (checkedId == R.id.rbPickUp) { // if pickup -> addrss section not visible
                llAddressSection.setVisibility(View.GONE);
                selectedAddressId = -1; // default vals
                selectedDelFee = -1;
                // in case user selects pickup after selecting delivery, clear these interface-only infos
                // just for better ux
                tvSelectedAddress.setText("");
                tvDelFee.setText("");
            }
        });

        // add address
        tvAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String houseNo = etHouseNo.getText().toString().trim();
                String streetName = etStreetName.getText().toString().trim();
                String suburb = etSuburb.getText().toString().trim();
                String city = citySpinner.getSelectedItem().toString();

                AddressHelper.addAddress(databaseHelper, cusId, houseNo, streetName, suburb, city,
                        new AddressHelper.AddressCallback() {
                            @Override
                            public void onSuccess() {
                                AddressHelper.clearAddressForm(etHouseNo, etStreetName, etSuburb, citySpinner);
                                loadAddresses();
                                Toast.makeText(DeliveryActivity.this, "New address added!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(String message) {
                                Toast.makeText(DeliveryActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
        });

        // date and time dialog-picker pops up
        tvDateTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar calendar = Calendar.getInstance();
                        DatePickerDialog datePicker = new DatePickerDialog(
                                DeliveryActivity.this,
                                (view, year, month, dayOfMonth) -> {
                                    TimePickerDialog timePicker = new TimePickerDialog(
                                            DeliveryActivity.this,
                                            (timeView, hourOfDay, minute) -> {
                                                String dateTime = String.format(
                                                        Locale.getDefault(),
                                                        "%02d/%02d/%04d  %02d:%02d",
                                                        dayOfMonth, month + 1, year, hourOfDay, minute
                                                );
                                                tvDateTime.setText(dateTime);
                                            },
                                            calendar.get(Calendar.HOUR_OF_DAY),
                                            calendar.get(Calendar.MINUTE),
                                            false
                                    );
                                    timePicker.show();
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                        );
                        datePicker.show();
                    }
                });

        btnConfirmCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmCollection();
            }
        });
}

    private void loadAddresses() {
        addressList = AddressHelper.loadAddresses(databaseHelper, cusId);
        // set showRemoveButtton in AddressAdapter's constructor as false so users cant delete addresses from here
        addressAdapter = new AddressAdapter(this, addressList, null, false);
        lvAddresses.setAdapter(addressAdapter);

        lvAddresses.setOnItemClickListener((parent, view, position, id) -> {
            Address selected = addressList.get(position);
            selectedAddressId = selected.getAddressId();

            // use the hashmap to dynamically display del fee depending on city
            Double fee = CITY_FEES.get(selected.getCity().toLowerCase());
            selectedDelFee = (fee != null) ? fee : -1;
            tvDelFee.setText(fee != null ? "Delivery Fee: LKR " + fee : "Unable to calculate fee");
            tvSelectedAddress.setText("Address: " + selected.addressDisplay());
            Toast.makeText(DeliveryActivity.this, "Selected: " + selected.addressDisplay(), Toast.LENGTH_SHORT).show();
        });
    }

    private void confirmCollection() {
        // date and time validations
        String dateTime = tvDateTime.getText().toString().trim();
        if (dateTime.isEmpty()) {
            Toast.makeText(this, "Please select a date & time", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            Date selectedDate = sdf.parse(dateTime);
            Calendar now = Calendar.getInstance();

            if (selectedDate.before(now.getTime())) {
                Toast.makeText(this, "Cannot choose past date", Toast.LENGTH_SHORT).show();
                return;
            }
            // time limit (cannot accept immediate requests)
            Calendar minAllowed = Calendar.getInstance();
            minAllowed.add(Calendar.HOUR_OF_DAY, 1);
            if (selectedDate.before(minAllowed.getTime())) {
                Toast.makeText(this, "You must select at least 1 hour from now", Toast.LENGTH_SHORT).show();
                return;
            }
            // working hours only check
            Calendar selectedCal = Calendar.getInstance();
            selectedCal.setTime(selectedDate);
            int hour = selectedCal.get(Calendar.HOUR_OF_DAY);
            if (hour < 8 || hour >= 18) {
                Toast.makeText(this, "Collections are available between 8AM and 6PM", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (ParseException e) { // catch & handle any parse exceptions that may be thrown
            Toast.makeText(this, "Something went wrong when selecting collection date",Toast.LENGTH_SHORT).show();
            return;
        }

        // check if collection type was input,
        int checkedId = radioGroup.getCheckedRadioButtonId();
        if (checkedId == -1) {
            Toast.makeText(DeliveryActivity.this, "Please select delivery or pickup", Toast.LENGTH_SHORT).show();
            return;
        }

        String deliveryType = (checkedId == R.id.rbDelivery) ? "Delivery" : "Pickup";
        if (deliveryType.equals("Delivery")) {
            if (selectedAddressId == -1) {
                Toast.makeText(DeliveryActivity.this, "Please select a delivery address", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedDelFee == -1) {
                Toast.makeText(DeliveryActivity.this, "We can't deliver to the selected address", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        int addressIdForDB = deliveryType.equals("Pickup") ? -1 : selectedAddressId;
        // separate variables since usage of -1 affects calculations,
        double delFeeForDB = deliveryType.equals("Pickup") ? -1 : selectedDelFee;
        double delFeeForCalc = deliveryType.equals("Pickup") ? 0 : selectedDelFee;

        OrderItem orderItem = new OrderItem(productId, selectedDesignId, customText, qty, unitPrice);
        Delivery delivery = new Delivery(addressIdForDB, deliveryType, dateTime, delFeeForDB);

        int orderId = databaseHelper.insertCompleteOrder(cusId, orderItem, delivery, specIds);

        if (orderId != -1) {
            double totalAmount = (unitPrice * qty) + delFeeForCalc;
            Toast.makeText(DeliveryActivity.this, "Order confirmed!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), OrderSuccessActivity.class);
            intent.putExtra("order_id", orderId);
            intent.putExtra("order_total", unitPrice * qty);
            intent.putExtra("del_fee", delFeeForDB);
            intent.putExtra("total_amount", totalAmount);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Failed to place order. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}