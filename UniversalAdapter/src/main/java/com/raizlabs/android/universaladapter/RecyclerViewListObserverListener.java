package com.raizlabs.android.universaladapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;

import com.raizlabs.android.coreutils.threading.ThreadingUtils;
import com.raizlabs.android.coreutils.util.observable.lists.ListObserver;
import com.raizlabs.android.coreutils.util.observable.lists.ListObserverListener;

/**
 * Helper class which delegates {@link ListObserverListener} calls back to a
 * {@link Adapter}.
 *
 * @param <Item> The item type of the {@link ListObserverListener}.
 */
public class RecyclerViewListObserverListener<Item> implements ListObserverListener<Item> {

    private RecyclerView.Adapter<?> adapter;

    /**
     * Constructs a new listener which will delegate its calls back to the
     * given adapter.
     *
     * @param adapter The adapter to send calls back to.
     */
    public RecyclerViewListObserverListener(RecyclerView.Adapter<?> adapter) {
        this.adapter = adapter;
    }

    @Override
    public void onItemRangeChanged(ListObserver<Item> observer, final int startPosition, final int itemCount) {
        ThreadingUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyItemRangeChanged(startPosition, itemCount);
            }
        });
    }

    @Override
    public void onItemRangeInserted(ListObserver<Item> observer, final int startPosition, final int itemCount) {
        ThreadingUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyItemRangeInserted(startPosition, itemCount);
            }
        });
    }

    @Override
    public void onItemRangeRemoved(ListObserver<Item> observer, final int startPosition, final int itemCount) {
        ThreadingUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyItemRangeRemoved(startPosition, itemCount);
            }
        });
    }

    @Override
    public void onGenericChange(ListObserver<Item> observer) {
        ThreadingUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

}
