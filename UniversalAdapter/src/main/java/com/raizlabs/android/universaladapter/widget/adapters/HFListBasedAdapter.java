package com.raizlabs.android.universaladapter.widget.adapters;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: An extension on {@link ListBasedAdapter} that adds ability for an artibitrary header
 * and footer views. Provides a different set of methods to implement.
 * <p/>
 * <p></p>
 * <p/>
 * We have adjusted each "normal" method from {@link ListBasedAdapter} that deals with the each
 * adapter to take into account header and footer views. So do not override the "normal" methods.
 */
public abstract class HFListBasedAdapter<Item, Holder extends ViewHolder> extends ListBasedAdapter<Object, ViewHolder> {

    private List<ViewHolder> headerHolders = new ArrayList<>();

    private List<ViewHolder> footerHolders = new ArrayList<>();

    @SuppressWarnings("unchecked")
    @Override
    protected void onBindViewHolder(ViewHolder viewHolder, Object item, int position) {
        if (position < getHeadersCount()) {
            onBindHeaderViewHolder(viewHolder, position);
        } else if (position > getFooterStartIndex()) {
            onBindFooterViewHolder(viewHolder, position - getFooterStartIndex() - 1);
        } else {
            onBindItemViewHolder((Holder) viewHolder, (Item) item, position - getHeadersCount());
        }
    }

    @Override
    protected ViewHolder onCreateViewHolder(ViewGroup parent, int itemType) {
        return getViewHolderForType(parent, itemType);
    }

    /**
     * This method should return the ViewHolder for an {@link Item}
     *
     * @param parent   The parent to bind to
     * @param itemType The item type to use.
     * @return A new {@link Holder}
     */
    protected abstract Holder onCreateItemViewHolder(ViewGroup parent, int itemType);

    /**
     * This method should bind the {@link Item} to the {@link Holder}
     *
     * @param holder   The holder to bind to
     * @param item     The item to use.
     * @param position The adjusted position between the header and footers count.
     */
    protected abstract void onBindItemViewHolder(Holder holder, Item item, int position);

    /**
     * Binds a header {@link ViewHolder} for the specified position.
     *
     * @param holder   The holder to bind to
     * @param position The position of the header within the headers list.
     */
    protected abstract void onBindHeaderViewHolder(ViewHolder holder, int position);

    /**
     * Binds a footer {@link ViewHolder} for the specified position.
     *
     * @param holder   The holder to bind to
     * @param position The position of the footer within the footers list.
     */
    protected abstract void onBindFooterViewHolder(ViewHolder holder, int position);

    /**
     * @return The item view type count for the underlying {@link ListBasedAdapter}. Do
     * not take into account the count of headers and footers.
     */
    protected int getListItemViewTypeCount() {
        return 1;
    }

    /**
     * @param position The position within the contained {@link ListBasedAdapter} and adjusted
     *                 for the headers.
     * @return The normal list item view type you return in {@link #getItemViewType(int)}
     */
    protected int getListItemViewType(int position) {
        return 0;
    }

    @Override
    public int getCount() {
        return super.getCount() + getHeadersCount() + getFootersCount();
    }

    @Override
    public Object get(int position) {
        return getItem(position);
    }

    private ViewHolder getViewHolderForType(ViewGroup parent, int viewType) {
        ViewHolder viewHolder;
        if (viewType < getHeadersCount()) {
            viewHolder = headerHolders.get(viewType);
        } else if (viewType > (getHeadersCount() + getListItemViewTypeCount() - 1)) {
            viewHolder = footerHolders.get(viewType - getHeadersCount() - getListItemViewTypeCount());
        } else {
            viewHolder = onCreateItemViewHolder(parent, viewType - getHeadersCount() + 1);
        }
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        int viewType;
        if (position < getHeadersCount()) {
            viewType = position;
        } else if (position >= getFooterStartIndex()) {
            viewType = position - getFooterStartIndex() + getHeadersCount() + getListItemViewTypeCount() - 1;
        } else {
            viewType = getListItemViewType(position - getHeadersCount()) + getHeadersCount();
        }
        return viewType;
    }

    @Override
    public int getItemViewTypeCount() {
        return getListItemViewTypeCount() + getFootersCount() + getHeadersCount();
    }

    @Override
    public Object getItem(int position) {
        if (position < getHeadersCount() || position > getFooterStartIndex()) {
            return null;
        } else {
            return super.getItem(position - getHeadersCount());
        }
    }

    private int getFooterStartIndex() {
        return getHeadersCount() + getItemsList().size() - 1;
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
}
