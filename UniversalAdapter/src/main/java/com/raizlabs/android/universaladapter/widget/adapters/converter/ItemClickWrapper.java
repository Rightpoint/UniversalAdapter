package com.raizlabs.android.universaladapter.widget.adapters.converter;

import android.view.View;

import com.raizlabs.android.universaladapter.widget.adapters.UniversalAdapterUtils;
import com.raizlabs.android.universaladapter.widget.adapters.ViewHolder;

/**
 * Description: Wraps conversion for {@link View.OnClickListener} and {@link View.OnLongClickListener}
 * into a unified class.
 */
class ItemClickWrapper<Item, Holder extends ViewHolder> implements View.OnClickListener, View.OnLongClickListener {

    private UniversalConverter<Item, Holder, ?> universalConverter;

    ItemClickedListener<Item, Holder> itemClickedListener;
    ItemLongClickedListener<Item, Holder> itemLongClickedListener;

    public ItemClickWrapper(UniversalConverter<Item, Holder, ?> converter) {
        this.universalConverter = converter;
    }

    public void register(View view) {
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (universalConverter.getViewGroup() != null) {
            int index = universalConverter.getViewGroup().indexOfChild(v);

            if (universalConverter.getListAdapter().isEnabled(index) && itemClickedListener != null) {
                Item item = universalConverter.getListAdapter().get(index);
                Holder holder = (Holder) UniversalAdapterUtils.getViewHolder(v);
                itemClickedListener.onItemClicked(universalConverter.getListAdapter(), item, holder, index);
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (universalConverter.getViewGroup() != null) {
            int index = universalConverter.getViewGroup().indexOfChild(v);

            if (universalConverter.getListAdapter().isEnabled(index) && itemLongClickedListener != null) {
                Item item = universalConverter.getListAdapter().get(index);
                Holder holder = (Holder) UniversalAdapterUtils.getViewHolder(v);
                return itemLongClickedListener.onItemLongClicked(universalConverter.getListAdapter(), item, holder, index);
            }
        }
        return false;
    }
}
