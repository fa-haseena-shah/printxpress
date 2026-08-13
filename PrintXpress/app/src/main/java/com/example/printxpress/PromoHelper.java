package com.example.printxpress;

import java.util.ArrayList;
import java.util.List;

public class PromoHelper {
    private static final List<Promo> promos = new ArrayList<>();

    public static List<Promo> getAllPromos() {
        if(!promos.isEmpty()){
            return promos; // if it was already populated, just return the list
        }
        // otherwise add these and return this
        promos.add(new Promo(15, "NEWYEAR15", "15% off to celebrate the new year", "2026-01-01", "2026-01-15"));
        promos.add(new Promo(10, "WELCOME10", "10% off for all customers, all year round", "2026-01-01", "2026-12-31"));
        promos.add(new Promo(20, "MIDYEAR20", "20% off our mid-year promotion", "2026-06-01", "2026-07-31"));
        promos.add(new Promo(5, "FLASH5", "5% off — this week only!", "2026-06-20", "2026-06-30"));
        promos.add(new Promo(25, "BLACKFRIDAY25", "25% off Black Friday sale", "2026-11-20", "2026-11-30"));
        promos.add(new Promo(12, "SUMMER12", "12% off summer specials", "2026-06-01", "2026-08-31"));
        return promos;
    }

    // to display the valid promos only : call Promo class isCurrentlyValid method
    public List<Promo> getValidPromos() {
        List<Promo> valid = new ArrayList<>();
        for (Promo promo : getAllPromos()) {
            if (promo.isCurrentlyValid()) { // only the current valid ones in a list called valid
                valid.add(promo);
            }
        }
        return valid; // return list ov valid promos
    }
}