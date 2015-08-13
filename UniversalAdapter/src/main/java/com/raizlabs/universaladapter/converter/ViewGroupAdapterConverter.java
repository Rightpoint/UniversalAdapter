package com.raizlabs.universaladapter.converter;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.raizlabs.coreutils.util.observable.lists.ListObserver;
import com.raizlabs.coreutils.util.observable.lists.ListObserverListener;
import com.raizlabs.coreutils.util.observable.lists.SimpleListObserverListener;
import com.raizlabs.universaladapter.UniversalAdapterUtils;
import com.raizlabs.universaladapter.ViewHolder;

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
public class ViewGroupAdapterConverter<Item, Holder extends ViewHolder> implements UniversalConverter<Item, Holder> {

    // region Members

    private ViewGroup viewGroup;
    private UniversalAdapter<Item, Holder> universalAdapter;
    private ItemClickWrapper<Item, Holder> itemClickWrapper;

    // endregion Members

    /**
     * Constructs a new adapter bound to the given {@link ViewGroup}, and binds
     * the given adapter.
     *
     * @param viewGroup The view group which will be populated with views.
     * @param adapter   The list adapter to use to populate views.
     */
    public ViewGroupAdapterConverter(@NonNull
    UniversalAdapter<Item, Holder> adapter, @NonNull
    ViewGroup viewGroup) {
        adapter.checkIfBoundAndSet();
        setAdapter(adapter);
        this.viewGroup = viewGroup;
        itemClickWrapper = new ItemClickWrapper<>(this);
        populateAll();
    }

    // region Accessors

    /**
     * @return The {@link ViewGroup} that this adapter populates.
     */
    public ViewGroup getViewGroup() {
        return viewGroup;
    }

    // endregion Accessors

    // region Inherited Methods

    @Override
    public UniversalAdapter<Item, Holder> getAdapter() {
        return universalAdapter;
    }

    @Override
    public void setItemClickedListener(ItemClickedListener<Item, Holder> listener) {
        getAdapter().setItemClickedListener(listener);
    }

    @Override
    public void setItemLongClickedListener(ItemLongClickedListener<Item, Holder> listener) {
        getAdapter().setItemLongClickedListener(listener);
    }

    @Override
    public void setHeaderClickedListener(HeaderClickedListener headerClickedListener) {
        getAdapter().setHeaderClickedListener(headerClickedListener);
    }

    @Override
    public void setFooterClickedListener(FooterClickedListener footerClickedListener) {
        getAdapter().setFooterClickedListener(footerClickedListener);
    }

    @Override
    public void setHeaderLongClickedListener(HeaderLongClickListener headerLongClickedListener) {
        getAdapter().setHeaderLongClickListener(headerLongClickedListener);
    }

    @Override
    public void setFooterLongClickedListener(FooterLongClickedListener footerLongClickedListener) {
        getAdapter().setFooterLongClickedListener(footerLongClickedListener);
    }

    @Override
    public void setAdapter(@NonNull
    UniversalAdapter<Item, Holder> adapter) {
        if (getAdapter() != null) {
            getAdapter().getListObserver().removeListener(listChangeListener);
        }

        this.universalAdapter = adapter;
        adapter.getListObserver().addListener(listChangeListener);

        populateAll();
    }

    @Override
    public void cleanup() {
        if (getAdapter() != null) {
            getAdapter().getListObserver().removeListener(listChangeListener);
        }
        this.viewGroup = null;
    }

    // endregion Inherited Methods

    // region Instance Methods

    private void clear() {
        viewGroup.removeAllViews();
    }

    private void populateAll() {
        if (viewGroup != null) {
            clear();

            if (getAdapter() != null) {
                final int count = getAdapter().getInternalCount();
                for (int i = 0; i < count; i++) {
                    addItem(i);
                }
            }

        }
    }

    private void addItem(int position) {
        ViewHolder holder = getAdapter().createViewHolder(getViewGroup(),
                                                          universalAdapter.getInternalItemViewType(position));
        getAdapter().bindViewHolder(holder, position);

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

    // endregion Instance Methods

}
