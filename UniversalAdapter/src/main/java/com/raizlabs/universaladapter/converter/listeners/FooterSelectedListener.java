package com.raizlabs.universaladapter.converter.listeners;

import android.view.View;

import com.raizlabs.universaladapter.ViewHolder;
import com.raizlabs.universaladapter.converter.UniversalAdapter;

/**
 * Unified interface for selections of a footer {@link View}
 */
public interface FooterSelectedListener {

    /**
     * Called when a footer within an adapter is selected.
     *
     * @param adapter      The adapter who's footer was selected.
     * @param footerHolder The holder that contains the footer.
     * @param position     The position of the footer within the footer group.
     */
    void onFooterSelected(UniversalAdapter adapter, ViewHolder footerHolder, int position);
}
