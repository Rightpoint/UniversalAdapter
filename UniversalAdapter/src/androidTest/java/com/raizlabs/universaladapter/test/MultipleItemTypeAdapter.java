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

    @Override
    protected com.raizlabs.universaladapter.ViewHolder onCreateViewHolder(ViewGroup parent, int itemType) {
        switch (itemType) {
            case TYPE_1:
                return new ViewHolder1(new View(parent.getContext()));
            case TYPE_2:
                return new ViewHolder2(new View(parent.getContext()));
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
        } else {
            throw new IllegalArgumentException("Wrong item viewtype here");
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (get(position) instanceof String) ? TYPE_1 : TYPE_2;
    }

    @Override
    public int getItemViewTypeCount() {
        return 2;
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
}
