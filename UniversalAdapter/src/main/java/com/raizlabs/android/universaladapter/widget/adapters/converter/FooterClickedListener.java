package com.raizlabs.android.universaladapter.widget.adapters.converter;

import android.view.View;

import com.raizlabs.android.universaladapter.widget.adapters.ViewHolder;

/**
 * Unified interface for clicks on a footer {@link View}
 */
public interface FooterClickedListener {

    /**
     * Called when a footer within an adapter is clicked.
     *
     * @param adapter      The adapter who's footer was clicked
     * @param footerHolder The holder that contains the header.
     * @param position     The position of the header within the footer group
     */
    void onFooterClicked(UniversalAdapter adapter, ViewHolder footerHolder, int position);
}
