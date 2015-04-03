package com.raizlabs.android.universaladapter.widget.adapters.converter;

import com.raizlabs.android.universaladapter.widget.adapters.ViewHolder;

/**
 * Interface for a listener which is called when a {@link ViewGroupAdapterConverter}
 * view is long clicked.
 */
public interface ItemLongClickedListener<Item, Holder extends ViewHolder> {
    /**
     * Called when an item in the adapter is long clicked.
     *
     * @param adapter  The adapter whose item was long clicked.
     * @param item     The item which was long clicked.
     * @param holder   The view holder for the clicked item.
     * @param position The index of the long clicked item in the adapter.
     * @return true if the callback consumed the long click, false otherwise.
     */
    boolean onItemLongClicked(UniversalAdapter<Item, Holder> adapter, Item item, Holder holder, int position);
}
