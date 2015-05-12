package com.raizlabs.universaladapter;

import android.view.View;

/**
 * Class which allows wrapping any other class into a {@link ViewHolder}. This
 * allows the inner view holder type to be constructed at will and eventually
 * wrapped into a {@link ViewHolder} instead of forcing the construction to
 * happen after the view was inflated, and also is convenient for compatibility.
 *
 * @param <InnerHolderType> The type of the contained view holder.
 */
public class ViewHolderWrapper<InnerHolderType> extends ViewHolder {
    public InnerHolderType innerHolder;

    public ViewHolderWrapper(View itemView) {
        super(itemView);
    }
}
