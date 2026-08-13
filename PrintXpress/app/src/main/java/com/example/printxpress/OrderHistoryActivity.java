package com.example.printxpress;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderHistoryActivity extends AppCompatActivity {
    ListView lvOrders;
    DatabaseHelper databaseHelper;
    private int customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // btm navbar
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        BottomNavHelper.setupNav(this, bottomNav, R.id.nav_orders);

        lvOrders = findViewById(R.id.lvOrders);
        databaseHelper = new DatabaseHelper(this);
        SharedPreferences sharedPreferences = getSharedPreferences("cusPrefs", MODE_PRIVATE);
        customerId = sharedPreferences.getInt("customerId", -1);
        loadOrders();
    }

    // setting order history cards into list view using order adapter
    private void loadOrders() {
        List<HistoryHelper> orders = databaseHelper.getOrderHistoriesByCustomer(customerId);
        checkOrderStatusUpdates(orders); // generate toast dep on order status
        OrderAdapter adapter = new OrderAdapter(this, orders, new OrderAdapter.OnOrderActionListener() {
            @Override
            public void onCancel(HistoryHelper order) {
                AlertDialog.Builder builder = new AlertDialog.Builder(OrderHistoryActivity.this);
                builder.setMessage("Do you really want to cancel this order?");
                builder.setTitle("Order Cancellation");
                builder.setCancelable(false);

                // yes choice
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    boolean cancelled = databaseHelper.updateOrderStatus(order.getOrderId(), "cancelled");
                    if (cancelled) {
                        Toast.makeText(OrderHistoryActivity.this, "Order cancelled", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(OrderHistoryActivity.this, "Failed to cancel order", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss(); // close the dialog
                });

                // no choice
                builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss()); // dismiss(), not cancel() — see note below
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }

            @Override
            public void onReschedule(HistoryHelper order) {
                // reschedule through the same date time picker + validations we used in DeliveryActivity,
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePicker = new DatePickerDialog(OrderHistoryActivity.this,
                        (view, year, month, dayOfMonth) -> {
                            TimePickerDialog timePicker = new TimePickerDialog(
                                    OrderHistoryActivity.this,
                                    (timeView, hourOfDay, minute) -> {
                                        String newDateTime = String.format(Locale.getDefault(), "%02d/%02d/%04d  %02d:%02d", dayOfMonth, month + 1, year, hourOfDay, minute);
                                        try {
                                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                                            Date selectedDate = sdf.parse(newDateTime);
                                            Calendar now = Calendar.getInstance();
                                            assert selectedDate != null;
                                            if (selectedDate.before(now.getTime())) {
                                                Toast.makeText(OrderHistoryActivity.this, "Cannot choose past date", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            // time limit
                                            Calendar minAllowed = Calendar.getInstance();
                                            minAllowed.add(Calendar.HOUR_OF_DAY, 1);
                                            if (selectedDate.before(minAllowed.getTime())) {
                                                Toast.makeText(OrderHistoryActivity.this, "You must select at least 1 hour from now", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            // hours check
                                            Calendar selectedCal = Calendar.getInstance();
                                            selectedCal.setTime(selectedDate);
                                            int hour = selectedCal.get(Calendar.HOUR_OF_DAY);
                                            if (hour < 8 || hour >= 18) {
                                                Toast.makeText(OrderHistoryActivity.this, "Collections are available between 8AM and 6PM", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                        } catch (ParseException e) {
                                            Toast.makeText(OrderHistoryActivity.this, "Something went wrong when rescheduling the collection",Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        boolean updated = databaseHelper.updateDeliveryDateTime(order.getOrderId(), newDateTime);
                                        if (updated) {
                                            Toast.makeText(OrderHistoryActivity.this, "Order rescheduled to " + newDateTime, Toast.LENGTH_SHORT).show();
                                            loadOrders();
                                        } else {
                                            Toast.makeText(OrderHistoryActivity.this, "Failed to reschedule", Toast.LENGTH_SHORT).show();
                                        }
                                    },
                                    calendar.get(Calendar.HOUR_OF_DAY),
                                    calendar.get(Calendar.MINUTE),
                                    false
                            ); timePicker.show();
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );
                datePicker.show();
            }
        });
        lvOrders.setAdapter(adapter);
    }

    // for creating order progress notifications,
    private void checkOrderStatusUpdates(List<HistoryHelper> orders) {
        // a shared preferences to store the toasts which have been sent already,
        SharedPreferences prefs = getSharedPreferences("orderNotifyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // for each order (type is HistoryHelper so it will contain all
        // details regarding one order) returned,
        for (HistoryHelper order : orders) {
            String status = order.getStatus();
            // if status is not processing or cancelled (no need to send toasts),
            boolean isTrackedStatus = !status.equalsIgnoreCase("processing") && !status.equalsIgnoreCase("cancelled");
            if (isTrackedStatus) {
                String key = "notified_" + order.getOrderId() + "_" + status.toLowerCase(); // we put the status in the key so a NEW status for the same order can get its own toast
                // read whether this key exists and default to false (not yet notified)
                boolean alreadyNotified = prefs.getBoolean(key, false);

                // if alreadyNotified is false, create a toast and show it
                if (!alreadyNotified) {
                    Toast.makeText(OrderHistoryActivity.this, "Order #" + order.getOrderId() + " for " + order.getProductName() + " is " + status,
                            Toast.LENGTH_LONG).show();
                    editor.putBoolean(key, true); // set it to true now since it has been used in notification
                }
            }
        }
        editor.apply();
    }
}