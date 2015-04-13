package com.raizlabs.android.universaladapter.widget.adapters.converter;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.raizlabs.android.coreutils.util.observable.lists.ListObserver;
import com.raizlabs.android.coreutils.util.observable.lists.ListObserverListener;
import com.raizlabs.android.coreutils.util.observable.lists.SimpleListObserverListener;
import com.raizlabs.android.universaladapter.widget.adapters.UniversalAdapterUtils;
import com.raizlabs.android.universaladapter.widget.adapters.ViewHolder;

/**
 * Class which uses a {@link UniversalAdapter} to display a set of views in a
 * {@link ViewGroup}. This class keeps a reference to the {@link ViewGroup} and
 * lives inside the {@link UniversalAdapter}, so you should call
 * {@link #cleanup()} when you are done with it or the {@link ViewGroup} will
 * be detached.
 *
 * @param <Item>   The type of the item that views will represent.
 * @param <Holder> The type of the {@link ViewHolder} that will be used to hold
 *                 views.
 */
public class ViewGroupAdapterConverter<Item, Holder extends ViewHolder> implements UniversalConverter<Item, Holder, ViewGroup> {

    private ViewGroup viewGroup;

    /**
     * @return The {@link ViewGroup} that this adapter populates.
     */
    public ViewGroup getViewGroup() {
        return viewGroup;
    }

    private UniversalAdapter<Item, Holder> universalAdapter;

    private ItemClickWrapper<Item, Holder> itemClickWrapper;

    /**
     * Constructs a new adapter bound to the given {@link ViewGroup}, and binds
     * the given adapter.
     *
     * @param viewGroup The view group which will be populated with views.
     * @param adapter   The list adapter to use to populate views.
     */
    ViewGroupAdapterConverter(@NonNull UniversalAdapter<Item, Holder> adapter, @NonNull ViewGroup viewGroup) {
        setAdapter(adapter);
        this.viewGroup = viewGroup;
        populateAll();
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
     * @return The {@link UniversalAdapter} that this adapter uses to populate
     * the view group.
     */
    @Override
    public UniversalAdapter<Item, Holder> getAdapter() {
        return universalAdapter;
    }

    /**
     * Sets the listener to be called when an item is clicked.
     *
     * @param listener The listener to call.
     */
    @Override
    public void setItemClickedListener(ItemClickedListener<Item, Holder> listener) {
        getAdapter().setItemClickedListener(listener);
    }

    /**
     * Sets the adapter to use to populate the {@link ViewGroup} and loads
     * the current data.
     *
     * @param adapter The adapter to use to populate the view group.
     */
    @Override
    public void setAdapter(@NonNull UniversalAdapter<Item, Holder> adapter) {
        if (this.universalAdapter != null) {
            this.universalAdapter.getListObserver().removeListener(listChangeListener);
        }

        this.universalAdapter = adapter;
        this.universalAdapter.getListObserver().addListener(listChangeListener);

        populateAll();
    }

    @Override
    public void cleanup() {
        if (this.universalAdapter != null) {
            this.universalAdapter.getListObserver().removeListener(listChangeListener);
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

            if (universalAdapter != null) {
                final int count = universalAdapter.getInternalCount();
                for (int i = 0; i < count; i++) {
                    addItem(i);
                }
            }

        }
    }

    private void addItem(int position) {
        ViewHolder holder = universalAdapter.createViewHolder(getViewGroup(), universalAdapter.getItemViewTypeInternal(position));
        universalAdapter.bindViewHolder(holder, position);

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
