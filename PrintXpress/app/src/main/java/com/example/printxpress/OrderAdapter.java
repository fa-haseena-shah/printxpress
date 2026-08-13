package com.example.printxpress;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class OrderAdapter extends BaseAdapter {
    private Context context;
    private List<HistoryHelper> orderList;
    private OnOrderActionListener actionListener;

    public interface OnOrderActionListener {
        void onCancel(HistoryHelper order);
        void onReschedule(HistoryHelper order);
    }

    public OrderAdapter(Context context, List<HistoryHelper> orderList,
                        OnOrderActionListener actionListener) {
        this.context = context;
        this.orderList = orderList;
        this.actionListener = actionListener;
    }

    @Override
    public int getCount() {
        return orderList.size();
    }

    @Override
    public Object getItem(int position) {
        return orderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.order_item, parent, false);
            holder = new ViewHolder();
            holder.tvOrderId = convertView.findViewById(R.id.tvOrderId);
            holder.tvDate = convertView.findViewById(R.id.tvDate);
            holder.tvProduct = convertView.findViewById(R.id.tvProduct);
            holder.tvStatus = convertView.findViewById(R.id.tvStatus);
            holder.tvAmount = convertView.findViewById(R.id.tvAmount);
            holder.tvQty = convertView.findViewById(R.id.tvQty);
            holder.tvCustomization = convertView.findViewById(R.id.tvCustomization);
            holder.tvAddress = convertView.findViewById(R.id.tvAddress);
            holder.tvReschedule = convertView.findViewById(R.id.tvReschedule);
            holder.tvCancel = convertView.findViewById(R.id.tvCancel);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        HistoryHelper order = orderList.get(position);

        holder.tvOrderId.setText("Order #" + order.getOrderId());
        holder.tvDate.setText(order.getDeliveryDateTime());
        holder.tvStatus.setText(order.getStatus().substring(0, 1).toUpperCase() + order.getStatus().substring(1));
        holder.tvProduct.setText(order.getProductName());
        holder.tvQty.setText("x " + order.getQty());
        holder.tvCustomization.setText(order.getCustomization());
        holder.tvAmount.setText("Rs. " + order.getTotalAmount());

        // delivery info line : handle it differently for delivery and pickup
        if ("Delivery".equalsIgnoreCase(order.getDeliveryType())) {
            holder.tvAddress.setText("Delivery > " + order.getAddressDisplay());
        } else if ("Pickup".equalsIgnoreCase(order.getDeliveryType())) {
            holder.tvAddress.setText("Pickup");
        } else {
            holder.tvAddress.setText("Delivery details pending");
        }

        // check order status, if its processing,
        // set the rescedule and cancel actions to be visible
        // otherwise not using ternary operator
        boolean canModify = "processing".equalsIgnoreCase(order.getStatus());
        holder.tvReschedule.setVisibility(canModify ? View.VISIBLE : View.GONE);
        holder.tvCancel.setVisibility(canModify ? View.VISIBLE : View.GONE);

        holder.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actionListener != null) {
                    actionListener.onCancel(order);
                }
            }
        });

        holder.tvReschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actionListener != null) actionListener.onReschedule(order);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        TextView tvOrderId, tvDate, tvProduct, tvStatus, tvAmount,tvQty,
                tvCustomization, tvAddress, tvReschedule, tvCancel;
    }
}