package com.raizlabs.universaladapter.converter.listeners;

import android.view.View;

import com.raizlabs.universaladapter.ViewHolder;
import com.raizlabs.universaladapter.converter.UniversalAdapter;

/**
 * Unified interface for selections of a header {@link View}
 */
public interface HeaderSelectedListener {

    /**
     * Called when a header within an adapter is selected.
     *
     * @param adapter      The adapter who's header was selected.
     * @param headerHolder The holder that contains the header.
     * @param position     The position of the header within the header group.
     */
    void onHeaderSelected(UniversalAdapter adapter, ViewHolder headerHolder, int position);
}
