package com.example.printxpress;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

// Using BaseAdapter bc its getCount(), getItem(), and getView() read directly from
// productList field; important when reassigning productList later for filtering/searching
public class ProductAdapter extends BaseAdapter {
    private final Context context;
    private List<Product> productList;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    // implement the 4 mandatory methods of BaseAdapter,

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Product getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
            holder = new ViewHolder();
            holder.imgSample = convertView.findViewById(R.id.imgSample);
            holder.tvProduct = convertView.findViewById(R.id.tvProduct);
            holder.tvPrice = convertView.findViewById(R.id.tvPrice);
            holder.btnOrder = convertView.findViewById(R.id.btnOrder);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Product product = productList.get(position);
        // set the product objects info into view holder
        holder.tvProduct.setText(product.getName());
        holder.tvPrice.setText("LKR " + product.getBasePrice());
        String imageName = product.getSample().replace(".png", "");
        int imageResId = context.getResources().getIdentifier(
                imageName,
                "drawable",
                context.getPackageName()
        );
        holder.imgSample.setImageResource(imageResId);

        // get the id through product object so it can be used to fetch details in details activity
        holder.btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("product_id", product.getProductId());
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        ImageView imgSample;
        TextView tvProduct, tvPrice;
        Button btnOrder;
    }

    // for filtering, searching
    public void updateList(List<Product> newList) {
        this.productList = newList;
        notifyDataSetChanged();
    }
}
