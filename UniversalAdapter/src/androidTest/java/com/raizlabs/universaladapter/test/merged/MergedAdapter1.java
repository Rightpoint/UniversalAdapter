package com.raizlabs.universaladapter.test.merged;

import android.view.View;
import android.view.ViewGroup;

import com.raizlabs.universaladapter.ListBasedAdapter;
import com.raizlabs.universaladapter.ViewHolder;
import com.raizlabs.universaladapter.converter.MergedUniversalAdapter;

/**
 * Description: Simple adapter to be included in a {@link MergedUniversalAdapter}
 */
public class MergedAdapter1 extends ListBasedAdapter<String, ViewHolder> {

    @Override
    protected com.raizlabs.universaladapter.ViewHolder onCreateViewHolder(ViewGroup parent, int itemType) {
        return new ViewHolder(new View(parent.getContext()));
    }

    @Override
    protected void onBindViewHolder(com.raizlabs.universaladapter.ViewHolder viewHolder, String s, int position) {
        // nothing
    }

    static class ViewHolder extends com.raizlabs.universaladapter.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
