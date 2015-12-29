package com.raizlabs.universaladapter.converter.listeners;

import android.view.View;

import com.raizlabs.universaladapter.ViewHolder;
import com.raizlabs.universaladapter.converter.UniversalAdapter;

/**
 * A unified interface for selections of a {@link View} within each corresponding adapter.
 *
 * @param <Item>   The uniform item used for the {@link Holder}
 * @param <Holder> The holder for the uniform item type.
 */
public interface ItemSelectedListener<Item, Holder extends ViewHolder> {

    /**
     * Called when an item in the adapter is selected.
     *
     * @param adapter  The adapter whose item was selected.
     * @param item     The item which was selected.
     * @param holder   The view holder for the selected item.
     * @param position The index of the selected item in the adpater.
     */
    void onItemSelected(UniversalAdapter<Item, Holder> adapter, Item item, Holder holder, int position);
}
