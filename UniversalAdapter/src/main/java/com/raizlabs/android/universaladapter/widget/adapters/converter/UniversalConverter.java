package com.raizlabs.android.universaladapter.widget.adapters.converter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.raizlabs.android.universaladapter.widget.adapters.ViewHolder;

/**
 * A unified interface that all converters such as {@link BaseAdapterConverter}, {@link PagerAdapterConverter},
 * {@link RecyclerViewAdapterConverter}, and {@link ViewGroupAdapterConverter} implement. The {@link UniversalConverterFactory}
 * returns this interface when calling {@link UniversalConverterFactory#createGeneric(UniversalAdapter, ViewGroup)}
 */
public interface UniversalConverter<Item, Holder extends ViewHolder, Register extends ViewGroup> {

    void setItemClickedListener(ItemClickedListener<Item, Holder> listener);

    void setItemLongClickedListener(ItemLongClickedListener<Item, Holder> longClickedListener);

    void setAdapter(@NonNull UniversalAdapter<Item, Holder> listAdapter);

    UniversalAdapter<Item, Holder> getAdapter();

    void cleanup();

}
