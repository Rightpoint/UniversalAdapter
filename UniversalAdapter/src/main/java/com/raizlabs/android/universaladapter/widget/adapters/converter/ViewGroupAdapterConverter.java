package com.raizlabs.android.universaladapter.widget.adapters.converter;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;

import com.raizlabs.android.coreutils.util.observable.lists.ListObserver;
import com.raizlabs.android.coreutils.util.observable.lists.ListObserverListener;
import com.raizlabs.android.coreutils.util.observable.lists.SimpleListObserverListener;
import com.raizlabs.android.universaladapter.widget.adapters.ListBasedAdapter;
import com.raizlabs.android.universaladapter.widget.adapters.ViewHolder;
import com.raizlabs.android.universaladapter.widget.adapters.viewholderstrategy.ViewHolderStrategyUtils;

/**
 * Class which uses a {@link ListBasedAdapter} to display a set of views in a
 * {@link ViewGroup}. This class keeps a reference to the {@link ViewGroup} and
 * lives inside the {@link ListBasedAdapter}, so you should call
 * {@link #cleanup()} when you are done with it or the {@link ViewGroup} will
 * be detached.
 *
 * @param <Item>   The type of the item that views will represent.
 * @param <Holder> The type of the {@link ViewHolder} that will be used to hold
 *                 views.
 */
public class ViewGroupAdapterConverter<Item, Holder extends ViewHolder> {

    /**
     * Helper for constructing {@link ViewGroupAdapterConverter}s from
     * {@link ListBasedAdapter}s. Handles generics a little more conveniently
     * than the equivalent constructor.
     *
     * @param adapter   The list adapter to use to populate views.
     * @param viewGroup The view group which will be populated with views.
     * @return An adapter which will populate the view group via the given
     * adapter.
     */
    public static <Item, Holder extends ViewHolder> ViewGroupAdapterConverter<Item, Holder> from(ListBasedAdapter<Item, Holder> adapter, ViewGroup viewGroup) {
        return new ViewGroupAdapterConverter<Item, Holder>(viewGroup, adapter);
    }

    private ViewGroup viewGroup;

    /**
     * @return The {@link ViewGroup} that this adapter populates.
     */
    public ViewGroup getViewGroup() {
        return viewGroup;
    }

    private ListBasedAdapter<Item, Holder> listAdapter;

    /**
     * @return The {@link ListBasedAdapter} that this adapter uses to populate
     * the view group.
     */
    public ListBasedAdapter<Item, Holder> getListAdapter() {
        return listAdapter;
    }

    private ItemClickedListener<Item, Holder> itemClickedListener;
    private ItemLongClickedListener<Item, Holder> itemLongClickedListener;

    /**
     * Sets the listener to be called when an item is clicked.
     *
     * @param listener The listener to call.
     */
    public void setItemClickedListener(ItemClickedListener<Item, Holder> listener) {
        this.itemClickedListener = listener;
    }

    /**
     * Sets the listener to be called when an item is long clicked.
     *
     * @param listener The listener to call.
     */
    public void setItemLongClickedListener(ItemLongClickedListener<Item, Holder> listener) {
        this.itemLongClickedListener = listener;
    }

    /**
     * Sets the adapter to use to populate the {@link ViewGroup} and laods
     * the current data.
     *
     * @param adapter The adapter to use to populate the view group.
     */
    public void setAdapter(ListBasedAdapter<Item, Holder> adapter) {
        if (this.listAdapter != null) {
            this.listAdapter.getListObserver().removeListener(listChangeListener);
        }

        this.listAdapter = adapter;

        if (this.listAdapter != null) {
            this.listAdapter.getListObserver().addListener(listChangeListener);
        }

        populateAll();
    }

    /**
     * Constructs a new adapter bound to the given {@link ViewGroup}, but binds
     * no data.
     *
     * @param viewGroup The view group which will be populated with views.
     * @see #setAdapter(ListBasedAdapter)
     */
    public ViewGroupAdapterConverter(ViewGroup viewGroup) {
        if (viewGroup == null)
            throw new IllegalArgumentException("ViewGroup may not be null.");

        this.viewGroup = viewGroup;
    }

    /**
     * Constructs a new adapter bound to the given {@link ViewGroup}, and binds
     * the given adapter.
     *
     * @param viewGroup The view group which will be populated with views.
     * @param adapter   The list adapter to use to populate views.
     */
    public ViewGroupAdapterConverter(ViewGroup viewGroup, ListBasedAdapter<Item, Holder> adapter) {
        this(viewGroup);
        setAdapter(adapter);
    }

    /**
     * Cleans up this adapter and disconnects it from the {@link ViewGroup} and
     * {@link ListBasedAdapter}.
     */
    public void cleanup() {
        setAdapter(null);
        this.viewGroup = null;
    }

    private void clear() {
        viewGroup.removeAllViews();
    }

    private void populateAll() {
        clear();

        onPrePopulate(viewGroup);

        if (listAdapter != null) {
            final int count = listAdapter.getCount();
            for (int i = 0; i < count; i++) {
                addItem(i);
            }
        }

        onPostPopulate(viewGroup);
    }

    /**
     * Useful when adding header views outside of the adapter to the underlying {@link ViewGroup}
     *
     * @param viewGroup The view group that this adapter loads into.
     */
    protected void onPrePopulate(ViewGroup viewGroup) {

    }

    /**
     * Useful to know when the {@link ViewGroup} is done populating. Its useful for adding footer
     * views to the underlying {@link ViewGroup}
     *
     * @param viewGroup The view group that this adapter loads into.
     */
    protected void onPostPopulate(ViewGroup viewGroup) {

    }

    private void addItem(int index) {
        Holder holder = listAdapter.createViewHolder(getViewGroup(), listAdapter.getItemViewType(index));
        listAdapter.bindViewHolder(holder, index);

        View view = holder.itemView;
        ViewHolderStrategyUtils.setViewHolder(view, holder);
        view.setOnClickListener(internalItemClickListener);
        view.setOnLongClickListener(internalItemLongClickListener);

        getViewGroup().addView(view, index);
    }

    private ListObserverListener<Item> listChangeListener = new SimpleListObserverListener<Item>() {
        @Override
        public void onGenericChange(ListObserver<Item> observer) {
            populateAll();
        }
    };

    private OnClickListener internalItemClickListener = new OnClickListener() {
        @SuppressWarnings("unchecked")
        @Override
        public void onClick(View v) {
            if (itemClickedListener != null) {
                int index = getViewGroup().indexOfChild(v);
                Item item = listAdapter.get(index);
                Holder holder = (Holder) ViewHolderStrategyUtils.getViewHolder(v);
                itemClickedListener.onItemClicked(getListAdapter(), item, holder, index);
            }
        }
    };

    private OnLongClickListener internalItemLongClickListener = new OnLongClickListener() {

        @SuppressWarnings("unchecked")
        @Override
        public boolean onLongClick(View v) {
            if (itemLongClickedListener != null) {
                int index = getViewGroup().indexOfChild(v);
                Item item = listAdapter.get(index);
                Holder holder = (Holder) ViewHolderStrategyUtils.getViewHolder(v);
                return itemLongClickedListener.onItemLongClicked(getListAdapter(), item, holder, index);
            }
            return false;
        }
    };
}
