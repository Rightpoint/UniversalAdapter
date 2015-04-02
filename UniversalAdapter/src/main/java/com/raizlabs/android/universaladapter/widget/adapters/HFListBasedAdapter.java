package com.raizlabs.android.universaladapter.widget.adapters;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: An extension on {@link ListBasedAdapter} that adds ability for header
 * and footer views. Provides a different set of methods to implement.
 */
public abstract class HFListBasedAdapter<Item, Holder extends ViewHolder> extends ListBasedAdapter<Object, ViewHolder> {

    private List<ViewHolder> headerHolders = new ArrayList<>();

    private List<ViewHolder> footerHolders = new ArrayList<>();

    private final Class<Holder> holderType;

    public HFListBasedAdapter(Class<Holder> holderType) {
        this.holderType = holderType;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView != null) {
            viewHolder = getViewHolder(convertView);
        }

        if (viewHolder == null) {
            int viewType = getItemViewType(position);
            viewHolder = createViewHolder(position, parent, viewType);
            setViewHolder(viewHolder.itemView, viewHolder);
        }

        // fix view holder if wrong type.
        if (!getViewHolderType(position).equals(viewHolder.getClass())) {
            int viewType = getItemViewType(position);
            viewHolder = createViewHolder(position, parent, viewType);
            setViewHolder(viewHolder.itemView, viewHolder);
        }

        bindViewHolder(viewHolder, position);

        return viewHolder.itemView;
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView != null) {
            viewHolder = getViewHolder(convertView);
        }

        if (viewHolder == null) {
            int viewType = getItemViewType(position);
            viewHolder = createDropDownViewHolder(position, parent, viewType);
            setViewHolder(viewHolder.itemView, viewHolder);
        }

        // fix view holder if wrong type.
        if (!getViewHolderType(position).equals(viewHolder.getClass())) {
            int viewType = getItemViewType(position);
            viewHolder = createViewHolder(position, parent, viewType);
            setViewHolder(viewHolder.itemView, viewHolder);
        }

        bindDropDownViewHolder(viewHolder, position);

        return viewHolder.itemView;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onBindViewHolder(ViewHolder viewHolder, Object item, int position) {
        if (position < getHeadersCount()) {
            onBindHeaderViewHolder(viewHolder, position);
        } else if (position >= getFooterStart()) {
            onBindFooterViewHolder(viewHolder, position - getHeadersCount() - getItemsList().size());
        } else {
            onBindItemViewHolder((Holder) viewHolder, (Item) item, position - getHeadersCount());
        }
    }

    @Override
    protected ViewHolder onCreateViewHolder(int position, ViewGroup parent, int itemType) {
        if (position < getHeadersCount()) {
            return headerHolders.get(position);
        } else if (position >= getFooterStart()) {
            return footerHolders.get(position - getFooterStart());
        } else {
            return onCreateItemViewHolder(parent, itemType);
        }
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

    @Override
    public int getItemViewType(int position) {
        if (position < getHeadersCount()) {
            return position;
        } else if (position >= getFooterStart()) {
            return position - getFooterStart();
        } else {
            return super.getItemViewType(position - getHeadersCount());
        }
    }

    @Override
    public int getItemViewTypeCount() {
        return getListItemViewTypeCount() + getFootersCount() + getHeadersCount();
    }

    @Override
    public Object getItem(int position) {
        if (position < getHeadersCount() || position >= getFooterStart()) {
            return null;
        } else {
            return super.getItem(position - getHeadersCount());
        }
    }

    private int getFooterStart() {
        return getHeadersCount() + getItemsList().size();
    }

    private Class<?> getViewHolderType(int position) {
        if (position < getHeadersCount()) {
            return headerHolders.get(position).getClass();
        } else if (position >= getFooterStart()) {
            return footerHolders.get(position - getFooterStart()).getClass();
        } else {
            return holderType;
        }
    }

    /**
     * Adds a header to this adapter.
     *
     * @param viewHolder The view holder to add as a header to this adapter.
     */
    public void addHeaderHolder(ViewHolder viewHolder) {
        headerHolders.add(viewHolder);
    }

    /**
     * Adds a footer to this adapter.
     *
     * @param viewHolder The viewh holder to add as a footer to this adapter.
     */
    public void addFooterHolder(ViewHolder viewHolder) {
        footerHolders.add(viewHolder);
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
