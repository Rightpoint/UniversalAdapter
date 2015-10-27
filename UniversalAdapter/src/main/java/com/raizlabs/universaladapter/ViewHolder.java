package com.raizlabs.universaladapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ViewHolder extends RecyclerView.ViewHolder {

    public ViewHolder(View itemView) {
        super(itemView);
    }

    /**
     * Returns the {@link Context} of this {@link ViewHolder}, based on its {@link #itemView}.
     * @return The context from the item view, or null if there is no item view.
     */
    public Context getContext() {
        if (itemView != null) {
            return itemView.getContext();
        } else {
            return null;
        }
    }
}
