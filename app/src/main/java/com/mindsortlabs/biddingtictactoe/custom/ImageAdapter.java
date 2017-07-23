package com.mindsortlabs.biddingtictactoe.custom;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.mindsortlabs.biddingtictactoe.R;

public class ImageAdapter extends BaseAdapter {
    int board_sizes = 0;
    private Context mContext;
    // references to our images
    private Integer[] mThumbIds;

    public ImageAdapter(Context c, int board_sizes) {
        mContext = c;
        this.board_sizes = board_sizes;
        mThumbIds = new Integer[board_sizes * board_sizes];

        for (int i = 0; i < board_sizes * board_sizes; i++)
            mThumbIds[i] = R.drawable.back;

    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        int width = parent.getWidth();
        int height = parent.getHeight();
        int min = Math.min(width, height);


        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(min / board_sizes, min / board_sizes));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(5, 5, 5, 5);
        } else {
            imageView = (ImageView) convertView;
            if (imageView.getHeight() == 0)
                imageView.setLayoutParams(new GridView.LayoutParams(min / board_sizes, min / board_sizes));
        }

        //imageView.animate().alpha(0);
        imageView.setTag(R.drawable.back);
        imageView.setBackgroundResource(R.drawable.grid_items_border);
        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }


}

