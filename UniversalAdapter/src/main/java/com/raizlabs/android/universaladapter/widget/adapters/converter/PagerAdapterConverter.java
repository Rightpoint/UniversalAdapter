package com.raizlabs.android.universaladapter.widget.adapters.converter;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.raizlabs.android.coreutils.threading.ThreadingUtils;
import com.raizlabs.android.coreutils.util.observable.lists.ListObserver;
import com.raizlabs.android.coreutils.util.observable.lists.ListObserverListener;
import com.raizlabs.android.coreutils.util.observable.lists.SimpleListObserverListener;
import com.raizlabs.android.universaladapter.widget.adapters.ListBasedAdapter;
import com.raizlabs.android.universaladapter.widget.adapters.UniversalAdapterUtils;
import com.raizlabs.android.universaladapter.widget.adapters.ViewHolder;

/**
 * Class which dynamically converts a {@link ListBasedAdapter} into a
 * {@link PagerAdapter}. This keeps a binding to the
 * {@link ListBasedAdapter} so it will be notified of data changes made to the
 * outer adapter.
 *
 * @param <Item>   The type of item that views will represent.
 * @param <Holder> The type of the {@link ViewHolder} that will be used to hold
 *                 views.
 */
public class PagerAdapterConverter<Item, Holder extends ViewHolder>
        extends PagerAdapter implements UniversalConverter<Item, Holder, ViewPager> {

    /**
     * Helper for constructing {@link ViewGroupAdapterConverter}s from
     * {@link ListBasedAdapter}s. Handles generics a little more conveniently
     * than the equivalent constructor.
     *
     * @param adapter   The list adapter to use to populate views.
     * @param viewPager The view pager which will be populated with views.
     * @return An adapter which will populate the view pager via the given
     * adapter.
     */
    public static <Item, Holder extends ViewHolder> PagerAdapterConverter<Item, Holder> from(ListBasedAdapter<Item, Holder> adapter, ViewPager viewPager) {
        return new PagerAdapterConverter<>(adapter, viewPager);
    }

    /**
     * Helper for constructing {@link ViewGroupAdapterConverter}s from
     * {@link ListBasedAdapter}s. Handles generics a little more conveniently
     * than the equivalent constructor.
     *
     * @param adapter The list adapter to use to populate views.
     * @return An adapter which will populate the view pager via the given
     * adapter.
     */
    public static <Item, Holder extends ViewHolder> PagerAdapterConverter<Item, Holder> from(ListBasedAdapter<Item, Holder> adapter) {
        return new PagerAdapterConverter<>(adapter);
    }

    private ListBasedAdapter<Item, Holder> listAdapter;

    private ItemClickWrapper<Item, Holder> itemClickedWrapper;

    private ViewPager viewPager;

    public PagerAdapterConverter(ListBasedAdapter<Item, Holder> listBasedAdapter, ViewPager viewPager) {
        setAdapter(listBasedAdapter);
        register(viewPager);
        itemClickedWrapper = new ItemClickWrapper<>(this);
    }

    public PagerAdapterConverter(ListBasedAdapter<Item, Holder> listBasedAdapter) {
        setAdapter(listBasedAdapter);
        itemClickedWrapper = new ItemClickWrapper<>(this);
    }

    /**
     * Sets the listener to be called when an item is clicked.
     *
     * @param listener The listener to call.
     */
    @Override
    public void setItemClickedListener(ItemClickedListener<Item, Holder> listener) {
        itemClickedWrapper.itemClickedListener = listener;
    }

    @Override
    public ListBasedAdapter<Item, Holder> getListAdapter() {
        return listAdapter;
    }

    @Override
    public ViewPager getViewGroup() {
        return viewPager;
    }

    @Override
    public void setAdapter(@NonNull ListBasedAdapter<Item, Holder> listAdapter) {
        if (this.listAdapter != null) {
            this.listAdapter.getListObserver().removeListener(internalListObserverListener);
        }
        this.listAdapter = listAdapter;
        this.listAdapter.getListObserver().addListener(internalListObserverListener);
    }

    @Override
    public void register(@NonNull ViewPager viewPager) {
        this.viewPager = viewPager;
        viewPager.setAdapter(this);
        superNotifyDataSetChanged();
    }

    @Override
    public void cleanup() {
        if (this.listAdapter != null) {
            this.listAdapter.getListObserver().removeListener(internalListObserverListener);
        }
        this.viewPager = null;
    }

    /**
     * Sets the listener to be called when an item is long clicked.
     *
     * @param listener The listener to call.
     */
    public void setItemLongClickedListener(ItemLongClickedListener<Item, Holder> listener) {
        itemClickedWrapper.itemLongClickedListener = listener;
    }

    @Override
    public void notifyDataSetChanged() {
        listAdapter.notifyDataSetChanged();
    }

    protected void superNotifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    private final Runnable superDataSetChangedRunnable = new Runnable() {
        @Override
        public void run() {
            superNotifyDataSetChanged();
        }
    };

    /**
     * Calls {@link #superNotifyDataSetChanged()} on the UI thread.
     */
    protected void superNotifyDataSetChangedOnUIThread() {
        ThreadingUtils.runOnUIThread(superDataSetChangedRunnable);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Holder holder = listAdapter.createViewHolder(container, listAdapter.getItemViewType(position));
        listAdapter.bindViewHolder(holder, position);
        View view = holder.itemView;
        UniversalAdapterUtils.setViewHolder(view, holder);
        itemClickedWrapper.register(view);
        container.addView(view);
        return holder.itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return listAdapter.getCount();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    private final ListObserverListener<Item> internalListObserverListener = new SimpleListObserverListener<Item>() {
        @Override
        public void onGenericChange(ListObserver<Item> listObserver) {
            superNotifyDataSetChangedOnUIThread();
        }
    };

}
