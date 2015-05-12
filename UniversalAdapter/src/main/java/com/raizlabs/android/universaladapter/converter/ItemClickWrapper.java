package com.raizlabs.android.universaladapter.converter;

import android.view.View;

import com.raizlabs.android.universaladapter.UniversalAdapterUtils;
import com.raizlabs.android.universaladapter.ViewHolder;

/**
 * Wraps conversion for {@link View.OnClickListener} and {@link View.OnLongClickListener}
 * into a unified class.
 */
class ItemClickWrapper<Item, Holder extends ViewHolder> implements View.OnClickListener, View.OnLongClickListener {

    private UniversalConverter<Item, Holder> universalConverter;

    public ItemClickWrapper(UniversalConverter<Item, Holder> converter) {
        this.universalConverter = converter;
    }

    public void register(View view) {
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        universalConverter.getAdapter().onItemClicked(UniversalAdapterUtils.getIndex(v), v);
    }

    @Override
    public boolean onLongClick(View v) {
        return universalConverter.getAdapter().onItemLongClicked(UniversalAdapterUtils.getIndex(v), v);
    }
}
