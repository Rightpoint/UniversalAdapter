package com.raizlabs.android.universaladapter.widget.adapters.converter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raizlabs.android.coreutils.threading.ThreadingUtils;
import com.raizlabs.android.coreutils.util.observable.lists.ListObserver;
import com.raizlabs.android.coreutils.util.observable.lists.ListObserverListener;
import com.raizlabs.android.coreutils.util.observable.lists.SimpleListObserver;
import com.raizlabs.android.universaladapter.widget.adapters.ViewHolder;
import com.raizlabs.widget.adapters.R;

import java.util.ArrayList;
import java.util.List;

public abstract class UniversalAdapter<Item, Holder extends ViewHolder> {

    private boolean runningTransaction;
    private boolean transactionModified;

    private SimpleListObserver<Item> listObserver;

    private List<ViewHolder> headerHolders = new ArrayList<>();

    private List<ViewHolder> footerHolders = new ArrayList<>();

    private ItemClickedListener<Item, Holder> itemClickedListener;
    private FooterClickedListener footerClickedListener;
    private HeaderClickedListener headerClickedListener;

    public UniversalAdapter() {
        listObserver = new SimpleListObserver<>();
    }

    public ListObserver<Item> getListObserver() {
        return listObserver;
    }

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

    public void setItemClickedListener(ItemClickedListener<Item, Holder> itemClickedListener) {
        this.itemClickedListener = itemClickedListener;
    }

    public void setFooterClickedListener(FooterClickedListener footerClickedListener) {
        this.footerClickedListener = footerClickedListener;
    }

    public void setHeaderClickedListener(HeaderClickedListener headerClickedListener) {
        this.headerClickedListener = headerClickedListener;
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
    void bindViewHolder(ViewHolder viewHolder, int position) {
        if (position < getHeadersCount()) {
            onBindHeaderViewHolder(viewHolder, position);
        } else if (position > getFooterStartIndex()) {
            onBindFooterViewHolder(viewHolder, position - getFooterStartIndex() - 1);
        } else {
            int adjusted = position - getHeadersCount();
            viewHolder.itemView.setTag(R.id.com_raizlabs_viewholderIndexID, adjusted);
            onBindViewHolder((Holder) viewHolder, get(adjusted), adjusted);
        }
    }

    /**
     * Binds a header {@link ViewHolder} for the specified position.
     *
     * @param holder   The holder to bind to
     * @param position The position of the header within the headers list.
     */
    protected void onBindHeaderViewHolder(ViewHolder holder, int position) {

    }

    /**
     * Binds a footer {@link ViewHolder} for the specified position.
     *
     * @param holder   The holder to bind to
     * @param position The position of the footer within the footers list.
     */
    protected void onBindFooterViewHolder(ViewHolder holder, int position) {

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

    int getInternalCount() {
        return getHeadersCount() + getCount() + getFootersCount();
    }

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

    int getItemViewTypeInternal(int position) {
        int viewType;
        if (position < getHeadersCount()) {
            viewType = position;
        } else if (position >= getFooterStartIndex()) {
            viewType = position - getFooterStartIndex() + getHeadersCount() + getItemViewTypeCount() - 1;
        } else {
            viewType = getItemViewType(position - getHeadersCount()) + getHeadersCount();
        }
        return viewType;
    }

    public int getItemViewTypeCount() {
        return 1;
    }

    int getInternalItemViewTypeCount() {
        return getItemViewTypeCount() + getFootersCount() + getHeadersCount();
    }

    public boolean hasStableIds() {
        return false;
    }

    ViewHolder createViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder;
        if (viewType < getHeadersCount()) {
            viewHolder = headerHolders.get(viewType);
        } else if (viewType > (getHeadersCount() + getItemViewTypeCount() - 1)) {
            viewHolder = footerHolders.get(viewType - getHeadersCount() - getItemViewTypeCount());
        } else {
            viewHolder = onCreateViewHolder(parent, viewType - getHeadersCount() + 1);
        }
        viewHolder.itemView.setTag(R.id.com_raizlabs_viewholderTagID, viewHolder);
        return viewHolder;
    }

    protected abstract Holder onCreateViewHolder(ViewGroup parent, int itemType);

    ViewHolder createDropDownViewHolder(ViewGroup parent, int itemType) {
        return onCreateDropDownViewHolder(parent, itemType);
    }

    protected ViewHolder onCreateDropDownViewHolder(ViewGroup parent, int itemType) {
        return onCreateViewHolder(parent, itemType);
    }

    @SuppressWarnings("unchecked")
    void bindDropDownViewHolder(ViewHolder viewHolder, int position) {
        if (position < getHeadersCount()) {
            onBindHeaderViewHolder(viewHolder, position);
        } else if (position > getFooterStartIndex()) {
            onBindFooterViewHolder(viewHolder, position - getFooterStartIndex() - 1);
        } else {
            onBindDropDownViewHolder((Holder) viewHolder, position - getHeadersCount());
        }
    }

    protected void onBindDropDownViewHolder(Holder viewHolder, int position) {
        onBindViewHolder(viewHolder, get(position), position);
    }

    private int getFooterStartIndex() {
        return getHeadersCount() + getCount() - 1;
    }

    /**
     * Adds an arbitrary view to the list of headers, wrapping it in a {@link ViewHolder}
     *
     * @param headerView The view to add to the list of headers.
     */
    public void addHeaderView(View headerView) {
        addHeaderHolder(new ViewHolder(headerView));
    }

    /**
     * Adds a header to this adapter.
     *
     * @param viewHolder The view holder to add as a header to this adapter.
     */
    public void addHeaderHolder(ViewHolder viewHolder) {
        headerHolders.add(viewHolder);
        onItemRangeChanged(getHeadersCount() - 1, 1);
    }

    /**
     * Adds an arbitrary footer view to this adapter.
     *
     * @param footerView The view to add to list of headers.
     */
    public void addFooterView(View footerView) {
        addFooterHolder(new ViewHolder(footerView));
    }

    /**
     * Adds a footer to this adapter.
     *
     * @param viewHolder The viewh holder to add as a footer to this adapter.
     */
    public void addFooterHolder(ViewHolder viewHolder) {
        footerHolders.add(viewHolder);
        onItemRangeChanged(getFooterStartIndex() + getFootersCount() - 1, 1);
    }

    /**
     * @return The count of headers.
     */
    public int getHeadersCount() {
        return headerHolders.size();
    }

    /**
     * @return The count of footers.
     */
    public int getFootersCount() {
        return footerHolders.size();
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

    /**
     * Called when an item is clicked. This consolidates and delegates the call to this adapter. We retrieve the {@link Holder}
     * from the view and call {@link #onItemClicked(int, ViewHolder)}
     *
     * @param position The position of the item in the whole list, including headers and footers
     * @param view     The view that was clicked.
     */
    @SuppressWarnings("unchecked")
    void onItemClicked(int position, View view) {
        ViewHolder holder = (ViewHolder) view.getTag(R.id.com_raizlabs_viewholderTagID);
        onItemClicked(position, holder);
    }

    /**
     * Called when an item is clicked. This consolidates and delegates the call to this adapter.
     *
     * @param position The position of the item in the whole list, including headers and footers
     * @param holder   The holder of the clicked item.
     */
    void onItemClicked(int position, ViewHolder holder) {
        if (isEnabled(position)) {
            if (position < getHeadersCount()) {
                if (footerClickedListener != null) {
                    footerClickedListener.onFooterClicked(this, holder, position);
                }
            } else if (position >= getFooterStartIndex()) {
                if (headerClickedListener != null) {
                    headerClickedListener.onHeaderClicked(this, holder, position - getFooterStartIndex() - 1);
                }
            } else {
                if (itemClickedListener != null) {
                    int adjusted = position - getHeadersCount();
                    itemClickedListener.onItemClicked(this, get(adjusted), (Holder) holder, adjusted);
                }
            }
        }
    }

    /**
     * {@link ListObserverListener} which listens to underlying list changes and calls the appropriate methods.
     */
    protected final ListObserverListener<Item> observableListener = new ListObserverListener<Item>() {

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

}
