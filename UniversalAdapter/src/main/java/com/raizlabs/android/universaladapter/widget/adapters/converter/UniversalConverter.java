package com.raizlabs.android.universaladapter.widget.adapters.converter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.raizlabs.android.universaladapter.widget.adapters.ListBasedAdapter;
import com.raizlabs.android.universaladapter.widget.adapters.ViewHolder;

/**
 * Description:
 */
public interface UniversalConverter<Item, Holder extends ViewHolder, Register extends ViewGroup> {

    void setItemClickedListener(ItemClickedListener<Item, Holder> listener);

    void setAdapter(@NonNull ListBasedAdapter<Item, Holder> listAdapter);

    void register(@NonNull Register register);

    Register getViewGroup();

    void cleanup();

    ListBasedAdapter<Item, Holder> getListAdapter();
}