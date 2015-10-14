package com.raizlabs.universaladapter.converter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.raizlabs.universaladapter.ViewHolder;

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

    /**
     * Sets listener to get called when a header is clicked.
     *
     * @param headerClickedListener The listener to call.
     */
    void setHeaderClickedListener(HeaderClickedListener headerClickedListener);

    /**
     * Sets listener to get called when a footer is clicked.
     *
     * @param footerClickedListener The listener to call.
     */
    void setFooterClickedListener(FooterClickedListener footerClickedListener);

    /**
     * Sets the listener to be called when an item is long clicked.
     *
     * @param longClickedListener The listener to call.
     */
    void setItemLongClickedListener(ItemLongClickedListener<Item, Holder> longClickedListener);

    /**
     * Sets listener to get called when a header is long clicked.
     *
     * @param headerLongClickedListener The listener to call.
     */
    void setHeaderLongClickedListener(HeaderLongClickedListener headerLongClickedListener);

    /**
     * Sets listener to get called when a footer is long clicked.
     *
     * @param footerLongClickedListener The listener to call.
     */
    void setFooterLongClickedListener(FooterLongClickedListener footerLongClickedListener);

    /**
     * Sets the adapter to use to populate the {@link ViewGroup} and loads
     * the current data.
     *
     * @param listAdapter The adapter to use to populate the view group.
     */
    void setAdapter(
            @NonNull
            UniversalAdapter<Item, Holder> listAdapter);

    /**
     * @return The {@link UniversalAdapter} that this adapter uses to populate
     * the {@link ViewGroup}.
     */
    UniversalAdapter<Item, Holder> getAdapter();

    /**
     * Performs some cleanup such as unbinding a {@link ViewGroup}.
     */
    void cleanup();

}
