package com.raizlabs.android.universaladapter.converter;

import android.view.View;

import com.raizlabs.android.universaladapter.R;
import com.raizlabs.android.universaladapter.ViewHolder;


/**
 * Description: Wraps conversion for {@link View.OnClickListener} and {@link View.OnLongClickListener}
 * into a unified class.
 */
class ItemClickWrapper<Item, Holder extends ViewHolder> implements View.OnClickListener, View.OnLongClickListener {

    private UniversalConverter<Item, Holder, ?> universalConverter;

    public ItemClickWrapper(UniversalConverter<Item, Holder, ?> converter) {
        this.universalConverter = converter;
    }

    public void register(View view) {
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int index = (int) v.getTag(R.id.com_raizlabs_viewholderIndexID);
        universalConverter.getAdapter().onItemClicked(index, v);
    }

    @Override
    public boolean onLongClick(View v) {
        int index = (int) v.getTag(R.id.com_raizlabs_viewholderIndexID);
        return universalConverter.getAdapter().onItemLongClicked(index, v);
    }
}
