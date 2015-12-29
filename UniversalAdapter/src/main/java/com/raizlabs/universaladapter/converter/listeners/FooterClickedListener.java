package com.raizlabs.universaladapter.converter.listeners;

import android.view.View;

import com.raizlabs.universaladapter.ViewHolder;
import com.raizlabs.universaladapter.converter.UniversalAdapter;

/**
 * Unified interface for clicks on a footer {@link View}
 */
public interface FooterClickedListener {

    /**
     * Called when a footer within an adapter is clicked.
     *
     * @param adapter      The adapter who's footer was clicked.
     * @param footerHolder The holder that contains the footer.
     * @param position     The position of the footer within the footer group.
     */
    void onFooterClicked(UniversalAdapter adapter, ViewHolder footerHolder, int position);
}
