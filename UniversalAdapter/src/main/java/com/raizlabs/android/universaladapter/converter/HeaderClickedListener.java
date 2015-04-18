package com.raizlabs.android.universaladapter.converter;

import android.view.View;

import com.raizlabs.android.universaladapter.ViewHolder;

/**
 * Unified interface for clicks on a header {@link View}
 */
public interface HeaderClickedListener {

    /**
     * Called when a header within an adapter is clicked.
     *
     * @param adapter      The adapter who's header was clicked
     * @param headerHolder The holder that contains the header.
     * @param position     The position of the header within the header group
     */
    void onHeaderClicked(UniversalAdapter adapter, ViewHolder headerHolder, int position);
}
