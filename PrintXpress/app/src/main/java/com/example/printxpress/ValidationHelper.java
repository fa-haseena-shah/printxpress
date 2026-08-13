package com.example.printxpress;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationHelper {
    private String emailPattern = "^[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+$";
    private String phonePattern = "^(?:\\+94|0)?7[0-35-8][0-9]{7}$"; // sri lankan number format
    private String passwordStrength = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    public boolean validateEmail(String email){
        return Pattern.matches(emailPattern, email);
    }

    public boolean validatePhone(String phone){
        return Pattern.matches(phonePattern, phone);
    }

    public boolean validatePasswordStrength(String password) {
        return Pattern.matches(passwordStrength, password);
    }
}
