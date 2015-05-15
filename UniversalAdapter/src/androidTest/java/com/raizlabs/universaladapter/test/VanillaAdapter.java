package com.raizlabs.universaladapter.test;

import android.view.View;
import android.view.ViewGroup;

import com.raizlabs.universaladapter.ListBasedAdapter;
import com.raizlabs.universaladapter.ViewHolder;

/**
 * Description: Simple adapter
 */
public class VanillaAdapter extends ListBasedAdapter<Object, VanillaAdapter.VanillaHolder> {

    @Override
    protected VanillaHolder onCreateViewHolder(ViewGroup parent, int itemType) {
        return new VanillaHolder(new View(parent.getContext()));
    }

    @Override
    protected void onBindViewHolder(VanillaHolder viewHolder, Object o, int position) {
        // nothing
    }

    static class VanillaHolder extends ViewHolder {

        public VanillaHolder(View itemView) {
            super(itemView);
        }
    }
}
