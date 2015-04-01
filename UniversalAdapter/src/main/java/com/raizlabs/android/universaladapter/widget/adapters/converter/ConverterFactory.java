package com.raizlabs.android.universaladapter.widget.adapters.converter;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.raizlabs.android.universaladapter.widget.adapters.ListBasedAdapter;
import com.raizlabs.android.universaladapter.widget.adapters.ViewHolder;

/**
 * Description:
 */
public class ConverterFactory {

    /**
     * @param viewGroup The viewgroup to register the adapter with. This populates and registers any click events
     *                  with the viewgroup.
     * @param <Item>
     * @param <Holder>
     * @return The appropriate converter from a specific {@link ListBasedAdapter} and {@link ViewGroup}.
     * If it can't understand the {@link ViewGroup} more specifically, a {@link ViewGroupAdapterConverter}
     * is returned.
     */
    @SuppressWarnings("unchecked")
    public static <Item, Holder extends ViewHolder>
    UniversalConverter<Item, Holder, ?> create(ListBasedAdapter<Item, Holder> adapter, ViewGroup viewGroup) {
        if (viewGroup instanceof RecyclerView) {
            return new RecyclerViewAdapterConverter<>(adapter, (RecyclerView) viewGroup);
        } else if (viewGroup instanceof ViewPager) {
            return new PagerAdapterConverter<>(adapter, (ViewPager) viewGroup);
        } else if (viewGroup instanceof AdapterView) {
            return new BaseAdapterConverter<>(adapter, (AdapterView<BaseAdapter>) viewGroup);
        } else {
            return new ViewGroupAdapterConverter<>(adapter, viewGroup);
        }
    }
}
