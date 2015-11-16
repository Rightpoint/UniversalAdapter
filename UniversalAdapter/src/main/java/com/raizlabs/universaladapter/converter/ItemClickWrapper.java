package com.raizlabs.universaladapter.converter;

import android.view.View;

import com.raizlabs.universaladapter.UniversalAdapterUtils;
import com.raizlabs.universaladapter.ViewHolder;

/**
 * Wraps conversion for {@link View.OnClickListener} and {@link View.OnLongClickListener}
 * into a unified class.
 */
class ItemClickWrapper<Item, Holder extends ViewHolder> implements View.OnClickListener, View.OnLongClickListener {

    private UniversalConverter<Item, Holder> universalConverter;

    public ItemClickWrapper(UniversalConverter<Item, Holder> converter) {
        this.universalConverter = converter;
    }

    public void registerClick(View view) {
        view.setOnClickListener(this);
    }

    public void unregisterClick(View view) {
        view.setOnClickListener(null);
    }

    public void registerLongClick(View view) {
        view.setOnLongClickListener(this);
    }

    public void unregisterLongClick(View view) {
        view.setOnLongClickListener(null);
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
