package com.example.sainath.memorytest.model;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sainath.memorytest.R;
import com.example.sainath.memorytest.model.GridItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by sainath on 10/17/2015.
 */
final public class GridViewAdapter extends ArrayAdapter<GridItem> {
    private final Context context;
    private int layoutResourceId;
    private ArrayList<GridItem> mGridData;

    public GridViewAdapter(Context context,int layoutResourceId, ArrayList<GridItem> mGridData) {
        super(context, layoutResourceId, mGridData);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.mGridData = mGridData;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.titleTextView = (TextView) row.findViewById(R.id.grid_item_title);
            holder.imageView = (ImageView) row.findViewById(R.id.grid_item_image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        if(mGridData.size() > 0) {
            GridItem item = mGridData.get(position);
            holder.titleTextView.setText(Html.fromHtml(item.getTitle()));

            if (item.isShown())
                Picasso.with(context).load(item.getImage()).placeholder(R.drawable.placeholder).into(holder.imageView);
            else
                holder.imageView.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_launcher));
        } else {
            holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.error));
        }
        return row;
    }

    /**
     * ViewHolder class to hold the title and the imageviews
     */
    static class ViewHolder {
        TextView titleTextView;
        ImageView imageView;
    }

    /**
     * Updates grid data and refresh grid items.
     *
     * @param mGridData
     */
    public void setGridData(ArrayList<GridItem> mGridData) {
        this.mGridData = mGridData;
        notifyDataSetChanged();
    }
}
