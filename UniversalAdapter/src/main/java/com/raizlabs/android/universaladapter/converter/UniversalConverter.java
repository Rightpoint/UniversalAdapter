package com.raizlabs.android.universaladapter.converter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.raizlabs.android.universaladapter.ViewHolder;

/**
 * A unified interface that all converters such as {@link BaseAdapterConverter}, {@link PagerAdapterConverter},
 * {@link RecyclerViewAdapterConverter}, and {@link ViewGroupAdapterConverter} implement. The {@link UniversalConverterFactory}
 * returns this interface when calling {@link UniversalConverterFactory#createGeneric(UniversalAdapter, ViewGroup)}
 *
 * @param <Item>   The uniform item used for the {@link Holder}
 * @param <Holder> The holder for the uniform item type.
 */
public interface UniversalConverter<Item, Holder extends ViewHolder> {

    /**
     * Sets the listener to be called when an item is clicked.
     *
     * @param listener The listener to call.
     */
    void setItemClickedListener(ItemClickedListener<Item, Holder> listener);

    void setHeaderClickedListener(HeaderClickedListener headerClickedListener);

    void setFooterClickedListener(FooterClickedListener footerClickedListener);

    /**
     * Sets the listener to be called when an item is long clicked.
     *
     * @param longClickedListener The listener to call.
     */
    void setItemLongClickedListener(ItemLongClickedListener<Item, Holder> longClickedListener);

    void setHeaderLongClickedListener(HeaderLongClickListener headerLongClickedListener);

    void setFooterLongClickedListener(FooterLongClickedListener footerLongClickedListener);

    /**
     * Sets the adapter to use to populate the {@link ViewGroup} and loads
     * the current data.
     *
     * @param listAdapter The adapter to use to populate the view group.
     */
    void setAdapter(@NonNull UniversalAdapter<Item, Holder> listAdapter);

    /**
     * @return The {@link UniversalAdapter} that this adapter uses to populate
     * the view group.
     */
    UniversalAdapter<Item, Holder> getAdapter();

    void cleanup();

}
