package com.raizlabs.android.universaladapter.widget.adapters.converter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raizlabs.android.coreutils.threading.ThreadingUtils;
import com.raizlabs.android.coreutils.util.observable.lists.ListObserver;
import com.raizlabs.android.coreutils.util.observable.lists.ListObserverListener;
import com.raizlabs.android.coreutils.util.observable.lists.SimpleListObserver;
import com.raizlabs.android.universaladapter.widget.adapters.ViewHolder;

public abstract class UniversalAdapter<Item, Holder extends ViewHolder> {

    private boolean runningTransaction;
    private boolean transactionModified;

    private SimpleListObserver<Item> listObserver;

    public UniversalAdapter() {
        listObserver = new SimpleListObserver<>();
    }

    public ListObserver<Item> getListObserver() {
        return listObserver;
    }

    /**
     * {@link ListObserverListener} which listens to underlying list changes and calls the appropriate methods.
     */
    protected ListObserverListener<Item> observableListener = new ListObserverListener<Item>() {

        @Override
        public void onItemRangeChanged(ListObserver<Item> observer, int startPosition, int itemCount) {
            UniversalAdapter.this.onItemRangeChanged(startPosition, itemCount);
        }

        @Override
        public void onItemRangeInserted(ListObserver<Item> observer, int startPosition, int itemCount) {
            UniversalAdapter.this.onItemRangeInserted(startPosition, itemCount);
        }

        @Override
        public void onItemRangeRemoved(ListObserver<Item> observer, int startPosition, int itemCount) {
            UniversalAdapter.this.onItemRangeRemoved(startPosition, itemCount);
        }

        @Override
        public void onGenericChange(ListObserver<Item> observer) {
            UniversalAdapter.this.onGenericChange();
        }
    };

    public abstract void notifyDataSetChanged();

    private final Runnable dataSetChangedRunnable = new Runnable() {
        @Override
        public void run() {
            notifyDataSetChanged();
        }
    };

    protected void onItemRangeChanged(int startPosition, int itemCount) {
        if (tryTransactionModification()) {
            this.listObserver.notifyItemRangeChanged(startPosition, itemCount);
        }
    }

    protected void onItemRangeInserted(int startPosition, int itemCount) {
        if (tryTransactionModification()) {
            this.listObserver.notifyItemRangeInserted(startPosition, itemCount);
        }
    }

    protected void onItemRangeRemoved(int startPosition, int itemCount) {
        if (tryTransactionModification()) {
            this.listObserver.notifyItemRangeRemoved(startPosition, itemCount);
        }
    }

    protected void onGenericChange() {
        if (tryTransactionModification()) {
            this.listObserver.notifyGenericChange();
        }
    }

    /**
     * Calls {@link #notifyDataSetChangedOnUIThread()} on the UI thread.
     */
    public void notifyDataSetChangedOnUIThread() {
        ThreadingUtils.runOnUIThread(dataSetChangedRunnable);
    }

    public long getItemId(int position) {
        return 0;
    }

    /**
     * Inflates the given layout resource using the context of the given parent.
     * Does not add the resources to the parent.
     *
     * @param parent      A parent view where the view will eventually be added.
     * @param layoutResId The layout resource ID to inflate.
     * @return The inflated view.
     */
    protected View inflateView(ViewGroup parent, int layoutResId) {
        return LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
    }

    /**
     * Call to populate the given view holder with the data at the given
     * position in the list.
     *
     * @param viewHolder The view holder to populate.
     * @param position   The position of the data in the list.
     */
    public void bindViewHolder(Holder viewHolder, int position) {
        onBindViewHolder(viewHolder, get(position), position);
    }

    /**
     * Called to populate the given view holder with the data from the given
     * item. By default, this is called from
     * {@link #bindViewHolder(ViewHolder, int)} with the appropriate item.
     *
     * @param viewHolder The view holder to populate.
     * @param item       The item whose data to populate into the view holder.
     * @param position   The position of the item in the list.
     */
    protected abstract void onBindViewHolder(Holder viewHolder, Item item, int position);

    public abstract int getCount();

    public abstract Item get(int position);

    public boolean areAllItemsEnabled() {
        return true;
    }

    public boolean isEnabled(int position) {
        return true;
    }

    public int getItemViewType(int position) {
        return 0;
    }

    public int getItemViewTypeCount() {
        return 1;
    }

    public boolean hasStableIds() {
        return false;
    }

    public Holder createViewHolder(ViewGroup parent, int itemType) {
        return onCreateViewHolder(parent, itemType);
    }

    protected abstract Holder onCreateViewHolder(ViewGroup parent, int itemType);

    public Holder createDropDownViewHolder(ViewGroup parent, int itemType) {
        return onCreateDropDownViewHolder(parent, itemType);
    }

    protected Holder onCreateDropDownViewHolder(ViewGroup parent, int itemType) {
        return onCreateViewHolder(parent, itemType);
    }

    public void bindDropDownViewHolder(Holder viewHolder, int position) {
        onBindDropDownViewHolder(viewHolder, position);
    }

    protected void onBindDropDownViewHolder(Holder viewHolder, int position) {
        onBindViewHolder(viewHolder, get(position), position);
    }

    /**
     * Records a modification attempt to any currently running transaction and
     * returns whether the change should notify listeners.
     *
     * @return True if the modification should notify listeners, false if it
     * should not.
     */
    private boolean tryTransactionModification() {
        if (runningTransaction) {
            transactionModified = true;
            return false;
        }
        return true;
    }

    public void beginTransaction() {
        if (!runningTransaction) {
            runningTransaction = true;
            transactionModified = false;
        } else {
            throw new IllegalStateException("Tried to begin a transaction when one was already running!");
        }
    }

    public void endTransaction() {
        if (runningTransaction) {
            runningTransaction = false;
            if (transactionModified) {
                onGenericChange();
            }
        } else {
            throw new IllegalStateException("Tried to end a transaction when no transaction was running!");
        }
    }

}
