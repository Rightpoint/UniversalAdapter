package com.raizlabs.android.universaladapter.converter;

import android.view.View;

import com.raizlabs.android.universaladapter.ViewHolder;

/**
 * A unified interface for clicks on a {@link View} within each corresponding adapter.
 *
 * @param <Item>   The uniform item used for the {@link Holder}
 * @param <Holder> The holder for the uniform item type.
 */
public interface ItemClickedListener<Item, Holder extends ViewHolder> {

    /**
     * Called when an item in the adapter is clicked.
     *
     * @param adapter  The adapter whose item was clicked.
     * @param item     The item which was clicked.
     * @param holder   The view holder for the clicked item.
     * @param position The index of the clicked item in the adpater.
     */
    void onItemClicked(UniversalAdapter<Item, Holder> adapter, Item item, Holder holder, int position);
}
