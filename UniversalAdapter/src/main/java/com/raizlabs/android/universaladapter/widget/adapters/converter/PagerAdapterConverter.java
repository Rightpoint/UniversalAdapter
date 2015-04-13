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
import com.raizlabs.android.universaladapter.widget.adapters.UniversalAdapterUtils;
import com.raizlabs.android.universaladapter.widget.adapters.ViewHolder;

/**
 * Class which dynamically converts a {@link UniversalAdapter} into a
 * {@link PagerAdapter}. This keeps a binding to the
 * {@link UniversalAdapter} so it will be notified of data changes made to the
 * outer adapter.
 *
 * @param <Item>   The type of item that views will represent.
 * @param <Holder> The type of the {@link ViewHolder} that will be used to hold
 *                 views.
 */
public class PagerAdapterConverter<Item, Holder extends ViewHolder>
        extends PagerAdapter implements UniversalConverter<Item, Holder, ViewPager> {

    private UniversalAdapter<Item, Holder> universalAdapter;

    private ItemClickWrapper<Item, Holder> itemClickedWrapper;

    PagerAdapterConverter(UniversalAdapter<Item, Holder> listBasedAdapter, ViewPager viewPager) {
        setAdapter(listBasedAdapter);
        viewPager.setAdapter(this);
        superNotifyDataSetChanged();
        itemClickedWrapper = new ItemClickWrapper<>(this);
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

    @Override
    public UniversalAdapter<Item, Holder> getAdapter() {
        return universalAdapter;
    }

    @Override
    public void setAdapter(@NonNull UniversalAdapter<Item, Holder> listAdapter) {
        if (this.universalAdapter != null) {
            this.universalAdapter.getListObserver().removeListener(internalListObserverListener);
        }
        this.universalAdapter = listAdapter;
        this.universalAdapter.getListObserver().addListener(internalListObserverListener);
    }

    @Override
    public void cleanup() {
        if (this.universalAdapter != null) {
            this.universalAdapter.getListObserver().removeListener(internalListObserverListener);
        }
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
        universalAdapter.notifyDataSetChanged();
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
        ViewHolder holder = universalAdapter.createViewHolder(container, universalAdapter.getItemViewTypeInternal(position));
        universalAdapter.bindViewHolder(holder, position);
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
        return universalAdapter.getInternalCount();
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
