package com.example.printxpress;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Promo {
    private int discountPercent;
    private String code, description, validFrom, validTill;

    public Promo(int discountPercent, String code, String description, String validFrom, String validTill) {
        this.discountPercent = discountPercent;
        this.code = code;
        this.description = description;
        this.validFrom = validFrom;
        this.validTill = validTill;
    }

    public int getDiscountPercent() {
        return discountPercent;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getValidFrom() {
        return validFrom;
    }

    public String getValidTill() {
        return validTill;
    }

    // this helper function will allow us to display currently valid promos only
    public boolean isCurrentlyValid() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date from = sdf.parse(validFrom);
            Date till = sdf.parse(validTill);
            Date now = new Date();
            return !now.before(from) && !now.after(till);
        } catch (ParseException e) {
            return false; // handle the invalid dates instead of crashing app
        }
    }
}
