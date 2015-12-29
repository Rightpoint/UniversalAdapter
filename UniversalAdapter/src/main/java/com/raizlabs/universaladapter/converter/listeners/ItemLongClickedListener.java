package com.raizlabs.universaladapter.converter.listeners;

import com.raizlabs.universaladapter.ViewHolder;
import com.raizlabs.universaladapter.converter.PagerAdapterConverter;
import com.raizlabs.universaladapter.converter.UniversalAdapter;
import com.raizlabs.universaladapter.converter.ViewGroupAdapterConverter;

/**
 * Interface for a listener which is called when a {@link ViewGroupAdapterConverter} or {@link PagerAdapterConverter}
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
