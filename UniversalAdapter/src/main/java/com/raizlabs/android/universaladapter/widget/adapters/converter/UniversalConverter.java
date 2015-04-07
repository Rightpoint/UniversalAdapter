package com.raizlabs.android.universaladapter.widget.adapters.converter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.raizlabs.android.universaladapter.widget.adapters.ViewHolder;

/**
 * Description:
 */
public interface UniversalConverter<Item, Holder extends ViewHolder, Register extends ViewGroup> {

    void setItemClickedListener(ItemClickedListener<Item, Holder> listener);

    void setAdapter(@NonNull UniversalAdapter<Item, Holder> listAdapter);
    UniversalAdapter<Item, Holder> getAdapter();

    Register getViewGroup();

    void cleanup();

}
