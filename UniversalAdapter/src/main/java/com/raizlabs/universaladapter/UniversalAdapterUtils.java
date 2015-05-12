package com.raizlabs.universaladapter;

import android.view.View;

/**
 * Class with some useful utility functions.
 */
public class UniversalAdapterUtils {
    /**
     * ID of the tag in views in which the view holder should be stored.
     *
     * @see View#getTag(int)
     */
    public static final int VIEWHOLDER_TAG_ID = R.id.com_raizlabs_viewholderTagID;

    /**
     * ID of the tag in views in which the current index in the list that view is in.
     */
    public static final int VIEWHOLDER_INDEX_ID = R.id.com_raizlabs_viewholderIndexID;

    /**
     * Sets the view holder associated with the given view to the given
     * view holder.
     *
     * @param view   The view to set the view holder of.
     * @param holder The view holder to associate with the given view.
     */
    public static void setViewHolder(View view, Object holder) {
        if (view != null) {
            view.setTag(VIEWHOLDER_TAG_ID, holder);
        }
    }

    /**
     * Returns the view holder associated with the given view.
     *
     * @param view The view to get the view holder of.
     * @return The associated view holder, or null if none was found.
     */
    @SuppressWarnings("unchecked")
    public static <Holder> Holder getViewHolder(View view) {
        if (view == null) {
            return null;
        }
        return (Holder) view.getTag(VIEWHOLDER_TAG_ID);
    }

    /**
     * @param view The view to retrieve an index from.
     * @return The index of the view
     */
    public static int getIndex(View view) {
        return (int) view.getTag(VIEWHOLDER_INDEX_ID);
    }
}
