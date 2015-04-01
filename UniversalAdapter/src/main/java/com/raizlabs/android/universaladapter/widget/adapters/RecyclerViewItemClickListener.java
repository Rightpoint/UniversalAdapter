package com.raizlabs.android.universaladapter.widget.adapters;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnItemTouchListener;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Class which assists in handling the item clicks of a {@link RecyclerView}.
 * This class should be attached to
 * {@link RecyclerView#addOnItemTouchListener(OnItemTouchListener)}.
 */
public abstract class RecyclerViewItemClickListener implements OnItemTouchListener {
    private GestureDetector gestureDetector;

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        if (gestureDetector == null) {
            gestureDetector = new GestureDetector(view.getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });
        }

        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && gestureDetector.onTouchEvent(e)) {
            int position = view.getChildPosition(childView);
            ViewHolder viewHolder = (ViewHolder) view.getChildViewHolder(childView);
            onItemClick(viewHolder, view, position, e.getX(), e.getY());
        }

        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
    }

    /**
     * Called when an item in the {@link RecyclerView} is clicked.
     *
     * @param viewHolder The view holder of the clicked item.
     * @param parent     The recycler view which contained the clicked item.
     * @param position   The position in the adapter of the clicked item.
     */
    public abstract void onItemClick(ViewHolder viewHolder, RecyclerView parent, int position, float x, float y);
}
