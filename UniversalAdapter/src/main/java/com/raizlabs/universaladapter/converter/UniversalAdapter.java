package com.raizlabs.universaladapter.converter;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.raizlabs.coreutils.threading.ThreadingUtils;
import com.raizlabs.coreutils.util.observable.lists.ListObserver;
import com.raizlabs.coreutils.util.observable.lists.ListObserverListener;
import com.raizlabs.coreutils.util.observable.lists.SimpleListObserver;
import com.raizlabs.universaladapter.R;
import com.raizlabs.universaladapter.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * The base adapter for all of the convertible adapters. This is the only list you'll ever need to use. It provides
 * common functionality that not only works with {@link BaseAdapter} and {@link RecyclerView.Adapter}, but also
 * binds to {@link ViewGroup} and {@link ViewPager}.
 * <p/>
 * It also supports an arbitrary number of headers and footer views. This adapter handles positioning, item view types,
 * clicks, and more making it independent of the standard adapter classes.
 *
 * @param <Item>   The uniform item used for the {@link Holder}
 * @param <Holder> The holder for the uniform item type.
 */
public abstract class UniversalAdapter<Item, Holder extends ViewHolder> {

    // region Constants

    private final List<ViewHolder> headerHolders = new ArrayList<>();
    private final List<ViewHolder> footerHolders = new ArrayList<>();

    // endregion Constants

    // region Members

    private boolean runningTransaction;
    private boolean transactionModified;

    private SimpleListObserver<Item> listObserver;

    private ItemClickedListener<Item, Holder> itemClickedListener;
    private FooterClickedListener footerClickedListener;
    private HeaderClickedListener headerClickedListener;

    private ItemLongClickedListener<Item, Holder> itemLongClickedListener;
    private FooterLongClickedListener footerLongClickedListener;
    private HeaderLongClickedListener headerLongClickedListener;

    private boolean isBound;

    // endregion Members

    // region Constructors

    public UniversalAdapter() {
        listObserver = new SimpleListObserver<>();
    }

    // endregion Constructors

    // region Accessors

    public ListObserver<Item> getListObserver() {
        return listObserver;
    }

    ItemClickedListener<Item, Holder> getItemClickedListener() {
        return this.itemClickedListener;
    }

    public void setItemClickedListener(ItemClickedListener<Item, Holder> itemClickedListener) {
        this.itemClickedListener = itemClickedListener;
    }

    ItemLongClickedListener<Item, Holder> getItemLongClickedListener() {
        return this.itemLongClickedListener;
    }

    public void setItemLongClickedListener(ItemLongClickedListener<Item, Holder> itemLongClickedListener) {
        this.itemLongClickedListener = itemLongClickedListener;
    }

    FooterClickedListener getFooterClickedListener() {
        return this.footerClickedListener;
    }

    public void setFooterClickedListener(FooterClickedListener footerClickedListener) {
        this.footerClickedListener = footerClickedListener;
    }

    HeaderClickedListener getHeaderClickedListener() {
        return this.headerClickedListener;
    }

    public void setHeaderClickedListener(HeaderClickedListener headerClickedListener) {
        this.headerClickedListener = headerClickedListener;
    }

    FooterLongClickedListener getFooterLongClickedListener() {
        return this.footerLongClickedListener;
    }

    public void setFooterLongClickedListener(FooterLongClickedListener footerLongClickedListener) {
        this.footerLongClickedListener = footerLongClickedListener;
    }

    HeaderLongClickedListener getHeaderLongClickedListener() {
        return this.headerLongClickedListener;
    }

    public void setHeaderLongClickedListener(HeaderLongClickedListener headerLongClickedListener) {
        this.headerLongClickedListener = headerLongClickedListener;
    }

    /**
     * @param isBound if true, we cannot any longer add header and footer views to this adapter. As well as bind to other adapters.
     */
    void setBound(boolean isBound) {
        this.isBound = true;
    }

    // endregion Accessors

    // region Abstract Methods

    /**
     * @param position The position within the count of items to return. This pretends like there
     *                 are no headers or footers specified. So if you return for {@link #getCount()} as 5 with
     *                 headers and footers, the positions will range from [0,4]
     * @return An item at the specified position.
     */
    public abstract Item get(int position);

    /**
     * @return The count of "normal" items that this adapter contains. Do not take into account headers or footers.
     * @see {@link RecyclerView.Adapter#getItemCount()}
     * @see {@link BaseAdapter#getCount()}
     */
    public abstract int getCount();

    /**
     * Called to create the given view holder with the specific itemType. This is called from {@link #createViewHolder(ViewGroup, int)}.
     *
     * @param parent   The parent that the {@link Holder} uses to for params and context.
     * @param itemType The item type to use.
     * @return A created {@link Holder} with its views already instantiated.
     */
    protected abstract Holder onCreateViewHolder(ViewGroup parent, int itemType);

    /**
     * Called to bind the given view holder with the data from the given
     * item. This is called from
     * {@link #bindViewHolder(ViewHolder, int)} with the appropriate item.
     *
     * @param viewHolder The view holder to populate.
     * @param item       The item whose data to populate into the view holder.
     * @param position   The position of the item in the list.
     */
    protected abstract void onBindViewHolder(Holder viewHolder, Item item, int position);

    /**
     * Called when any observables on this adapter should be notified that the list has changed.
     *
     * @see {@link BaseAdapter#notifyDataSetChanged()}
     * @see {@link RecyclerView.Adapter#notifyDataSetChanged()}
     */
    public abstract void notifyDataSetChanged();

    // endregion Abstract Methods

    // region Instance Methods

    /**
     * Calls {@link #notifyDataSetChangedOnUIThread()} on the UI thread.
     */
    public void notifyDataSetChangedOnUIThread() {
        ThreadingUtils.runOnUIThread(dataSetChangedRunnable);
    }

    /**
     * @param position The position within the item list.
     * @return The id for an item at the specified position. Do not take into account headers and footers.
     * @see {@link BaseAdapter#getItemId(int)}
     * @see {@link RecyclerView.Adapter#getItemId(int)}
     */
    public long getItemId(int position) {
        return 0;
    }

    /**
     * @return true if all are enabled, false if not.
     * @see {@link BaseAdapter#areAllItemsEnabled()}
     */
    public boolean areAllItemsEnabled() {
        return true;
    }

    /**
     * Respects the corresponding {@link ItemClickedListener} and ignores any positions that return false here. This
     * position is within the items list and does not include headers or footers.
     *
     * @param position The position in the item list.
     * @return true if its enabled and responds to item clicks, false if not.
     */
    public boolean isEnabled(int position) {
        return true;
    }

    /**
     * @param position The position within the count of headers.
     * @return true if the header is enabled and responds to clicks.
     */
    public boolean isHeaderEnabled(int position) {
        return true;
    }

    /**
     * @param position The position within the count of footers.
     * @return true if the footer is enabled and responds to clicks.
     */
    public boolean isFooterEnabled(int position) {
        return true;
    }

    /**
     * @param position The position within the items list.
     * @return The item view type for position.
     * @see {@link BaseAdapter#getItemViewType(int)}
     * @see {@link RecyclerView.Adapter#getItemViewType(int)}
     */
    public int getItemViewType(int position) {
        return 0;
    }

    /**
     * @return The count of item types within the items list. Headers and footers are managed automatically, so
     * only return count for the items list.
     */
    public int getItemViewTypeCount() {
        return 1;
    }

    /**
     * @return true if we have stable ids
     * @see {@link BaseAdapter#hasStableIds()}
     * @see {@link RecyclerView.Adapter#hasStableIds()}
     */
    public boolean hasStableIds() {
        return false;
    }

    /**
     * Adds an arbitrary view to the list of headers, wrapping it in a {@link ViewHolder}
     *
     * @param headerView The view to add to the list of headers.
     */
    public void addHeaderView(View headerView) {
        tryThrowAlreadyBoundException(
                "Cannot bind a header view post-bind due to limitations of view types and recycling.");
        addHeaderHolder(new ViewHolder(headerView));
    }

    /**
     * Adds a header to this adapter.
     *
     * @param viewHolder The view holder to add as a header to this adapter.
     */
    public void addHeaderHolder(ViewHolder viewHolder) {
        tryThrowAlreadyBoundException(
                "Cannot bind a header holder post-bind due to limitations of view types and recycling.");
        headerHolders.add(viewHolder);
        onItemRangeInserted(getHeadersCount() - 1, 1);
    }

    /**
     * Adds an arbitrary footer view to this adapter.
     *
     * @param footerView The view to add to list of headers.
     */
    public void addFooterView(View footerView) {
        tryThrowAlreadyBoundException(
                "Cannot bind a footer view post-bind due to limitations of view types and recycling.");
        addFooterHolder(new ViewHolder(footerView));
    }

    /**
     * Adds a footer to this adapter.
     *
     * @param viewHolder The viewh holder to add as a footer to this adapter.
     */
    public void addFooterHolder(ViewHolder viewHolder) {
        tryThrowAlreadyBoundException(
                "Cannot bind a footer holder post-bind due to limitations of view types and recycling.");
        footerHolders.add(viewHolder);
        onItemRangeInserted(getFooterStartIndex() + getFootersCount(), 1);
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
     * @param rawPosition The raw position the binded to {@link ViewGroup} reports.
     * @return The position offset by the headers. This is useful when you respond to a custom view that returns a
     * position that does not take into account the headers of this adapter. For example if you add logic to a ListView
     * that has no knowledge of the headers in this adapter. Call this method to resolve its actual position. It will return
     * a position < 0 if the passed in position is a header or a number larger than the {@link #getCount()} method for a footer.
     */
    public int getAdjustedPosition(int rawPosition) {
        return rawPosition - getHeadersCount();
    }

    /**
     * @param rawPosition The raw position that the binded {@link ViewGroup} reports.
     * @return true if the raw position is a header.
     */
    public boolean isHeaderPosition(int rawPosition) {
        return rawPosition < getHeadersCount();
    }

    /**
     * @param rawPosition The raw position that the binded {@link ViewGroup} reports.
     * @return true if the raw position is a footer.
     */
    public boolean isFooterPosition(int rawPosition) {
        return rawPosition > getFooterStartIndex();
    }

    /**
     * Starts a transaction to only notify our observers only when {@link #endTransaction()} is called. Use this
     * when you need to modify the list in a significant way.
     */
    public void beginTransaction() {
        if (!runningTransaction) {
            runningTransaction = true;
            transactionModified = false;
        } else {
            throw new IllegalStateException("Tried to begin a transaction when one was already running!");
        }
    }

    /**
     * Ends a transaction and notifies the {@link ListObserver}. Only call this after a call to {@link #beginTransaction()}
     */
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
     * If this {@link UniversalAdapter} is already bound, an {@link IllegalStateException} is thrown if you try to add headers or footers
     */
    void checkIfBoundAndSet() {
        if (!isBound) {
            setBound(true);
        }
    }

    /**
     * @param parent   The parent to reference in the {@link ViewHolder}
     * @param viewType The type of holder to return.
     * @return The proper {@link ViewHolder} based on view type. This method takes into account header and footer view
     * holders and properly will adjust this method to either retrieve the header/footer {@link ViewHolder} or call
     * {@link #onCreateViewHolder(ViewGroup, int)} when its a normal holder.
     */
    ViewHolder createViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder;
        if (viewType < getHeadersCount()) {
            viewHolder = headerHolders.get(viewType);
        } else if (viewType > (getHeadersCount() + getItemViewTypeCount() - 1)) {
            viewHolder = footerHolders.get(viewType - getHeadersCount() - getItemViewTypeCount());
        } else {
            viewHolder = onCreateViewHolder(parent, viewType - getHeadersCount());
        }
        viewHolder.itemView.setTag(R.id.com_raizlabs_viewholderTagID, viewHolder);
        return viewHolder;
    }

    /**
     * @param parent   The parent to reference in the {@link ViewHolder}
     * @param viewType The type of holder to return.
     * @return The proper {@link ViewHolder} based on view type. This method takes into account header and footer view
     * holders and properly will adjust this method to either retrieve the header/footer {@link ViewHolder} or call
     * {@link #onCreateDropDownViewHolder(ViewGroup, int)} (ViewGroup, int)} when its a normal holder.
     */
    ViewHolder createDropDownViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder;
        if (viewType < getHeadersCount()) {
            viewHolder = headerHolders.get(viewType);
        } else if (viewType > (getHeadersCount() + getItemViewTypeCount() - 1)) {
            viewHolder = footerHolders.get(viewType - getHeadersCount() - getItemViewTypeCount());
        } else {
            viewHolder = onCreateDropDownViewHolder(parent, viewType - getHeadersCount() + 1);
        }
        viewHolder.itemView.setTag(R.id.com_raizlabs_viewholderTagID, viewHolder);
        return viewHolder;
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
     * Internally binds the drop down view holder and takes into account header and footer view holders.
     *
     * @param viewHolder The viewholder to bind.
     * @param position   The position in the whole list (including headers and footers)
     */
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

    /**
     * @return The actual count of this list. Takes into account items, headers, and footers.
     */
    int getInternalCount() {
        return getHeadersCount() + getCount() + getFootersCount();
    }

    /**
     * @param position The position in the whole list including headers and footers.
     * @return The item view type. Header and footers each will have a unique item type, since they're not uniform.
     */
    int getInternalItemViewType(int position) {
        int viewType;
        if (position < getHeadersCount()) {
            viewType = position;
        } else if (position > getFooterStartIndex()) {
            viewType = position - getFooterStartIndex() + getHeadersCount() + getItemViewTypeCount() - 1;
        } else {
            viewType = getItemViewType(position - getHeadersCount()) + getHeadersCount();
        }
        return viewType;
    }

    /**
     * @return The total count of view types, including header and footers, which for each one added creates a new item type.
     */
    int getInternalItemViewTypeCount() {
        return getItemViewTypeCount() + getFootersCount() + getHeadersCount();
    }

    /**
     * @param position The position in the whole list, including headers and footers.
     * @return true if the view is enabled and clickable.
     */
    boolean internalIsEnabled(int position) {
        if (position < getHeadersCount()) {
            return isHeaderEnabled(position);
        } else if (position > getFooterStartIndex()) {
            return isFooterEnabled(position - getFooterStartIndex() - 1);
        } else {
            return isEnabled(getAdjustedPosition(position));
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
        if (internalIsEnabled(position)) {
            if (position < getHeadersCount()) {
                if (getHeaderClickedListener() != null) {
                    getHeaderClickedListener().onHeaderClicked(this, holder, position);
                }
            } else if (position > getFooterStartIndex()) {
                if (getFooterClickedListener() != null) {
                    getFooterClickedListener().onFooterClicked(this, holder, position - getFooterStartIndex() - 1);
                }
            } else {
                if (getItemClickedListener() != null) {
                    int adjusted = getAdjustedPosition(position);
                    getItemClickedListener().onItemClicked(this, get(adjusted), (Holder) holder, adjusted);
                }
            }
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
    boolean onItemLongClicked(int position, View view) {
        ViewHolder holder = (ViewHolder) view.getTag(R.id.com_raizlabs_viewholderTagID);
        return onItemLongClicked(position, holder);
    }

    /**
     * Called when an item is clicked. This consolidates and delegates the call to this adapter.
     *
     * @param position The position of the item in the whole list, including headers and footers
     * @param holder   The holder of the clicked item.
     */
    boolean onItemLongClicked(int position, ViewHolder holder) {
        if (internalIsEnabled(position)) {
            if (position < getHeadersCount()) {
                if (getHeaderLongClickedListener() != null) {
                    return getHeaderLongClickedListener().onHeaderLongClicked(this, holder, position);
                }
            } else if (position > getFooterStartIndex()) {
                if (getFooterLongClickedListener() != null) {
                    return getFooterLongClickedListener().onFooterLongClicked(this, holder,
                                                                       position - getFooterStartIndex() - 1);
                }
            } else {
                if (getItemLongClickedListener() != null) {
                    int adjusted = getAdjustedPosition(position);
                    return getItemLongClickedListener().onItemLongClicked(this, get(adjusted), (Holder) holder, adjusted);
                }
            }
        }
        return false;
    }

    /**
     * Called when a range of items has changed. This is mostly used for {@link RecyclerViewAdapterConverter}
     *
     * @param startPosition The starting position of the change
     * @param itemCount     The total number of items changed from [startPosition, startPosition+itemCount]
     * @see {@link SimpleListObserver#notifyItemRangeChanged(int, int)}
     */
    protected void onItemRangeChanged(int startPosition, int itemCount) {
        if (tryTransactionModification()) {
            this.listObserver.notifyItemRangeChanged(startPosition, itemCount);
        }
    }

    /**
     * Call when items are added to this adapter.
     *
     * @param startPosition The starting position of the insert.
     * @param itemCount     The total number of items changed from [startPosition, startPosition+itemCount]
     * @see {@link SimpleListObserver#notifyItemRangeInserted(int, int)}
     */
    protected void onItemRangeInserted(int startPosition, int itemCount) {
        if (tryTransactionModification()) {
            this.listObserver.notifyItemRangeInserted(startPosition, itemCount);
        }
    }

    /**
     * Call when items are removed from this adapter.
     *
     * @param startPosition The starting position of the remove.
     * @param itemCount     The total number of items changed from [startPosition, startPosition+itemCount]
     * @see {@link SimpleListObserver#notifyItemRangeRemoved(int, int)}
     */
    protected void onItemRangeRemoved(int startPosition, int itemCount) {
        if (tryTransactionModification()) {
            this.listObserver.notifyItemRangeRemoved(startPosition, itemCount);
        }
    }

    /**
     * Call when items change in no particular range. At most for any adapter used in any sense (except {@link RecyclerViewAdapterConverter},
     * This is called from all other notify methods.
     *
     * @see {@link SimpleListObserver#notifyGenericChange()}
     */
    protected void onGenericChange() {
        if (tryTransactionModification()) {
            this.listObserver.notifyGenericChange();
        }
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
     * Binds a header {@link ViewHolder} for the specified position. Override if you need to do any custom
     * configuration.
     *
     * @param holder   The holder to bind to
     * @param position The position of the header within the headers list.
     */
    protected void onBindHeaderViewHolder(ViewHolder holder, int position) {

    }

    /**
     * Binds a footer {@link ViewHolder} for the specified position. Override if you need to do any custom
     * configuration.
     *
     * @param holder   The holder to bind to
     * @param position The position of the footer within the footers list.
     */
    protected void onBindFooterViewHolder(ViewHolder holder, int position) {

    }

    /**
     * By default, it calls {@link #onCreateViewHolder(ViewGroup, int)}. Override if you need to do something different
     * for drop down views.
     *
     * @param parent   The parent view where the view will eventually be added.
     * @param itemType The item type for the drop down viewholder.
     * @return The drop down view holder
     * @see {@link #onCreateViewHolder(ViewGroup, int)}
     */
    protected ViewHolder onCreateDropDownViewHolder(ViewGroup parent, int itemType) {
        return onCreateViewHolder(parent, itemType);
    }

    /**
     * By default, it calles {@link #onBindViewHolder(ViewHolder, Object, int)}. Override here only if you need to something
     * different for drop down views.
     *
     * @param viewHolder The view holder to bind to a drop down view.
     * @param position   The position of the item in the item list.
     * @see {@link #onBindViewHolder(ViewHolder, Object, int)}
     */
    protected void onBindDropDownViewHolder(Holder viewHolder, int position) {
        onBindViewHolder(viewHolder, get(position), position);
    }

    /**
     * @return The starting index of footer views (if any exist)
     */
    private int getFooterStartIndex() {
        return getHeadersCount() + getCount() - 1;
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

    private void tryThrowAlreadyBoundException(String message) {
        if (isBound) {
            throw new IllegalStateException(message);
        }
    }

    // endregion Instance Methods

    // region Anonymous Classes

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

    /**
     * Runs the {@link #notifyDataSetChanged()} on the UI thread when called.
     */
    private final Runnable dataSetChangedRunnable = new Runnable() {
        @Override
        public void run() {
            notifyDataSetChanged();
        }
    };

    // endregion Anonymous Classes

}
