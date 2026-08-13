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

// base adapter since lists will be changed through addition/deletions
public class AddressAdapter extends BaseAdapter {
    private final Context context;
    private List<Address> addressList;
    private OnAddressDeleteListener deleteListener;
    private boolean showRemoveButton;

    public interface OnAddressDeleteListener {
        void onDelete(Address address);
    }

    // used in AddressesActvity : remove button shown
    public AddressAdapter(Context context, List<Address> addressList,
                          OnAddressDeleteListener deleteListener) {
        this(context, addressList, deleteListener, true);
    }

    // used in DeliveryActivity : remove button hidden
    public AddressAdapter(Context context, List<Address> addressList,
                          OnAddressDeleteListener deleteListener, boolean showRemoveButton) {
        this.context = context;
        this.addressList = addressList;
        this.deleteListener = deleteListener;
        this.showRemoveButton = showRemoveButton;
    }

    @Override
    public int getCount() {
        return addressList.size();
    }

    @Override
    public Object getItem(int position) {
        return addressList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @Nullable ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.address_item, parent, false);
            holder = new ViewHolder();
            holder.tvAddress = convertView.findViewById(R.id.tvAddress);
            holder.tvDeleteAddress = convertView.findViewById(R.id.tvDeleteAddress);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Address address = addressList.get(position);
        holder.tvAddress.setText(address.addressDisplay());
        if (showRemoveButton) {
            holder.tvDeleteAddress.setVisibility(View.VISIBLE);
            holder.tvDeleteAddress.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDelete(address);
                }
            });
        } else {
            holder.tvDeleteAddress.setVisibility(View.GONE);
        }
        return convertView;
    }

    // we will delete addresses so, we call updateList method also
    public void updateList(List<Address> newList) {
        this.addressList = newList;
        notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView tvAddress, tvDeleteAddress;
    }
}
