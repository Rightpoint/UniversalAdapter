package com.raizlabs.universaladapter.converter.listeners;

import com.raizlabs.universaladapter.ViewHolder;
import com.raizlabs.universaladapter.converter.PagerAdapterConverter;
import com.raizlabs.universaladapter.converter.UniversalAdapter;
import com.raizlabs.universaladapter.converter.ViewGroupAdapterConverter;

/**
 * Interface for a listener which is called when a {@link ViewGroupAdapterConverter} or {@link PagerAdapterConverter} footer
 * view is long clicked.
 */
public interface FooterLongClickedListener {

    /**
     * Called when a footer within an adapter is long clicked.
     *
     * @param adapter      The adapter who's footer was long clicked.
     * @param footerHolder The holder that contains the footer
     * @param position     The position of the footer within the footer group
     * @return true if the callback consumed the click, false otherwise.
     */
    boolean onFooterLongClicked(UniversalAdapter adapter, ViewHolder footerHolder, int position);
}
