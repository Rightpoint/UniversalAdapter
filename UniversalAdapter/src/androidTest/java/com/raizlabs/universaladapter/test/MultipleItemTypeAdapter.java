package com.raizlabs.universaladapter.test;

import android.view.View;
import android.view.ViewGroup;

import com.raizlabs.universaladapter.ListBasedAdapter;
import com.raizlabs.universaladapter.ViewHolder;

/**
 * Description: Adapter with multiple item types here.
 */
public class MultipleItemTypeAdapter extends ListBasedAdapter<Object, ViewHolder> {

    public static final int TYPE_1 = 0;
    public static final int TYPE_2 = 1;
    public static final int TYPE_3 = 2;

    @Override
    protected com.raizlabs.universaladapter.ViewHolder onCreateViewHolder(ViewGroup parent, int itemType) {
        switch (itemType) {
            case TYPE_1:
                return new ViewHolder1(new View(parent.getContext()));
            case TYPE_2:
                return new ViewHolder2(new View(parent.getContext()));
            case TYPE_3:
                return new ViewHolder3(new View(parent.getContext()));
        }
        throw new IllegalArgumentException("Wrong item viewtype here");
    }

    @Override
    protected void onBindViewHolder(com.raizlabs.universaladapter.ViewHolder viewHolder, Object o, int position) {
        int viewType = getItemViewType(position);
        if(viewType == TYPE_1) {
            ViewHolder1 viewHolder1 = (ViewHolder1) viewHolder;
        } else if(viewType == TYPE_2) {
            ViewHolder2 viewHolder2 = (ViewHolder2) viewHolder;
        } else if(viewType == TYPE_3) {
            ViewHolder3 viewHolder3 = (ViewHolder3) viewHolder;
        } else {
            throw new IllegalArgumentException("Wrong item viewtype here");
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (get(position) instanceof String) {
            return TYPE_1;
        } else if(get(position) instanceof Integer) {
            return TYPE_2;
        } else if(get(position) instanceof Float) {
            return TYPE_3;
        } else {
            throw new IllegalArgumentException("Wrong item viewtype here");
        }
    }

    @Override
    public int getItemViewTypeCount() {
        return 3;
    }

    public static class ViewHolder1 extends ViewHolder {

        public ViewHolder1(View itemView) {
            super(itemView);
        }
    }

    public static class ViewHolder2 extends ViewHolder {

        public ViewHolder2(View itemView) {
            super(itemView);
        }
    }

    public static class ViewHolder3 extends ViewHolder {

        public ViewHolder3(View itemView) {
            super(itemView);
        }
    }
}
