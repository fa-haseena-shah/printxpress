package com.example.printxpress;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

// using array adapter instead of base adapter since there is no action associated to promos
public class PromoAdapter extends ArrayAdapter<Promo> {

    private final Context context;
    private final List<Promo> promoList;
    TextView tvPromoCode, tvDiscount, tvDescription, tvValidity;

    public PromoAdapter(Context context, List<Promo> promoList) {
        super(context, R.layout.promo_item, promoList);
        this.context = context;
        this.promoList = promoList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("ViewHolder")
        View rowView = inflater.inflate(R.layout.promo_item, parent, false);
        tvPromoCode = rowView.findViewById(R.id.tvPromoCode);
        tvDiscount = rowView.findViewById(R.id.tvDiscount);
        tvDescription = rowView.findViewById(R.id.tvDescription);
        tvValidity = rowView.findViewById(R.id.tvValidity);

        Promo promo = promoList.get(position);
        tvPromoCode.setText("#" + promo.getCode());
        tvDiscount.setText((int) promo.getDiscountPercent() + "% OFF");
        tvDescription.setText(promo.getDescription());
        tvValidity.setText("Valid: " + promo.getValidFrom() + " - " + promo.getValidTill());
        return rowView;
    }
}