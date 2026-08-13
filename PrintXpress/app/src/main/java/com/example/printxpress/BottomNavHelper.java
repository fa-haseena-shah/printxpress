package com.example.printxpress;

import android.content.Intent;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavHelper {

    public static void setupNav(
            AppCompatActivity activity,
            BottomNavigationView bottomNav,
            int selectedItem) {

        bottomNav.setSelectedItemId(selectedItem);

        bottomNav.setOnItemSelectedListener(item -> {
            Intent intent = null;
            if (item.getItemId() == R.id.nav_home && selectedItem != R.id.nav_home) {
                intent = new Intent(activity, HomeActivity.class);
            }
            else if (item.getItemId() == R.id.nav_orders &&
                    selectedItem != R.id.nav_orders) {
                intent = new Intent(activity,
                        OrderHistoryActivity.class);
            }
            else if (item.getItemId() == R.id.nav_help &&
                    selectedItem != R.id.nav_help) {
                intent = new Intent(activity,
                        PrintSupportActivity.class);
            }
            else if (item.getItemId() == R.id.nav_profile &&
                    selectedItem != R.id.nav_profile) {
                intent = new Intent(activity,
                        ProfileActivity.class);
            }

            // if target activity already exists in the back stack, clear everything above
            // it and reuse that instance instead of stacking a new one
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(intent);
                activity.finish();

                // remove blink animation between activities
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // for newer api levels (higher than 34)
                    activity.overrideActivityTransition(AppCompatActivity.OVERRIDE_TRANSITION_OPEN, 0, 0);
                } else { // for lower than 34 api lvls
                    activity.overridePendingTransition(0, 0);
                }
            }
            return true;
        });
    }
}