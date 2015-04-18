package com.raizlabs.android.universaladapter.widget.adapters;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnItemTouchListener;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.raizlabs.android.universaladapter.widget.adapters.converter.RecyclerViewAdapterConverter;

/**
 * Class which assists in handling the item clicks of a {@link RecyclerView}.
 * This class should be attached to
 * {@link RecyclerView#addOnItemTouchListener(OnItemTouchListener)}.
 */
public abstract class RecyclerViewItemClickListener
        implements OnItemTouchListener, RecyclerViewAdapterConverter.RecyclerItemClickListener {

    // region Members

    private GestureDetector gestureDetector;

    // endregion Members

    // region Inherited Methods

    @Override
    public boolean onInterceptTouchEvent(final RecyclerView view, MotionEvent e) {
        if (gestureDetector == null) {
            gestureDetector = new GestureDetector(view.getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View childView = view.findChildViewUnder(e.getX(), e.getY());
                    if (childView != null) {
                        int position = view.getChildAdapterPosition(childView);
                        ViewHolder viewHolder = (ViewHolder) view.getChildViewHolder(childView);
                        onItemLongClick(viewHolder, view, position, e.getX(), e.getY());
                    }
                }
            });
            gestureDetector.setIsLongpressEnabled(true);
        }

        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && gestureDetector.onTouchEvent(e)) {
            int position = view.getChildAdapterPosition(childView);
            ViewHolder viewHolder = (ViewHolder) view.getChildViewHolder(childView);
            onItemClick(viewHolder, view, position, e.getX(), e.getY());
        }

        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
    }

    // endregion Inherited Methods

    // region Abstract Methods

    /**
     * Called when an item in the {@link RecyclerView} is clicked.
     *
     * @param viewHolder The view holder of the clicked item.
     * @param parent     The recycler view which contained the clicked item.
     * @param position   The position in the adapter of the clicked item.
     */
    @Override
    public abstract void onItemClick(ViewHolder viewHolder, RecyclerView parent, int position, float x, float y);

    /**
     * Called when an item in the {@link RecyclerView} is long clicked.
     *
     * @param viewHolder The view holder of the clicked item.
     * @param parent     The recycler view which contained the clicked item.
     * @param position   The position in the adapter of the clicked item.
     */
    public abstract void onItemLongClick(ViewHolder viewHolder, RecyclerView parent, int position, float x, float y);

    // endregion Abstract Methods
}
