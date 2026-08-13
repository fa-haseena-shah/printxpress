package com.example.printxpress;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

// BaseAdapter since the design list will be updated (added to/deleted from)
public class DesignAdapter extends BaseAdapter {
    private final Context context;
    private List<Design> designList;
    private OnDesignDeleteListener deleteListener;
    // we will use this for SelectDesign and design management in DesignsActivity so
    // we have this flag to deem the appearance of remove design tv
    private boolean showRemoveButton;

    // constructor used in DesignsActivity : remove button shown
    public DesignAdapter(Context context, List<Design> designList, OnDesignDeleteListener deleteListener) {
        this(context, designList, deleteListener, true);
    }

    // constructor used in SelectDesignActivity : remove button hidden
    public DesignAdapter(Context context, List<Design> designList, OnDesignDeleteListener
            deleteListener, boolean showRemoveButton) {
        this.context = context;
        this.designList = designList;
        this.deleteListener = deleteListener;
        this.showRemoveButton = showRemoveButton;
    }

    public interface OnDesignDeleteListener {
        void onDelete(Design design);
    }

    @Override
    public int getCount() {
        return designList.size();
    }

    @Override
    public Object getItem(int position) {
        return designList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.design_item, parent, false);
            holder = new DesignAdapter.ViewHolder();
            holder.tvDesignTitle = convertView.findViewById(R.id.tvDesignTitle);
            holder.tvRemove = convertView.findViewById(R.id.tvRemove);
            holder.imgDesign = convertView.findViewById(R.id.imgDesign);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Design design = designList.get(position);
        holder.tvDesignTitle.setText(design.getTitle());
        File imageFile = new File(design.getFilePath());
        if (imageFile.exists()) {
            holder.imgDesign.setImageURI(Uri.fromFile(imageFile));
        } else {
            holder.imgDesign.setImageResource(R.drawable.ic_launcher_foreground); // fallback if file missing
        }
        if (showRemoveButton) { // adjust the apppearance of remove button depending on showRemoveButton bool
            // showRemoveButton = true then, make it visible
            holder.tvRemove.setVisibility(View.VISIBLE);
            holder.tvRemove.setOnClickListener(v -> { // add the on click listener to remove
                if (deleteListener != null) {
                    deleteListener.onDelete(design); // call delete listener interface
                }
            });
        } else { // showRemoveButton = true then, remove visiblty
            holder.tvRemove.setVisibility(View.GONE);
        }
        return convertView;
    }

    static class ViewHolder {
        TextView tvDesignTitle, tvRemove;
        ImageView imgDesign;
    }

    // for deleting+adding
    public void updateList(List<Design> newList) {
        this.designList = newList;
        notifyDataSetChanged();
    }
}
