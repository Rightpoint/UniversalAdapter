package com.raizlabs.android.universaladapter.widget.adapters;

import android.view.View;

import com.raizlabs.widget.adapters.R;

/**
 * Class with some useful utility functions.
 */
public class UniversalAdapterUtils {
    /**
     * ID of the tag in views in which the view holder should be stored.
     *
     * @see View#getTag(int, Object)
     */
    public static final int VIEWHOLDER_TAG_ID = R.id.com_raizlabs_viewholderTagID;

    /**
     * Sets the view holder associated with the given view to the given
     * view holder.
     *
     * @param view   The view to set the view holder of.
     * @param holder The view holder to associate with the given view.
     */
    public static void setViewHolder(View view, Object holder) {
        if (view != null)
            view.setTag(VIEWHOLDER_TAG_ID, holder);
    }

    /**
     * Returns the view holder associated with the given view.
     *
     * @param view The view to get the view holder of.
     * @return The associated view holder, or null if none was found.
     */
    public static Object getViewHolder(View view) {
        if (view == null) return null;
        return view.getTag(VIEWHOLDER_TAG_ID);
    }
}
