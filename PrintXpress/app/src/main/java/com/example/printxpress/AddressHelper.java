package com.example.printxpress;

import android.content.Context;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

public class AddressHelper {

    public interface AddressCallback {
        void onSuccess();
        void onError(String message);
    }

    // validates the address fields and inserts if no errors
    public static void addAddress(DatabaseHelper db, int customerId, String houseNo, String streetName,
                                  String suburb, String city, AddressCallback callback) {
        if (houseNo.trim().isEmpty() || streetName.trim().isEmpty() || suburb.trim().isEmpty()) {
            callback.onError("Please fill in all address fields");
            return;
        }
        if (city == null || city.equals("Select City")) {
            callback.onError("Please select a city");
            return;
        }
        long result = db.insertAddress(customerId, houseNo.trim(), streetName.trim(), suburb.trim(), city);
        if (result != -1) {
            callback.onSuccess();
        } else {
            callback.onError("Something went wrong...");
        }
    }

    // load addresses
    public static List<Address> loadAddresses(DatabaseHelper db, int customerId) {
        return db.getCusAddresses(customerId);
    }

    public static boolean deleteAddress(DatabaseHelper db, int addressId) {
        return db.deleteAddress(addressId);
    }

    // resets the form fields after a successful insert
    public static void clearAddressForm(EditText etHouseNo, EditText etStreet, EditText
            etSuburb, Spinner citySpinner) {
        etHouseNo.setText("");
        etStreet.setText("");
        etSuburb.setText("");
        citySpinner.setSelection(0);
    }
}