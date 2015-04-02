package com.raizlabs.android.universaladapter.widget.adapters.converter;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.raizlabs.android.coreutils.util.observable.lists.ListObserver;
import com.raizlabs.android.coreutils.util.observable.lists.ListObserverListener;
import com.raizlabs.android.coreutils.util.observable.lists.SimpleListObserverListener;
import com.raizlabs.android.universaladapter.widget.adapters.ListBasedAdapter;
import com.raizlabs.android.universaladapter.widget.adapters.UniversalAdapterUtils;
import com.raizlabs.android.universaladapter.widget.adapters.ViewHolder;

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
public class ViewGroupAdapterConverter<Item, Holder extends ViewHolder> implements UniversalConverter<Item, Holder, ViewGroup> {

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
        return new ViewGroupAdapterConverter<>(adapter, viewGroup);
    }

    /**
     * Helper for constructing {@link ViewGroupAdapterConverter}s from
     * {@link ListBasedAdapter}s. Handles generics a little more conveniently
     * than the equivalent constructor.
     *
     * @param adapter The list adapter to use to populate views.
     * @return An adapter which will populate the view group via the given
     * adapter.
     */
    public static <Item, Holder extends ViewHolder> ViewGroupAdapterConverter<Item, Holder> from(ListBasedAdapter<Item, Holder> adapter) {
        return new ViewGroupAdapterConverter<>(adapter);
    }

    private ViewGroup viewGroup;

    /**
     * @return The {@link ViewGroup} that this adapter populates.
     */
    public ViewGroup getViewGroup() {
        return viewGroup;
    }

    private ListBasedAdapter<Item, Holder> listAdapter;

    private ItemClickWrapper<Item, Holder> itemClickWrapper;

    /**
     * Constructs a new adapter bound to the given {@link ViewGroup}, and binds
     * the given adapter.
     *
     * @param adapter The list adapter to use to populate views.
     */
    public ViewGroupAdapterConverter(ListBasedAdapter<Item, Holder> adapter) {
        setAdapter(adapter);
        itemClickWrapper = new ItemClickWrapper<>(this);
    }

    /**
     * Constructs a new adapter bound to the given {@link ViewGroup}, and binds
     * the given adapter.
     *
     * @param viewGroup The view group which will be populated with views.
     * @param adapter   The list adapter to use to populate views.
     */
    public ViewGroupAdapterConverter(ListBasedAdapter<Item, Holder> adapter, ViewGroup viewGroup) {
        register(viewGroup);
        setAdapter(adapter);
        itemClickWrapper = new ItemClickWrapper<>(this);
    }

    /**
     * Sets the listener to be called when an item is long clicked.
     *
     * @param listener The listener to call.
     */
    public void setItemLongClickedListener(ItemLongClickedListener<Item, Holder> listener) {
        itemClickWrapper.itemLongClickedListener = listener;
    }

    // region Inherited Methods

    /**
     * @return The {@link ListBasedAdapter} that this adapter uses to populate
     * the view group.
     */
    @Override
    public ListBasedAdapter<Item, Holder> getListAdapter() {
        return listAdapter;
    }

    /**
     * Sets the listener to be called when an item is clicked.
     *
     * @param listener The listener to call.
     */
    @Override
    public void setItemClickedListener(ItemClickedListener<Item, Holder> listener) {
        itemClickWrapper.itemClickedListener = listener;
    }

    /**
     * Sets the adapter to use to populate the {@link ViewGroup} and loads
     * the current data.
     *
     * @param adapter The adapter to use to populate the view group.
     */
    @Override
    public void setAdapter(@NonNull ListBasedAdapter<Item, Holder> adapter) {
        if (this.listAdapter != null) {
            this.listAdapter.getListObserver().removeListener(listChangeListener);
        }

        this.listAdapter = adapter;
        this.listAdapter.getListObserver().addListener(listChangeListener);

        populateAll();
    }

    @Override
    public void register(@NonNull ViewGroup viewGroup) {
        this.viewGroup = viewGroup;
        populateAll();
    }

    @Override
    public void cleanup() {
        if (this.listAdapter != null) {
            this.listAdapter.getListObserver().removeListener(listChangeListener);
        }
        this.viewGroup = null;
    }

    // endregion Inherited Methods

    private void clear() {
        viewGroup.removeAllViews();
    }

    private void populateAll() {
        if (viewGroup != null) {
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

    private void addItem(int position) {
        Holder holder = listAdapter.createViewHolder(getViewGroup(), listAdapter.getItemViewType(position));
        listAdapter.bindViewHolder(holder, position);

        View view = holder.itemView;
        UniversalAdapterUtils.setViewHolder(view, holder);
        itemClickWrapper.register(view);

        getViewGroup().addView(view, position);
    }

    private ListObserverListener<Item> listChangeListener = new SimpleListObserverListener<Item>() {
        @Override
        public void onGenericChange(ListObserver<Item> observer) {
            populateAll();
        }
    };

}
